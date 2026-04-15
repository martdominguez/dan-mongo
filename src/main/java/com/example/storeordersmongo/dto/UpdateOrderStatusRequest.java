package com.example.storeordersmongo.dto;

import com.example.storeordersmongo.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {
}
