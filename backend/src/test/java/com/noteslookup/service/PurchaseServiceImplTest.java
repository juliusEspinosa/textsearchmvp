package com.noteslookup.service;

import com.noteslookup.config.ItemNotFoundException;
import com.noteslookup.config.PurchaseNotFoundException;
import com.noteslookup.controller.request.CreatePurchaseRequest;
import com.noteslookup.dto.PurchaseResponse;
import com.noteslookup.model.Item;
import com.noteslookup.model.Purchase;
import com.noteslookup.repository.ItemRepository;
import com.noteslookup.repository.PurchaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    private Item sampleItem() {
        return new Item("Wireless Mouse Ergonomic", "Some notes");
    }

    @Test
    void create_givenValidRequest_returnsCreatedPurchase() {
        // Given
        var itemId = UUID.randomUUID();
        var item = sampleItem();
        var request = new CreatePurchaseRequest("Alice", itemId, 3);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        var result = purchaseService.create(request);

        // Then
        assertThat(result.buyer()).isEqualTo("Alice");
        assertThat(result.quantity()).isEqualTo(3);
        assertThat(result.itemName()).isEqualTo("Wireless Mouse Ergonomic");
        verify(purchaseRepository).save(any(Purchase.class));
    }

    @Test
    void create_givenNonExistingItemId_throwsItemNotFoundException() {
        // Given
        var itemId = UUID.randomUUID();
        var request = new CreatePurchaseRequest("Alice", itemId, 1);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> purchaseService.create(request))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(itemId.toString());
    }

    @Test
    void getById_givenExistingId_returnsPurchase() {
        // Given
        var id = UUID.randomUUID();
        var purchase = new Purchase("Bob", sampleItem(), 2);
        when(purchaseRepository.findById(id)).thenReturn(Optional.of(purchase));

        // When
        var result = purchaseService.getById(id);

        // Then
        assertThat(result.buyer()).isEqualTo("Bob");
        assertThat(result.quantity()).isEqualTo(2);
    }

    @Test
    void getById_givenNonExistingId_throwsPurchaseNotFoundException() {
        // Given
        var id = UUID.randomUUID();
        when(purchaseRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> purchaseService.getById(id))
                .isInstanceOf(PurchaseNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void listByBuyer_givenBuyer_returnsPaginatedResults() {
        // Given
        var purchase = new Purchase("Alice", sampleItem(), 1);
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<>(List.of(purchase), pageable, 1);
        when(purchaseRepository.findByBuyer("Alice", pageable)).thenReturn(page);

        // When
        var result = purchaseService.listByBuyer("Alice", 0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().buyer()).isEqualTo("Alice");
    }

    @Test
    void listByItem_givenItemId_returnsPaginatedResults() {
        // Given
        var itemId = UUID.randomUUID();
        var purchase = new Purchase("Bob", sampleItem(), 5);
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<>(List.of(purchase), pageable, 1);
        when(purchaseRepository.findByItem_ItemId(itemId, pageable)).thenReturn(page);

        // When
        var result = purchaseService.listByItem(itemId, 0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().buyer()).isEqualTo("Bob");
    }

    @Test
    void list_givenPageAndSize_returnsPaginatedResults() {
        // Given
        var purchase = new Purchase("Charlie", sampleItem(), 10);
        var pageable = PageRequest.of(0, 20, Sort.by("purchasedAt").descending());
        var page = new PageImpl<>(List.of(purchase), pageable, 1);
        when(purchaseRepository.findAll(pageable)).thenReturn(page);

        // When
        var result = purchaseService.list(0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().buyer()).isEqualTo("Charlie");
    }

    @Test
    void search_givenQuery_returnsPaginatedResults() {
        // Given
        var purchase = new Purchase("Alice", sampleItem(), 1);
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<>(List.of(purchase), pageable, 1);
        when(purchaseRepository.searchByItemNotes("mouse", pageable)).thenReturn(page);

        // When
        var result = purchaseService.search("mouse", 0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().itemName()).isEqualTo("Wireless Mouse Ergonomic");
        verify(purchaseRepository).searchByItemNotes("mouse", pageable);
    }

    @Test
    void search_givenBlankQuery_returnsList() {
        // Given
        var purchase = new Purchase("Bob", sampleItem(), 2);
        var pageable = PageRequest.of(0, 20, Sort.by("purchasedAt").descending());
        var page = new PageImpl<>(List.of(purchase), pageable, 1);
        when(purchaseRepository.findAll(pageable)).thenReturn(page);

        // When
        var result = purchaseService.search("", 0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(purchaseRepository).findAll(pageable);
    }

    @Test
    void delete_givenExistingId_deletesPurchase() {
        // Given
        var id = UUID.randomUUID();
        when(purchaseRepository.existsById(id)).thenReturn(true);

        // When
        purchaseService.delete(id);

        // Then
        verify(purchaseRepository).deleteById(id);
    }

    @Test
    void delete_givenNonExistingId_throwsPurchaseNotFoundException() {
        // Given
        var id = UUID.randomUUID();
        when(purchaseRepository.existsById(id)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> purchaseService.delete(id))
                .isInstanceOf(PurchaseNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
