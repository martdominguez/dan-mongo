package com.example.storeordersmongo.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productId,
        String productName,
        String category,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
