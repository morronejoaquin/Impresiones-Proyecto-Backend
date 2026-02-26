package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.CartMapper;
import com.example.demo.Model.DTOS.Mappers.OrderItemMapper;
import com.example.demo.Model.DTOS.Request.CartCreateRequest;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.CartHistoryResponse;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Model.DTOS.Response.CartWithItemsResponse;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;
import com.example.demo.Model.Entities.*;
import com.example.demo.Model.Enums.AdminDateFilterType;
import com.example.demo.Model.Enums.CartStatusEnum;
import com.example.demo.Model.Enums.FileTypeEnum;
import com.example.demo.Model.Enums.OrderStatusEnum;
import com.example.demo.Repositories.CartRepository;
import com.example.demo.Repositories.OrderItemRepository;
import com.example.demo.Repositories.PaymentRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Utils.FileMetaData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    private final CartMapper cartMapper;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final PricingService pricingService;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    public CartService(CartMapper cartMapper, CartRepository cartRepository, UserRepository userRepository, OrderItemRepository orderItemRepository, OrderItemMapper orderItemMapper, PricingService pricingService, PaymentRepository paymentRepository, NotificationService notificationService) {
        this.cartMapper = cartMapper;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.pricingService = pricingService;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }

    // Método auxiliar para obtener usuario por email
    private UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    // Crear carrito usando email del usuario autenticado
    public CartResponse save(String email) {
        UserEntity user = getUserByEmail(email);
        verifyCart(user.getId());

        CartEntity entity = new CartEntity();
        entity.setUser(user);
        entity.setTotal(0);
        entity.setCustomer(new CustomerDataEntity(user.getName(), user.getSurname(), user.getEmail(), user.getPhone()));
        entity.setCartStatus(CartStatusEnum.OPEN);
        entity.setStatus(null);

        return cartMapper.toResponse(cartRepository.save(entity));
    }

    public CartResponse save(CartCreateRequest request){
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        verifyCart(user.getId());

        CartEntity entity = cartMapper.toEntity(request);
        entity.setUser(user);
        entity.setTotal(0);
        entity.setCustomer(new CustomerDataEntity(user.getName(), user.getSurname(), user.getEmail(), user.getPhone()));
        entity.setCartStatus(CartStatusEnum.OPEN);
        entity.setStatus(null);

        return cartMapper.toResponse(cartRepository.save(entity));
    }

    public void verifyCart(UUID userId){
        List<CartEntity> carts = cartRepository.findByUser_Id(userId);

        boolean hasOpenCart = carts.stream()
                .anyMatch(cart -> CartStatusEnum.OPEN.equals(cart.getCartStatus()));

        if (hasOpenCart) {
            throw new IllegalStateException("El usuario ya tiene un carrito en uso");
        }
    }

    public CartResponse findById(UUID id){
        return cartMapper.toResponse(cartRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado")));
    }

    public OrderItemResponse agregar(UUID cartId, OrderItemCreateRequest request, String driveFileId, String originalFileName, FileMetaData metadata){

        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        if(cart.getCartStatus() != CartStatusEnum.OPEN){
            throw new IllegalStateException("El carrito no está abierto");
        }

        OrderItemEntity item = orderItemMapper.toEntity(request);
        item.setCart(cart);

        item.setDriveFileId(driveFileId);
        item.setFileName(originalFileName);
        String extension = originalFileName
                .substring(originalFileName.lastIndexOf(".") + 1)
                .toUpperCase();

        item.setFileType(FileTypeEnum.valueOf(extension));

        int pages = metadata.getPages() != null ? metadata.getPages() : 1;

        item.setPages(pages);
        item.setImageWidth(metadata.getImageWidth());
        item.setImageHeight(metadata.getImageHeight());

        double subtotal = pricingService.calcular(item);
        item.setAmount(subtotal);
        item.setDeleted(false);

        cart.getItems().add(item);
        recalcularTotal(cart);

        orderItemRepository.save(item);
        cartRepository.save(cart);

        return orderItemMapper.toResponse(item);
    }

    public CartResponse closeCart(UUID cartId){
        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));
        cart.setCartStatus(CartStatusEnum.IN_PROGRESS);
        cart.setStatus(OrderStatusEnum.PENDING);
        cart.setAdmReceivedAt(Instant.now());
        
        // Copiar datos del usuario al customer del carrito
        if (cart.getUser() != null && cart.getCustomer() == null) {
            CustomerDataEntity customerData = new CustomerDataEntity();
            customerData.setName(cart.getUser().getName());
            customerData.setSurname(cart.getUser().getSurname());
            customerData.setPhone(cart.getUser().getPhone());
            cart.setCustomer(customerData);
        }
        
        return cartMapper.toResponse(cartRepository.save(cart));
    }

    public void eliminarItem(UUID cartId, UUID itemId){

        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        if(cart.getCartStatus() != CartStatusEnum.OPEN){
            throw new IllegalStateException("No se pueden modificar carritos cerrados");
        }

        OrderItemEntity item = orderItemRepository.findByIdAndDeletedFalse(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));

        item.setDeleted(true);
        recalcularTotal(cart);

        orderItemRepository.save(item);
        cartRepository.save(cart);
    }
    
    public Page<CartResponse> findAll(Pageable pageable){
    return cartRepository.findAll(pageable)
            .map(cartMapper::toResponse);
    }

    public Page<CartWithItemsResponse> findAllWithItems(Pageable pageable){
        return cartRepository.findAllByDeletedFalseOrderByCreatedAtDesc(pageable)
                .map(cartMapper::toResponseWithItems);
    }

    public Page<CartResponse> findByStatus(OrderStatusEnum status, Pageable pageable) {
        return cartRepository.findByStatusOrderByAdmReceivedAtAsc(status, pageable)
                .map(cartMapper::toResponse);
    }
    
    public Page<CartResponse> findDeliveredForAdmin(Instant date,AdminDateFilterType dateType,Pageable pageable){
    return cartRepository.findDeliveredForAdmin(OrderStatusEnum.DELIVERED,date,dateType,pageable)
    .map(cartMapper::toResponse);
    }


    public CartWithItemsResponse findOpenCart(UUID userId){
        CartEntity cart = cartRepository.findByUser_IdAndCartStatusAndDeletedFalse(userId, CartStatusEnum.OPEN)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        List<OrderItemEntity> activeItems = cart.getItems().stream()
                .filter(item -> !item.isDeleted())
                .toList();

        double total = 0;

        for (OrderItemEntity item : activeItems){
            if (item.isDeleted()) continue;

            double subtotal = pricingService.calcular(item);
            item.setAmount(subtotal);
            total+=subtotal;
        }

        cart.setTotal(total);

        cartRepository.save(cart);

        cart.setItems(activeItems);

        return cartMapper.toResponseWithItems(cart);
    }

    // Buscar carrito abierto usando email
    public CartWithItemsResponse findOpenCart(String email) {
        UserEntity user = getUserByEmail(email);
        return findOpenCart(user.getId());
    }

    // Obtener entidad de carrito abierto para el usuario (usado por PaymentService)
    public CartEntity getOpenCartForUser(String email) {
        UserEntity user = getUserByEmail(email);
        return cartRepository.findByUser_IdAndCartStatusAndDeletedFalse(user.getId(), CartStatusEnum.OPEN)
                .orElseThrow(() -> new NoSuchElementException("No tienes un carrito activo"));
    }

    // Agregar item al carrito usando email
    public OrderItemResponse agregar(OrderItemCreateRequest request, String driveFileId, String originalFileName, FileMetaData metadata, String email) {
        UserEntity user = getUserByEmail(email);
        
        // Buscar o crear carrito abierto
        CartEntity cart = cartRepository.findByUser_IdAndCartStatusAndDeletedFalse(user.getId(), CartStatusEnum.OPEN)
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setUser(user);
                    newCart.setTotal(0);
                    newCart.setCartStatus(CartStatusEnum.OPEN);
                    newCart.setStatus(null);
                    return cartRepository.save(newCart);
                });

        return agregar(cart.getId(), request, driveFileId, originalFileName, metadata);
    }

    // Eliminar item usando email para validación
    public void eliminarItem(UUID itemId, String email) {
        UserEntity user = getUserByEmail(email);
        OrderItemEntity item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));
        
        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("No tienes permiso para eliminar este item");
        }
        
        item.setDeleted(true);
        orderItemRepository.save(item);
        
        CartEntity cart = item.getCart();
        recalcularTotal(cart);
        cartRepository.save(cart);
    }

    // Obtener pedidos del usuario (carritos no abiertos)
    public Page<CartHistoryResponse> obtenerPedidosDelUsuario(String email, Pageable pageable) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        Page<CartEntity> carritos = cartRepository.findByUser_IdAndStatusNotNullAndDeletedFalseOrderByCreatedAtDesc(
                user.getId(), pageable);

        return carritos.map(cart -> {
            PaymentEntity payment = paymentRepository.findTopByCartIdOrderByOrderDateDesc(cart.getId())
                    .orElse(null);

            return CartHistoryResponse.builder()
                    .cartId(cart.getId())
                    .createdAt(cart.getCreatedAt())
                    .status(cart.getStatus())
                    .total(cart.getTotal())
                    .paymentMethod(payment != null ? payment.getPaymentMethod().name() : "N/A")
                    .paymentStatus(payment != null ? payment.getPaymentStatus().name() : "UNKNOWN")
                    .items(cart.getItems().stream()
                            .map(orderItemMapper::toResponse) // Usa tu mapper actual para los ítems
                            .toList())
                    .build();
        });
    }

    private void recalcularTotal(CartEntity cart){
        cart.setTotal(cart.getItems().stream()
                .filter(i -> !i.isDeleted())
                .mapToDouble(OrderItemEntity::getAmount)
                .sum());
    }

    public void validarFormato(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name == null || !name.matches(".*\\.(pdf|jpg|png)$")) {
            throw new IllegalArgumentException("Formato no permitido");
        }
    }

    public CartWithItemsResponse findWithItems(UUID cartId){

        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        cart.setItems(cart.getItems().stream()
                .filter(item -> !item.isDeleted())
                .toList());

        return cartMapper.toResponseWithItems(cart);
    }

    public CartWithItemsResponse actualizarEstado(UUID cartId, OrderStatusEnum nuevoEstado){

        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        // No podemos cambiar los estados en entregado o cancelado
        if (cart.getStatus() == OrderStatusEnum.DELIVERED ||
            cart.getStatus() == OrderStatusEnum.CANCELLED) {
            throw new IllegalStateException("No se puede modificar un carrito entregado o cancelado");
        }

        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        cart.setStatus(nuevoEstado);

        sincronizarCartStatus(cart); //Lo marcamos para si esta en uso o no el carrito

        if (nuevoEstado == OrderStatusEnum.READY) {
            String message = "¡Tu pedido #" + cart.getId().toString().substring(0,8) + " está listo para retirar!";
            notificationService.createNotification(cart.getUser().getEmail(), message);
        }

        // Setear fecha y hora
        if (nuevoEstado == OrderStatusEnum.DELIVERED) {
            cart.setDeliveredAt(Instant.now());
        } else if (nuevoEstado == OrderStatusEnum.READY){
            cart.setCompletedAt(Instant.now());
        }

        CartEntity actualizado = cartRepository.save(cart);
        return cartMapper.toResponseWithItems(actualizado);
    }

    private void sincronizarCartStatus(CartEntity cart) {

        switch (cart.getStatus()) {
            case PENDING, PRINTING, BINDING -> cart.setCartStatus(CartStatusEnum.IN_PROGRESS);

            case DELIVERED -> cart.setCartStatus(CartStatusEnum.DELIVERED);

            case CANCELLED -> cart.setCartStatus(CartStatusEnum.CANCELLED);

            case READY -> cart.setCartStatus(CartStatusEnum.READY);
        }
    }

    public Page<CartResponse> filterCarts(OrderStatusEnum status, Instant from, Instant to, UUID userId, Pageable pageable){
        return cartRepository.findByFilters(status, from, to, userId, pageable)
                .map(cartMapper::toResponse);
    }

    public List<OrderItemResponse> obtenerOrdenesPorCarrito(UUID carritoId) {
        // Validar que el carrito exista
        if(!cartRepository.existsById(carritoId)){
            throw new NoSuchElementException("Carrito no encontrado");
        }

        return orderItemRepository.findAllByCartIdAndDeletedFalse(carritoId)
                .stream()
                .map(orderItemMapper::toResponse)
                .toList();
    }

    public OrderItemResponse obtenerOrdenEspecificaPorCarrito(UUID carritoId, UUID ordenId) {
        return orderItemRepository.findByIdAndCartIdAndDeletedFalse(ordenId, carritoId)
                .map(orderItemMapper::toResponse)
                .orElseThrow(() -> new NoSuchElementException("La orden no existe o no pertenece a este carrito"));
    }

    public Page<CartResponse> getActiveCartsForAdmin(Pageable pageable) {
        return cartRepository.findAllByStatusInAndDeletedFalseOrderByAdmReceivedAtAsc(
                new java.util.ArrayList<>(java.util.List.of(
                        OrderStatusEnum.PENDING,
                        OrderStatusEnum.PRINTING,
                        OrderStatusEnum.BINDING,
                        OrderStatusEnum.READY
                )),
                pageable
        ).map(cartMapper::toResponse);
    }

    public Page<CartResponse> filterCartsForAdmin(OrderStatusEnum status, String startDate, String endDate, String customerEmail, Pageable pageable) {
        Instant start = null;
        Instant end = null;

        // Convertimos las fechas de String a Instant
        if (startDate != null && !startDate.isBlank()) {
            start = LocalDate.parse(startDate).atStartOfDay(ZoneOffset.UTC).toInstant();
        }
        if (endDate != null && !endDate.isBlank()) {
            end = LocalDate.parse(endDate).atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant();
        }

        return cartRepository.filterCartsForAdmin(status, start, end, customerEmail, pageable)
                .map(cartMapper::toResponse);
    }

    public Page<CartResponse> getDeliveredHistory(String customerEmail, String startDate, String endDate, Pageable pageable) {
        return cartRepository.getDeliveredHistory(customerEmail, startDate, endDate, pageable)
                .map(cartMapper::toResponse);
    }

}
