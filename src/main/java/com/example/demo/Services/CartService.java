package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.CartMapper;
import com.example.demo.Model.DTOS.Mappers.OrderItemMapper;
import com.example.demo.Model.DTOS.Request.CartCreateRequest;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CartService(CartMapper cartMapper, CartRepository cartRepository, UserRepository userRepository, OrderItemRepository orderItemRepository, OrderItemMapper orderItemMapper) {
        this.cartMapper = cartMapper;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
    }

    public CartResponse save(CartCreateRequest request){
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        CartEntity entity = cartMapper.toEntity(request);

        entity.setUser(user);
        entity.setTotal(0);
        entity.setCartStatus(CartStatusEnum.OPEN);
        entity.setStatus(OrderStatusEnum.PENDING);
        entity.setCompletedAt(null);
        entity.setDeliveredAt(null);

        CartEntity saved = cartRepository.save(entity);

        System.out.println("Carrito id: "+entity.getId());
        return cartMapper.toResponse(saved);
    }

    public Page<CartResponse> findAll(Pageable pageable){
        Page<CartEntity> page = cartRepository.findAll(pageable);
        return page.map(cartMapper::toResponse);
    }

    public CartResponse findById(UUID id){
        CartEntity entity = cartRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        return cartMapper.toResponse(entity);
    }

    public OrderItemResponse agregar(UUID cartId, OrderItemCreateRequest request){

        String formato = validarFormatoArchivo(request.getFile());

        CartEntity carrito = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        OrderItemEntity item = orderItemMapper.toEntity(request);

        item.setCart(carrito);
        item.setFile(request.getFile());
        item.setFileType(FileTypeEnum.valueOf(formato.toUpperCase()));
        item.setDeleted(false);

        carrito.getItems().add(item);
        cartRepository.save(carrito);

        OrderItemEntity saved = orderItemRepository.save(item);

        return orderItemMapper.toResponse(saved);
    }

    public String validarFormatoArchivo(String file){
        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("La ruta del archivo es obligatoria");
        }

        String formato = file.substring(file.lastIndexOf(".") + 1).toLowerCase();

        if(!formato.matches("pdf|jpg|png")){
            throw new IllegalArgumentException("Formato no valido: "+formato+". Solo se permiten PDF, JPG o PNG");
        }

        return formato;
    }

    public void eliminarItem(UUID cartId, UUID itemId){

        CartEntity cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        if(cart.getCartStatus() != CartStatusEnum.OPEN){
            throw new IllegalStateException("No se pueden modificar carritos cerrados");
        }

        OrderItemEntity item = orderItemRepository.findByIdAndDeletedFalse(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));

        if(!item.getCart().getId().equals(cartId)){
            throw new IllegalArgumentException("El item no pertenece a este carrito");
        }

        // Elimina virtualmente por decirlo de una manera
        item.setDeleted(true);

        // Recalcular total
        double nuevoTotal = cart.getItems().stream()
                .filter(i -> !i.isDeleted())
                .mapToDouble(OrderItemEntity::getAmount)
                .sum();

        cart.setTotal(nuevoTotal);

        orderItemRepository.save(item);
        cartRepository.save(cart);
    }

}
