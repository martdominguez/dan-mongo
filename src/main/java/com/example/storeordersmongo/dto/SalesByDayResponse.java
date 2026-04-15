package com.example.storeordersmongo.dto;

import java.math.BigDecimal;

public record SalesByDayResponse(
        String day,
        BigDecimal totalSales,
        long orderCount
) {
}
