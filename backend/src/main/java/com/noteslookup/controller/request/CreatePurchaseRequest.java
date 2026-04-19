package com.noteslookup.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePurchaseRequest(
        @NotBlank String buyer,
        @NotNull UUID itemId,
        @Min(1) int quantity
) {
}
