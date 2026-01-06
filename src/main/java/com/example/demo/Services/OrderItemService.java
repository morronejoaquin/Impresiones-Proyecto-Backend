package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.OrderItemMapper;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class OrderItemService {

    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(OrderItemMapper orderItemMapper, OrderItemRepository orderItemRepository) {
        this.orderItemMapper = orderItemMapper;
        this.orderItemRepository = orderItemRepository;
    }

    public void save(OrderItemCreateRequest request){
        OrderItemEntity entity = orderItemMapper.toEntity(request);
        orderItemRepository.save(entity);
    }

    public Page<OrderItemResponse> findAll(Pageable pageable){
        Page<OrderItemEntity> page = orderItemRepository.findAll(pageable);
        return page.map(orderItemMapper::toResponse);
    }

    public OrderItemResponse findById(UUID id){
        OrderItemEntity entity = orderItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado"));

        return orderItemMapper.toResponse(entity);
    }

    public void update(UUID id, Map<String, Object> camposActualizados) {
        OrderItemEntity entity = orderItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Almacenamiento no encontrado"));

        if (camposActualizados.containsKey("id") || camposActualizados.containsKey("idProductoLocal")) {
            throw new IllegalArgumentException("No está permitido modificar el campo 'id'");
        }

        camposActualizados.forEach((key, value) -> {
            Field campo = ReflectionUtils.findField(OrderItemEntity.class, key);
            if (campo != null && !key.equals("id")) {
                campo.setAccessible(true);
                ReflectionUtils.setField(campo, entity, value);
            }
        });
        orderItemRepository.save(entity);
    }
}
