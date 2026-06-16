package com.example.demo.Services;

import com.example.demo.Model.DTOS.Response.*;
import com.example.demo.Repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.Repositories.OrderItemRepository;
import com.example.demo.Repositories.PaymentRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartRepository cartRepository;

    public AdminDashboardResponse getDashboardData(String startDate, String endDate) {

        Instant start = null;
        Instant end = null;

        // Convertimos las fechas de String a Instant
        if (startDate != null && !startDate.isBlank()) {
            start = LocalDate.parse(startDate).atStartOfDay(ZoneOffset.UTC).toInstant();
        }else {
            start = Instant.ofEpochMilli(0); // por defecto busca desde el inicio
        }
        if (endDate != null && !endDate.isBlank()) {
            end = LocalDate.parse(endDate).atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant();
        }else {
            end = Instant.now();
        }

        List<OrderSummaryByStatusResponse> orderSummary = getOrderSummaryByStatus(start, end);
        List<PaymentSummaryByMethodResponse> paymentSummary = getPaymentSummaryByMethod(start, end);
        PrintingStatisticsResponse printingStatistics = getPrintingStatistics(start, end);

        return new AdminDashboardResponse(orderSummary, paymentSummary, printingStatistics);
    }

    public List<OrderSummaryByStatusResponse> getOrderSummaryByStatus(Instant start, Instant end) {
        return cartRepository.getOrderSummary(start, end);
    }

    public PrintingStatisticsResponse getPrintingStatistics(Instant start, Instant end) {
        PrintingStatisticsQueryResponse stats = orderItemRepository.getPrintingStats(start, end);

        if (stats == null || stats.getTotalSheets() == 0) {
            return new PrintingStatisticsResponse(0, 0, 0, 0, 0, 0, 0, 0);
        }

        double totalSheets = stats.getTotalSheets().doubleValue();
        double totalOrders = stats.getTotalOrders().doubleValue();

        return new PrintingStatisticsResponse(
                calculatePercentage(stats.getColorSheets().doubleValue(), totalSheets),
                calculatePercentage(stats.getBwSheets().doubleValue(), totalSheets),
                calculatePercentage(stats.getRingedCount().doubleValue(), totalOrders),
                calculatePercentage(stats.getStapledCount().doubleValue(), totalOrders),
                calculatePercentage(stats.getTotalOrders() - (stats.getRingedCount().doubleValue() + stats.getStapledCount().doubleValue()), totalOrders),
                (int) totalSheets,
                stats.getColorSheets().intValue(),
                stats.getBwSheets().intValue()
        );
    }

    private double calculatePercentage(double part, double total) {
        return total > 0 ? (part / total) * 100 : 0;
    }

    public List<PaymentSummaryByMethodResponse> getPaymentSummaryByMethod(Instant start, Instant end) {
        List<PaymentSummaryByMethodQueryResponse> results = paymentRepository.getPaymentSummary(start, end);

        double totalCollected = results.stream().mapToDouble(r -> r.getTotal()).sum();

        return results.stream()
                .map(r -> new PaymentSummaryByMethodResponse(
                        r.getMethod().toString(),
                        r.getTotal(),
                        r.getCount().intValue(),
                        calculatePercentage(r.getTotal(), totalCollected)
                ))
                .collect(Collectors.toList());
    }
}
