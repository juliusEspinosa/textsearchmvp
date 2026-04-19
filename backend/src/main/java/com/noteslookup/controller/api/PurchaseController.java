package com.noteslookup.controller.api;

import com.noteslookup.controller.request.CreatePurchaseRequest;
import com.noteslookup.dto.PurchaseResponse;
import com.noteslookup.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public ResponseEntity<PurchaseResponse> create(@Valid @RequestBody CreatePurchaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PurchaseResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(purchaseService.search(q, page, size));
    }

    @GetMapping
    public ResponseEntity<Page<PurchaseResponse>> list(
            @RequestParam(required = false) String buyer,
            @RequestParam(required = false) UUID itemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (buyer != null) {
            return ResponseEntity.ok(purchaseService.listByBuyer(buyer, page, size));
        }
        if (itemId != null) {
            return ResponseEntity.ok(purchaseService.listByItem(itemId, page, size));
        }
        return ResponseEntity.ok(purchaseService.list(page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        purchaseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
