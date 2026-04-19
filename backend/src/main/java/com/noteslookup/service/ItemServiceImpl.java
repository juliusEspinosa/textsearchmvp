package com.noteslookup.service;

import com.noteslookup.config.ItemNotFoundException;
import com.noteslookup.controller.request.CreateItemRequest;
import com.noteslookup.controller.request.UpdateItemRequest;
import com.noteslookup.dto.ItemResponse;
import com.noteslookup.model.Item;
import com.noteslookup.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemServiceImpl.class);

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public ItemResponse create(CreateItemRequest request) {
        var item = new Item(request.itemName(), request.notes());
        var saved = itemRepository.save(item);
        return ItemResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponse getById(UUID id) {
        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        return ItemResponse.from(item);
    }

    @Override
    @Transactional
    public ItemResponse update(UUID id, UpdateItemRequest request) {
        var item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        if (request.itemName() != null) {
            item.setItemName(request.itemName());
        }
        if (request.notes() != null) {
            item.setNotes(request.notes());
        }

        var saved = itemRepository.save(item);
        return ItemResponse.from(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException(id);
        }
        itemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemResponse> list(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("itemName").ascending());
        return itemRepository.findAll(pageable).map(ItemResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchResult search(String query, int page, int size) {
        if (query == null || query.isBlank()) {
            long start = System.nanoTime();
            var results = list(page, size);
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            log.info("List query completed in {}ms, returned {} results", durationMs, results.getTotalElements());
            return new SearchResult(results, durationMs);
        }
        var pageable = PageRequest.of(page, size);
        long start = System.nanoTime();
        var results = itemRepository.search(query.trim(), pageable).map(ItemResponse::from);
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        log.info("Search query='{}' completed in {}ms, returned {} results (page {})",
                query.trim(), durationMs, results.getTotalElements(), page);
        return new SearchResult(results, durationMs);
    }
}
