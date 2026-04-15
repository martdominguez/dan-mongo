package com.example.storeordersmongo.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AverageOrderAmountResponse(
        Instant from,
        Instant to,
        BigDecimal averageAmount,
        long orderCount
) {
}
