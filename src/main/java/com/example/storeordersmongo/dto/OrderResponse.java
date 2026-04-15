package com.example.storeordersmongo.dto;

import com.example.storeordersmongo.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        String id,
        String orderNumber,
        String customerId,
        String customerName,
        OrderStatus status,
        Instant createdAt,
        Instant updatedAt,
        BigDecimal totalAmount,
        List<OrderItemResponse> items,
        DeliveryInfoResponse delivery,
        PaymentInfoResponse payment,
        List<String> tags,
        String notes
) {
}
