package com.noteslookup.controller.request;

import jakarta.validation.constraints.Size;

public record UpdateItemRequest(
        @Size(max = 255) String itemName,
        String notes
) {
}
