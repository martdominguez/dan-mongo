package com.example.storeordersmongo.dto;

import java.math.BigDecimal;

public record SalesByCategoryResponse(
        String category,
        BigDecimal totalSales,
        long unitsSold
) {
}
