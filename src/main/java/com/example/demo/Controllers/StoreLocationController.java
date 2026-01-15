package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Response.StoreLocationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Entities.StoreLocationEntity;
import com.example.demo.Services.StoreLocationService;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/store")
public class StoreLocationController {

    private final StoreLocationService service;

    public StoreLocationController(StoreLocationService service) {
        this.service = service;
    }

    @GetMapping("/location")
    public ResponseEntity<StoreLocationResponse> getLocation(){
        StoreLocationResponse response = service.getLocation();
        return ResponseEntity.ok(response);
    }

}


