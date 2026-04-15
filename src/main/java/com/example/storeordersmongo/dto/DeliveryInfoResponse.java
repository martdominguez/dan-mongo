package com.example.storeordersmongo.dto;

public record DeliveryInfoResponse(
        String recipientName,
        String phone,
        String addressLine,
        String city,
        String postalCode,
        String country,
        String deliveryInstructions
) {
}
