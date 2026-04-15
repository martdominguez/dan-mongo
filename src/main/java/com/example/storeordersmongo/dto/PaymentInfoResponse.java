package com.example.storeordersmongo.dto;

import java.time.Instant;

public record PaymentInfoResponse(
        Boolean paid,
        String paymentMethod,
        String transactionReference,
        Instant paidAt
) {
}
