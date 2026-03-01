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

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    public PricesResponse updatePrices(PricesUpdateRequest request){

        validatePrices(request);

        Optional<PricesEntity> preciosVigentes = pricesRepository.findFirstByValidToIsNullOrderByValidFromDesc();

        if(preciosVigentes.isPresent()){
            PricesEntity entity = preciosVigentes.get();
            entity.setValidTo(Instant.now());
            pricesRepository.save(entity);
        }

        PricesEntity newPrices = pricesMapper.toEntity(request);
        newPrices.setValidFrom(Instant.now());
        newPrices.setValidTo(null);
        PricesEntity saved = pricesRepository.save(newPrices);
        return pricesMapper.toResponse(saved);
    }

    public PricesResponse getCurrentPrices(){
        PricesEntity entity = pricesRepository.findFirstByValidToIsNullOrderByValidFromDesc()
                .orElseThrow(() -> new NoSuchElementException("No hay precios configurados"));
        return pricesMapper.toResponse(entity);
    }

    public void validatePrices(PricesUpdateRequest request){
        if(request.getPricePerSheetBW() <= 0){
            throw new IllegalArgumentException("El precio de hoja a blanco y negro no puede ser 0 o negativo");
        }else if(request.getPricePerSheetColor() <= 0){
            throw new IllegalArgumentException("El precio de hoja a color no puede ser 0 o negativo");
        }else if(request.getPriceRingedBinding() <= 0){
            throw new IllegalArgumentException("El precio de encuardernado no puede ser 0 o negativo");
        }else if(request.getPriceStapledBinding() <= 0){
            throw new IllegalArgumentException("El precio de abrochado no puede ser 0 o negativo");
        }
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
