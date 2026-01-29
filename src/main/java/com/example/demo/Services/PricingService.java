package com.example.demo.Services;

import com.example.demo.Model.DTOS.Response.PriceCalculationResponse;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Model.Entities.PricesEntity;
import com.example.demo.Model.Enums.BindingTypeEnum;
import com.example.demo.Repositories.PricesRepository;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    private final PricesRepository pricesRepository;

    public PricingService(PricesRepository pricesRepository) {
        this.pricesRepository = pricesRepository;
    }

    public double calcular(OrderItemEntity item){

        PriceCalculationResponse response = calculadora(item.getPages(), item.getCopies(), item.isColor(), item.getBinding());
        item.setPricePerSheet(response.getPricePerSheet());
        item.setPriceRingedBinding(response.getPriceRingedBinding());

        return response.getTotal();
    }

    public PriceCalculationResponse calculadora(
            int pages,
            int copies,
            boolean color,
            BindingTypeEnum binding
    ) {

        PricesEntity prices = obtenerPreciosVigentes();

        double precioHoja = color
                ? prices.getPricePerSheetColor()
                : prices.getPricePerSheetBW();

        double total = precioHoja * pages * copies;

        double bindingPrice = 0;
        if (binding != BindingTypeEnum.NONE) {
            bindingPrice = prices.getPriceRingedBinding();
            total += bindingPrice;
        }

        PriceCalculationResponse response = new PriceCalculationResponse();
        response.setTotal(total);
        response.setPricePerSheet(precioHoja);
        response.setPriceRingedBinding(bindingPrice);

        return response;
    }

    public PricesEntity obtenerPreciosVigentes(){
        return pricesRepository
                .findFirstByValidToIsNullOrderByValidFromDesc()
                .orElseThrow(() -> new RuntimeException("No hay precios vigentes configurados"));

    }
}
