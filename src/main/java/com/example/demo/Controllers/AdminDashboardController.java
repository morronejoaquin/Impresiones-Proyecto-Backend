package com.example.demo.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Services.AdminDashboardService;
import com.example.demo.Model.DTOS.Response.OrderSummaryByStatusResponse;
import com.example.demo.Model.DTOS.Response.PrintingStatisticsResponse;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("/orders-by-status")
    public ResponseEntity<List<OrderSummaryByStatusResponse>> getOrdersSummaryByStatus() {
        List<OrderSummaryByStatusResponse> summary = adminDashboardService.getOrderSummaryByStatus();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/printing-statistics")
    public ResponseEntity<PrintingStatisticsResponse> getPrintingStatistics() {
        PrintingStatisticsResponse statistics = adminDashboardService.getPrintingStatistics();
        return ResponseEntity.ok(statistics);
    }
}
