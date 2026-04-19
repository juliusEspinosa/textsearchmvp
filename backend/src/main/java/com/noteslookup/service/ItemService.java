package com.noteslookup.service;

import com.noteslookup.controller.request.CreateItemRequest;
import com.noteslookup.controller.request.UpdateItemRequest;
import com.noteslookup.dto.ItemResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ItemService {

    ItemResponse create(CreateItemRequest request);

    ItemResponse getById(UUID id);

    ItemResponse update(UUID id, UpdateItemRequest request);

    void delete(UUID id);

    Page<ItemResponse> list(int page, int size);

    SearchResult search(String query, int page, int size);

    record SearchResult(Page<ItemResponse> page, long durationMs) {}
}
