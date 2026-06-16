package com.example.demo.Model.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PrintingStatisticsResponse {
    private double colorPercentage;
    private double bwPercentage;
    private double ringedPercentage;
    private double stapledPercentage;
    private double noBindingPercentage;
    private int totalSheets;
    private int colorSheets;
    private int bwSheets;
}
