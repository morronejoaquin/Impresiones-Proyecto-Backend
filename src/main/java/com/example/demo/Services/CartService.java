package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.CartMapper;
import com.example.demo.Model.DTOS.Mappers.OrderItemMapper;
import com.example.demo.Model.DTOS.Request.CartCreateRequest;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Model.Enums.CartStatusEnum;
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

    public void save(CartCreateRequest request){
        if(!userRepository.existsById(request.getUserId())){
            throw new NoSuchElementException("Usuario no encontrado");
        }

        CartEntity entity = cartMapper.toEntity(request);

        entity.setCartStatus(CartStatusEnum.PENDING);
        entity.setStatus(OrderStatusEnum.PENDING);

        cartRepository.save(entity);
        System.out.println("Carrito id: "+entity.getId());
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

    public String agregar(UUID cartId, OrderItemCreateRequest request){

        validarFormatoArchivo(request.getFile());

        CartEntity carrito = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Carrito no encontrado"));

        OrderItemEntity item = orderItemMapper.toEntity(request);

        item.setCart(carrito);

        carrito.getItems().add(item);
        cartRepository.save(carrito);

        return "Orden agregada correctamente";
    }

    public void validarFormatoArchivo(String file){
        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("La ruta del archivo es obligatoria");
        }

        String formato = file.substring(file.lastIndexOf(".") + 1).toLowerCase();

        if(!formato.matches("pdf|jpg|png")){
            throw new IllegalArgumentException("Formato no valido: "+formato+". Solo se permiten PDF, JPG o PNG");
        }
    }
}
