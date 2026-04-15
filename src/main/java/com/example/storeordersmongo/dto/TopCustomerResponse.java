package com.example.storeordersmongo.dto;

import java.math.BigDecimal;

public record TopCustomerResponse(
        String customerId,
        String customerName,
        BigDecimal totalPurchasedAmount,
        long orderCount
) {
}
