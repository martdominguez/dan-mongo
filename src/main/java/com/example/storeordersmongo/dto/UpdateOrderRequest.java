package com.example.storeordersmongo.dto;

import com.example.storeordersmongo.model.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateOrderRequest(
        @NotBlank String orderNumber,
        @NotBlank String customerId,
        @NotBlank String customerName,
        @NotNull OrderStatus status,
        @NotEmpty List<@Valid OrderItemRequest> items,
        @NotNull @Valid DeliveryInfoRequest delivery,
        @NotNull @Valid PaymentInfoRequest payment,
        List<String> tags,
        String notes
) {
}
