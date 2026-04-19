package com.noteslookup.service;

import com.noteslookup.config.ItemNotFoundException;
import com.noteslookup.config.PurchaseNotFoundException;
import com.noteslookup.controller.request.CreatePurchaseRequest;
import com.noteslookup.dto.PurchaseResponse;
import com.noteslookup.model.Purchase;
import com.noteslookup.repository.ItemRepository;
import com.noteslookup.repository.PurchaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ItemRepository itemRepository;

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository, ItemRepository itemRepository) {
        this.purchaseRepository = purchaseRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public PurchaseResponse create(CreatePurchaseRequest request) {
        var item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new ItemNotFoundException(request.itemId()));
        var purchase = new Purchase(request.buyer(), item, request.quantity());
        var saved = purchaseRepository.save(purchase);
        return PurchaseResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse getById(UUID id) {
        var purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new PurchaseNotFoundException(id));
        return PurchaseResponse.from(purchase);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseResponse> listByBuyer(String buyer, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return purchaseRepository.findByBuyer(buyer, pageable).map(PurchaseResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseResponse> listByItem(UUID itemId, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return purchaseRepository.findByItem_ItemId(itemId, pageable).map(PurchaseResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseResponse> list(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("purchasedAt").descending());
        return purchaseRepository.findAll(pageable).map(PurchaseResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PurchaseResponse> search(String query, int page, int size) {
        if (query == null || query.isBlank()) {
            return list(page, size);
        }
        var pageable = PageRequest.of(page, size);
        return purchaseRepository.searchByItemNotes(query.trim(), pageable).map(PurchaseResponse::from);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!purchaseRepository.existsById(id)) {
            throw new PurchaseNotFoundException(id);
        }
        purchaseRepository.deleteById(id);
    }
}
