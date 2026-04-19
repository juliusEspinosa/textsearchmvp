package com.noteslookup.dto;

import com.noteslookup.model.Item;

import java.time.Instant;
import java.util.UUID;

public record ItemResponse(
        UUID itemId,
        String itemName,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getItemId(),
                item.getItemName(),
                item.getNotes(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}
