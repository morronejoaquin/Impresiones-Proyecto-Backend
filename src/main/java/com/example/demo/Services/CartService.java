package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.CartMapper;
import com.example.demo.Model.DTOS.Mappers.OrderItemMapper;
import com.example.demo.Model.DTOS.Request.CartCreateRequest;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Model.DTOS.Response.CartWithItemsResponse;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;
import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Model.Enums.CartStatusEnum;
import com.example.demo.Model.Enums.FileTypeEnum;
import com.example.demo.Model.Enums.OrderStatusEnum;
import com.example.demo.Repositories.CartRepository;
import com.example.demo.Repositories.OrderItemRepository;
import com.example.demo.Repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

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

    public CartResponse save(CartCreateRequest request){
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        CartEntity entity = cartMapper.toEntity(request);
        entity.setUser(user);
        entity.setTotal(0);
        entity.setCartStatus(CartStatusEnum.OPEN);
        entity.setStatus(OrderStatusEnum.PENDING);

        return cartMapper.toResponse(cartRepository.save(entity));
    }

    public CartResponse findById(UUID id){
        return cartMapper.toResponse(cartRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado")));
    }

    public OrderItemResponse agregar(UUID cartId, OrderItemCreateRequest request, String driveFileId, String originalFileName){

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

        double subtotal = pricingService.calcular(item);
        item.setAmount(subtotal);
        item.setDeleted(false);

        cart.getItems().add(item);
        recalcularTotal(cart);

        orderItemRepository.save(item);
        cartRepository.save(cart);

        return orderItemMapper.toResponse(item);
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

    private void recalcularTotal(CartEntity cart){
        cart.setTotal(cart.getItems().stream()
                .filter(i -> !i.isDeleted())
                .mapToDouble(OrderItemEntity::getAmount)
                .sum());
    }

    private String validarFormatoArchivo(String file){
        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("La ruta del archivo es obligatoria");
        }

        String formato = file.substring(file.lastIndexOf(".") + 1).toLowerCase();
        if(!formato.matches("pdf|jpg|png")){
            throw new IllegalArgumentException("Formato no valido: "+formato);
        }
        return formato;
    }

    public CartWithItemsResponse findWithItems(UUID cartId){

        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        return cartMapper.toResponseWithItems(cart);
    }
}
