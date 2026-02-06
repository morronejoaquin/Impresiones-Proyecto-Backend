package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.CartMapper;
import com.example.demo.Model.DTOS.Mappers.OrderItemMapper;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Model.DTOS.Response.CartWithItemsResponse;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;
import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.CustomerDataEntity;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Model.Enums.AdminDateFilterType;
import com.example.demo.Model.Enums.CartStatusEnum;
import com.example.demo.Model.Enums.FileTypeEnum;
import com.example.demo.Model.Enums.OrderStatusEnum;
import com.example.demo.Repositories.CartRepository;
import com.example.demo.Repositories.OrderItemRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Utils.FileMetaData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class CartService {

    private final CartMapper cartMapper;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final PricingService pricingService;

    public CartService(CartMapper cartMapper,
                       CartRepository cartRepository,
                       UserRepository userRepository,
                       OrderItemRepository orderItemRepository,
                       OrderItemMapper orderItemMapper,
                       PricingService pricingService) {

        this.cartMapper = cartMapper;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.pricingService = pricingService;
    }

    public CartResponse save(String email){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        verifyCart(user.getId());

        CartEntity entity = new CartEntity();
        entity.setUser(user);
        entity.setTotal(0);
        entity.setCustomer(new CustomerDataEntity(user.getName(), user.getPhone(), user.getSurname()));
        entity.setCartStatus(CartStatusEnum.OPEN);
        entity.setStatus(null);

        return cartMapper.toResponse(cartRepository.save(entity));
    }

    public void verifyCart(UUID userId){
        List<CartEntity> carts = cartRepository.findByUser_Id(userId);

        for(CartEntity cart : carts){
            if(cart.getCartStatus().equals(CartStatusEnum.OPEN)){
                throw new IllegalStateException("El usuario ya tiene un carrito en uso");
            }
        }
    }

    public CartResponse findById(UUID id){
        return cartMapper.toResponse(cartRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado")));
    }

    public OrderItemResponse agregar(OrderItemCreateRequest request, String driveFileId, String originalFileName, FileMetaData metadata, String email) {

        CartEntity cart = getOpenCartForUser(email);

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

        double total = 0;

        for (OrderItemEntity item : cart.getItems()){
            if (item.isDeleted()) continue;

            double subtotal = pricingService.calcular(item);
            item.setAmount(subtotal);
            total+=subtotal;
            orderItemRepository.save(item);
        }

        cart.setTotal(total);

        return cartMapper.toResponse(cartRepository.save(cart));
    }

    public CartEntity getOpenCartForUser(String email){
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        return cartRepository.findByUser_IdAndCartStatusAndDeletedFalse(user.getId(), CartStatusEnum.OPEN)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));
    }

    public void eliminarItem(UUID itemId, String email){

        CartEntity cart = getOpenCartForUser(email);

        OrderItemEntity item = orderItemRepository.findByIdAndDeletedFalse(itemId)
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado en este carrito"));

        item.setDeleted(true);
        recalcularTotal(cart);

        orderItemRepository.save(item);
        cartRepository.save(cart);
    }
    
    public Page<CartResponse> findAll(Pageable pageable){
        return cartRepository.findAll(pageable)
                .map(cartMapper::toResponse);
    }

    public Page<CartResponse> findByStatus(OrderStatusEnum status, Pageable pageable) {
        return cartRepository.findByStatusOrderByAdmReceivedAtAsc(status, pageable)
                .map(cartMapper::toResponse);
    }
    
    public Page<CartResponse> findDeliveredForAdmin(Instant date,AdminDateFilterType dateType,Pageable pageable){
        return cartRepository.findDeliveredForAdmin(OrderStatusEnum.DELIVERED,date,dateType,pageable)
                .map(cartMapper::toResponse);
    }


    public CartWithItemsResponse findOpenCart(String email){

        CartEntity cart = getOpenCartForUser(email);

        double total = 0;

        for (OrderItemEntity item : cart.getItems()){
            if (item.isDeleted()) continue;

            double subtotal = pricingService.calcular(item);
            item.setAmount(subtotal);
            total+=subtotal;
            orderItemRepository.save(item);
        }

        cart.setTotal(total);

        cartRepository.save(cart);

        return cartMapper.toResponseWithItems(cart);
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

    public CartResponse actualizarEstado(UUID cartId, OrderStatusEnum nuevoEstado){

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


        // Setear fecha y hora
        if (nuevoEstado == OrderStatusEnum.DELIVERED) {
            cart.setDeliveredAt(Instant.now());
        } else {
            cart.setCompletedAt(Instant.now());
        }

        CartEntity actualizado = cartRepository.save(cart);
        return cartMapper.toResponse(actualizado);
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

    /**
     * Obtiene todos los pedidos (carritos completados) del usuario, ordenados de más reciente a antiguo*/
    public Page<CartResponse> obtenerPedidosDelUsuario(String email, Pageable pageable) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        return cartRepository.findByUser_IdAndStatusNotNullAndDeletedFalseOrderByCreatedAtDesc(
                user.getId(),
                pageable
        ).map(cartMapper::toResponse);
    }

}
