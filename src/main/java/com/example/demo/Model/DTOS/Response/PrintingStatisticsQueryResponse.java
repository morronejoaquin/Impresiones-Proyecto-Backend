package com.example.demo.Model.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrintingStatisticsQueryResponse {
    private Long totalSheets;
    private Long colorSheets;
    private Long bwSheets;
    private Long ringedCount;
    private Long stapledCount;
    private Long totalOrders;
}
