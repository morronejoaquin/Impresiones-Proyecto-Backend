package com.example.demo.Services;

import org.springframework.stereotype.Service;

import com.example.demo.Model.Entities.StoreLocationEntity;
import com.example.demo.Repositories.StoreLocationRepository;

@Service
public class StoreLocationService {

    private final StoreLocationRepository repo;

    public StoreLocationService(StoreLocationRepository repo) {
        this.repo = repo;
    }

    public StoreLocationEntity getLocation(){
        return repo.findAll().get(0);   // Indice cero porque esperamos uno solo
    }
}

