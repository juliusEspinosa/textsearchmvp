package com.noteslookup.service;

import com.noteslookup.config.ItemNotFoundException;
import com.noteslookup.controller.request.CreateItemRequest;
import com.noteslookup.controller.request.UpdateItemRequest;
import com.noteslookup.model.Item;
import com.noteslookup.repository.ItemRepository;
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
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void create_givenValidRequest_returnsCreatedItem() {
        // Given
        var request = new CreateItemRequest("Test Item", "Some notes");
        var item = new Item("Test Item", "Some notes");
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // When
        var result = itemService.create(request);

        // Then
        assertThat(result.itemName()).isEqualTo("Test Item");
        assertThat(result.notes()).isEqualTo("Some notes");
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void getById_givenExistingId_returnsItem() {
        // Given
        var id = UUID.randomUUID();
        var item = new Item("Test Item", "Notes");
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));

        // When
        var result = itemService.getById(id);

        // Then
        assertThat(result.itemName()).isEqualTo("Test Item");
    }

    @Test
    void getById_givenNonExistingId_throwsItemNotFoundException() {
        // Given
        var id = UUID.randomUUID();
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> itemService.getById(id))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void update_givenExistingIdAndNameUpdate_updatesName() {
        // Given
        var id = UUID.randomUUID();
        var item = new Item("Old Name", "Old Notes");
        var request = new UpdateItemRequest("New Name", null);
        when(itemRepository.findById(id)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // When
        var result = itemService.update(id, request);

        // Then
        assertThat(result.itemName()).isEqualTo("New Name");
        assertThat(result.notes()).isEqualTo("Old Notes");
    }

    @Test
    void update_givenNonExistingId_throwsItemNotFoundException() {
        // Given
        var id = UUID.randomUUID();
        var request = new UpdateItemRequest("New Name", null);
        when(itemRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> itemService.update(id, request))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void delete_givenExistingId_deletesItem() {
        // Given
        var id = UUID.randomUUID();
        when(itemRepository.existsById(id)).thenReturn(true);

        // When
        itemService.delete(id);

        // Then
        verify(itemRepository).deleteById(id);
    }

    @Test
    void delete_givenNonExistingId_throwsItemNotFoundException() {
        // Given
        var id = UUID.randomUUID();
        when(itemRepository.existsById(id)).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> itemService.delete(id))
                .isInstanceOf(ItemNotFoundException.class);
    }

    @Test
    void search_givenQuery_returnsPaginatedResults() {
        // Given
        var item = new Item("Wireless Mouse", "Bluetooth mouse");
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<>(List.of(item), pageable, 1);
        when(itemRepository.search("mouse", pageable)).thenReturn(page);

        // When
        var result = itemService.search("mouse", 0, 20);

        // Then
        assertThat(result.page().getContent()).hasSize(1);
        assertThat(result.page().getContent().getFirst().itemName()).isEqualTo("Wireless Mouse");
        assertThat(result.durationMs()).isGreaterThanOrEqualTo(0);
        verify(itemRepository).search("mouse", pageable);
    }

    @Test
    void search_givenBlankQuery_returnsList() {
        // Given
        var item = new Item("Test Item", "Notes");
        var pageable = PageRequest.of(0, 20, Sort.by("itemName").ascending());
        var page = new PageImpl<>(List.of(item), pageable, 1);
        when(itemRepository.findAll(pageable)).thenReturn(page);

        // When
        var result = itemService.search("", 0, 20);

        // Then
        assertThat(result.page().getContent()).hasSize(1);
        assertThat(result.durationMs()).isGreaterThanOrEqualTo(0);
        verify(itemRepository).findAll(pageable);
    }

    @Test
    void list_givenPageAndSize_returnsPaginatedResults() {
        // Given
        var item = new Item("Test Item", "Notes");
        var pageable = PageRequest.of(0, 20, Sort.by("itemName").ascending());
        var page = new PageImpl<>(List.of(item), pageable, 1);
        when(itemRepository.findAll(pageable)).thenReturn(page);

        // When
        var result = itemService.list(0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().itemName()).isEqualTo("Test Item");
    }
}
