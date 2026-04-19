package com.noteslookup.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "purchases")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "purchase_id")
    private UUID purchaseId;

    @Column(name = "buyer", nullable = false, length = 255)
    private String buyer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "purchased_at", nullable = false, updatable = false)
    private Instant purchasedAt;

    protected Purchase() {
    }

    public Purchase(String buyer, Item item, int quantity) {
        this.buyer = buyer;
        this.item = item;
        this.quantity = quantity;
    }

    @PrePersist
    void prePersist() {
        this.purchasedAt = Instant.now();
    }

    public UUID getPurchaseId() {
        return purchaseId;
    }

    public String getBuyer() {
        return buyer;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public Instant getPurchasedAt() {
        return purchasedAt;
    }
}
