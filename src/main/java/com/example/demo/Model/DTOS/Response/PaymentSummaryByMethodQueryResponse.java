package com.example.demo.Model.DTOS.Response;

import com.example.demo.Model.Enums.PaymentMethodEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentSummaryByMethodQueryResponse {
    private PaymentMethodEnum method;
    private Double total;
    private Long count;
}
