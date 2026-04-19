package com.noteslookup.dto;

import com.noteslookup.model.Purchase;

import java.time.Instant;
import java.util.UUID;

public record PurchaseResponse(
        UUID purchaseId,
        String buyer,
        UUID itemId,
        String itemName,
        int quantity,
        Instant purchasedAt
) {
    public static PurchaseResponse from(Purchase purchase) {
        return new PurchaseResponse(
                purchase.getPurchaseId(),
                purchase.getBuyer(),
                purchase.getItem().getItemId(),
                purchase.getItem().getItemName(),
                purchase.getQuantity(),
                purchase.getPurchasedAt()
        );
    }
}
