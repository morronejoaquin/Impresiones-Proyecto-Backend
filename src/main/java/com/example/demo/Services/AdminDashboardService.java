package com.example.demo.Services;

import com.example.demo.Model.DTOS.Response.AdminDashboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.Model.DTOS.Response.OrderSummaryByStatusResponse;
import com.example.demo.Model.DTOS.Response.PrintingStatisticsResponse;
import com.example.demo.Model.DTOS.Response.PaymentSummaryByMethodResponse;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Model.Entities.PaymentEntity;
import com.example.demo.Model.Enums.PaymentStatusEnum;
import com.example.demo.Repositories.OrderItemRepository;
import com.example.demo.Repositories.PaymentRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public AdminDashboardResponse getDashboardData(String startDate, String endDate) {

        Instant start = null;
        Instant end = null;

        // Convertimos las fechas de String a Instant
        if (startDate != null && !startDate.isBlank()) {
            start = LocalDate.parse(startDate).atStartOfDay(ZoneOffset.UTC).toInstant();
        }else {
            start = Instant.now().minus(30, ChronoUnit.DAYS); // por defecto los ultimos 30 dias
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
        List<OrderItemEntity> orders = orderItemRepository.findAllByCreatedAtBetween(start, end);

        // 1. Agrupar los ítems por el Cart al que pertenecen para evitar duplicados
        Map<UUID, List<OrderItemEntity>> itemsByCart = orders.stream()
                .collect(Collectors.groupingBy(item -> item.getCart().getId()));

        // 2. Agrupar esos pedidos únicos por su estado
        Map<String, List<UUID>> cartsByStatus = itemsByCart.values().stream()
                .collect(Collectors.groupingBy(
                        orderItems -> orderItems.get(0).getCart().getStatus().toString(),
                        Collectors.mapping(orderItems -> orderItems.get(0).getCart().getId(), Collectors.toList())
                ));

        List<OrderSummaryByStatusResponse> result = new ArrayList<>();
        cartsByStatus.forEach((status, cartIds) -> {
            int count = cartIds.size(); // Esto ahora cuenta pedidos únicos

            // Sumar el total del carrito una sola vez por cada pedido
            double totalAmount = itemsByCart.entrySet().stream()
                    .filter(entry -> cartIds.contains(entry.getKey()))
                    .mapToDouble(entry -> entry.getValue().get(0).getCart().getTotal())
                    .sum();

            result.add(new OrderSummaryByStatusResponse(status, count, totalAmount));
        });

        return result;
    }

    public PrintingStatisticsResponse getPrintingStatistics(Instant start, Instant end) {
        List<OrderItemEntity> orders = orderItemRepository.findAllByCreatedAtBetween(start, end);
        
        int totalSheets = 0;
        int colorSheets = 0;
        int bwSheets = 0;
        int ringedCount = 0;
        int stapledCount = 0;
        int noBindingCount = 0;

        for (OrderItemEntity order : orders) {
            int sheets = order.getPages() * order.getCopies();
            totalSheets += sheets;

            if (order.isColor()) {
                colorSheets += sheets;
            } else {
                bwSheets += sheets;
            }

            String binding = order.getBinding() != null ? order.getBinding().toString() : "NONE";
            switch (binding) {
                case "RINGED" -> ringedCount++;
                case "STAPLED" -> stapledCount++;
                default -> noBindingCount++;
            }
        }

        return new PrintingStatisticsResponse(
                calculatePercentage(colorSheets, totalSheets),
                calculatePercentage(bwSheets, totalSheets),
                calculatePercentage(ringedCount, orders.size()),
                calculatePercentage(stapledCount, orders.size()),
                calculatePercentage(noBindingCount, orders.size()),
                totalSheets, colorSheets, bwSheets
        );
    }

    private double calculatePercentage(double part, double total) {
        return total > 0 ? (part / total) * 100 : 0;
    }

    public List<PaymentSummaryByMethodResponse> getPaymentSummaryByMethod(Instant start, Instant end) {
        List<PaymentEntity> payments = paymentRepository.findAllByPaidAtBetween(start, end);
        
        // Filter only completed payments
        List<PaymentEntity> completedPayments = payments.stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatusEnum.APPROVED)
                .toList();

        double totalCollected = completedPayments.stream()
                .mapToDouble(PaymentEntity::getFinalPrice)
                .sum();

        // Group payments by method
        Map<String, List<PaymentEntity>> groupedByMethod = completedPayments.stream()
                .collect(Collectors.groupingBy(p -> p.getPaymentMethod().toString()));

        List<PaymentSummaryByMethodResponse> result = new ArrayList<>();
        groupedByMethod.forEach((method, payment) -> {
            double methodTotal = payment.stream()
                    .mapToDouble(PaymentEntity::getFinalPrice)
                    .sum();
            double percentage = calculatePercentage(methodTotal, totalCollected);
            result.add(new PaymentSummaryByMethodResponse(
                    method,
                    methodTotal,
                    payment.size(),
                    percentage
            ));
        });

        return result;
    }
}
