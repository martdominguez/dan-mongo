package com.example.storeordersmongo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record PaymentInfoRequest(
        @NotNull Boolean paid,
        @NotBlank String paymentMethod,
        String transactionReference,
        Instant paidAt
) {
}
