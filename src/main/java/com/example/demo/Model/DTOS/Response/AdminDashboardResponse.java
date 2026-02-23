package com.example.demo.Model.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminDashboardResponse {
    private List<OrderSummaryByStatusResponse> orderSummary;
    private List<PaymentSummaryByMethodResponse> paymentSummary;
    private PrintingStatisticsResponse printingStats;
}
