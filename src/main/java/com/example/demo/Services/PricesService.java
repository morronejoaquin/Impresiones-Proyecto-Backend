package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.PricesMapper;
import com.example.demo.Model.DTOS.Request.PricesUpdateRequest;
import com.example.demo.Model.DTOS.Response.PricesResponse;
import com.example.demo.Model.Entities.PricesEntity;
import com.example.demo.Repositories.PricesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PricesService {

    private final PricesMapper pricesMapper;
    private final PricesRepository pricesRepository;

    @Autowired
    public PricesService(PricesMapper pricesMapper, PricesRepository pricesRepository) {
        this.pricesMapper = pricesMapper;
        this.pricesRepository = pricesRepository;
    }

    public void save(PricesUpdateRequest request){
        PricesEntity entity = pricesMapper.toEntity(request);
        pricesRepository.save(entity);
    }

    public Page<PricesResponse> findAll(Pageable pageable){
        Page<PricesEntity> page = pricesRepository.findAll(pageable);
        return page.map(pricesMapper::toResponse);
    }

    public PricesResponse findById(UUID id){
        PricesEntity entity = pricesRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Precios no encontrados"));

        return pricesMapper.toResponse(entity);
    }
}
