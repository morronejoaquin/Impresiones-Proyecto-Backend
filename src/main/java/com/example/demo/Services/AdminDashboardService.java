package com.example.demo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.Model.DTOS.Response.OrderSummaryByStatusResponse;
import com.example.demo.Model.DTOS.Response.PrintingStatisticsResponse;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Repositories.OrderItemRepository;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDashboardService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderSummaryByStatusResponse> getOrderSummaryByStatus() {
        List<OrderItemEntity> allOrders = orderItemRepository.findAll();
        
        Map<String, List<OrderItemEntity>> groupedByStatus = allOrders.stream()
            .collect(Collectors.groupingBy(order -> order.getCart().getStatus().toString()));

        List<OrderSummaryByStatusResponse> result = new ArrayList<>();
        groupedByStatus.forEach((status, orders) -> {
            int count = orders.size();
            double totalAmount = orders.stream().mapToDouble(OrderItemEntity::getAmount).sum();
            result.add(new OrderSummaryByStatusResponse(status, count, totalAmount));
        });

        return result;
    }

    public PrintingStatisticsResponse getPrintingStatistics() {
        List<OrderItemEntity> allOrders = orderItemRepository.findAll();
        
        int totalSheets = 0;
        int colorSheets = 0;
        int bwSheets = 0;
        int ringedCount = 0;
        int stapledCount = 0;
        int noBindingCount = 0;

        for (OrderItemEntity order : allOrders) {
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

        double colorPercentage = totalSheets > 0 ? (colorSheets * 100.0) / totalSheets : 0;
        double bwPercentage = totalSheets > 0 ? (bwSheets * 100.0) / totalSheets : 0;
        double totalItems = allOrders.size();
        double ringedPercentage = totalItems > 0 ? (ringedCount * 100.0) / totalItems : 0;
        double stapledPercentage = totalItems > 0 ? (stapledCount * 100.0) / totalItems : 0;
        double noBindingPercentage = totalItems > 0 ? (noBindingCount * 100.0) / totalItems : 0;

        return new PrintingStatisticsResponse(colorPercentage, bwPercentage, ringedPercentage, 
                                         stapledPercentage, noBindingPercentage, totalSheets, 
                                         colorSheets, bwSheets);
    }
}
