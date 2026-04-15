package com.example.storeordersmongo.dto;

import jakarta.validation.constraints.NotBlank;

public record DeliveryInfoRequest(
        @NotBlank String recipientName,
        @NotBlank String phone,
        @NotBlank String addressLine,
        @NotBlank String city,
        @NotBlank String postalCode,
        @NotBlank String country,
        String deliveryInstructions
) {
}
