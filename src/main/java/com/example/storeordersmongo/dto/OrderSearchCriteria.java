package com.example.storeordersmongo.dto;

import com.example.storeordersmongo.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSearchCriteria(
        OrderStatus status,
        String city,
        Boolean paid,
        Instant createdFrom,
        Instant createdTo,
        BigDecimal minimumAmount
) {
}
