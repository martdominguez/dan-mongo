package com.example.storeordersmongo.dto;

import java.math.BigDecimal;

public record TopSoldProductResponse(
        String productId,
        String productName,
        String category,
        long unitsSold,
        BigDecimal totalSales
) {
}
