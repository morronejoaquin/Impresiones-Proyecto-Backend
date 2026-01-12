package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.OrderItemMapper;
import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Request.OrderItemUpdateRequest;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public OrderItemResponse update(UUID id, OrderItemUpdateRequest request) {
        OrderItemEntity entity = orderItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item no encontrado"));

        if (request.getCopies() != null) {
            entity.setCopies(request.getCopies());
        }
        if (request.getColor() != null) {
            entity.setColor(request.getColor());
        }
        if (request.getDoubleSided() != null) {
            entity.setDoubleSided(request.getDoubleSided());
        }
        if (request.getBinding() != null) {
            entity.setBinding(request.getBinding());
        }
        if (request.getComments() != null) {
            entity.setComments(request.getComments());
        }

        orderItemRepository.save(entity);
        return orderItemMapper.toResponse(entity);
    }
}
