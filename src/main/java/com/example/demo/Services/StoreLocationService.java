package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.StoreLocationMapper;
import com.example.demo.Model.DTOS.Response.StoreLocationResponse;
import com.example.demo.Model.Entities.StoreLocationEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.StoreLocationRepository;

@Service
public class StoreLocationService {

    private final StoreLocationRepository repo;
    private final StoreLocationMapper storeLocationMapper;

    public StoreLocationService(StoreLocationRepository repo, StoreLocationMapper storeLocationMapper) {
        this.repo = repo;
        this.storeLocationMapper = storeLocationMapper;
    }

    public StoreLocationResponse getLocation(){
        StoreLocationEntity entity = repo.findAll()
                .stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay ubicacion configurada"));

        return storeLocationMapper.toResponse(entity);
    }
}

