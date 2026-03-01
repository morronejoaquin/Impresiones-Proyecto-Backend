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

        PriceCalculationResponse response = calculadora(item.getPages(), item.getCopies(), item.isColor(), item.isDoubleSided(), item.getBinding());
        item.setPricePerSheet(response.getPricePerSheet());
        item.setBindingPrice(response.getBindingPrice());

        return response.getTotal();
    }

    public PriceCalculationResponse calculadora(
            int pages,
            int copies,
            boolean color,
            boolean doubleSided,
            BindingTypeEnum binding
    ) {

        PricesEntity prices = obtenerPreciosVigentes();

        double precioHoja = color
                ? prices.getPricePerSheetColor()
                : prices.getPricePerSheetBW();

        // LÓGICA DOBLE FAZ:
        // Si es doble faz y tiene más de 1 página, dividimos por 2 y redondeamos hacia arriba.
        int hojasAImprimir = pages;
        if (doubleSided && pages > 1) {
            hojasAImprimir = (int) Math.ceil(pages / 2.0);
        }

        double subtotalImpresiones = precioHoja * hojasAImprimir * copies;

        // LÓGICA ENCUADERNACIÓN:
        double bindingPrice = 0;
        if (binding == BindingTypeEnum.RINGED) {
            bindingPrice = prices.getPriceRingedBinding();
        } else if (binding == BindingTypeEnum.STAPLED) {
            bindingPrice = prices.getPriceStapledBinding();
        }

        double total = subtotalImpresiones + bindingPrice;

        PriceCalculationResponse response = new PriceCalculationResponse();
        response.setTotal(total);
        response.setPricePerSheet(precioHoja);
        response.setBindingPrice(bindingPrice);

        return response;
    }

    public PricesEntity obtenerPreciosVigentes(){
        return pricesRepository
                .findFirstByValidToIsNullOrderByValidFromDesc()
                .orElseThrow(() -> new RuntimeException("No hay precios vigentes configurados"));

    }
}
