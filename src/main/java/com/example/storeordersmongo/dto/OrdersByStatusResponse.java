package com.example.storeordersmongo.dto;

public record OrdersByStatusResponse(
        String status,
        long count
) {
}
