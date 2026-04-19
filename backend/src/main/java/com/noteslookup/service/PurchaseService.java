package com.noteslookup.service;

import com.noteslookup.controller.request.CreatePurchaseRequest;
import com.noteslookup.dto.PurchaseResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PurchaseService {

    PurchaseResponse create(CreatePurchaseRequest request);

    PurchaseResponse getById(UUID id);

    Page<PurchaseResponse> listByBuyer(String buyer, int page, int size);

    Page<PurchaseResponse> listByItem(UUID itemId, int page, int size);

    Page<PurchaseResponse> list(int page, int size);

    Page<PurchaseResponse> search(String query, int page, int size);

    void delete(UUID id);
}
