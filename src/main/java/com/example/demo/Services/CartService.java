package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.CartMapper;
import com.example.demo.Model.DTOS.Request.CartCreateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Model.Entities.CartEntity;
import com.example.demo.Repositories.CartRepository;
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

    @Autowired
    public CartService(CartMapper cartMapper, CartRepository cartRepository) {
        this.cartMapper = cartMapper;
        this.cartRepository = cartRepository;
    }

    public void save(CartCreateRequest request){
        CartEntity entity = cartMapper.toEntity(request);
        cartRepository.save(entity);
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
}
