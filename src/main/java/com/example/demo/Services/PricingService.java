package com.example.demo.Services;

import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Model.Entities.PricesEntity;
import com.example.demo.Repositories.PricesRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PricingService {

    private final PricesRepository pricesRepository;

    public PricingService(PricesRepository pricesRepository) {
        this.pricesRepository = pricesRepository;
    }

    public double calcular(OrderItemEntity item){

        PricesEntity prices = pricesRepository
                .findFirstByValidFromLessThanEqualAndValidToGreaterThanEqual(Instant.now(), Instant.now())
                .orElseThrow(() -> new RuntimeException("No hay precios vigentes configurados"));

        double precioHoja = item.isColor()
                ? prices.getPricePerSheetColor()
                : prices.getPricePerSheetBW();

        double total = precioHoja * item.getPages() * item.getCopies();

        if(item.getBinding() != null){
            total += prices.getPriceRingedBinding();
        }

        return total;
    }
}
