package com.noteslookup.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteslookup.config.GlobalExceptionHandler;
import com.noteslookup.config.ItemNotFoundException;
import com.noteslookup.controller.request.CreateItemRequest;
import com.noteslookup.controller.request.UpdateItemRequest;
import com.noteslookup.dto.ItemResponse;
import com.noteslookup.service.ItemService;
import com.noteslookup.service.ItemService.SearchResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import(GlobalExceptionHandler.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final Instant NOW = Instant.now();

    private ItemResponse sampleResponse() {
        return new ItemResponse(ITEM_ID, "Test Item", "Some notes", NOW, NOW);
    }

    @Test
    void create_givenValidRequest_returns201() throws Exception {
        // Given
        var request = new CreateItemRequest("Test Item", "Some notes");
        when(itemService.create(any(CreateItemRequest.class))).thenReturn(sampleResponse());

        // When/Then
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(ITEM_ID.toString()))
                .andExpect(jsonPath("$.itemName").value("Test Item"));
    }

    @Test
    void create_givenBlankName_returns400() throws Exception {
        // Given
        var request = new CreateItemRequest("", "Some notes");

        // When/Then
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_givenExistingId_returns200() throws Exception {
        // Given
        when(itemService.getById(ITEM_ID)).thenReturn(sampleResponse());

        // When/Then
        mockMvc.perform(get("/api/items/{id}", ITEM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(ITEM_ID.toString()))
                .andExpect(jsonPath("$.itemName").value("Test Item"));
    }

    @Test
    void getById_givenNonExistingId_returns404() throws Exception {
        // Given
        when(itemService.getById(ITEM_ID)).thenThrow(new ItemNotFoundException(ITEM_ID));

        // When/Then
        mockMvc.perform(get("/api/items/{id}", ITEM_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_givenValidRequest_returns200() throws Exception {
        // Given
        var request = new UpdateItemRequest("Updated Name", null);
        var response = new ItemResponse(ITEM_ID, "Updated Name", "Some notes", NOW, NOW);
        when(itemService.update(eq(ITEM_ID), any(UpdateItemRequest.class))).thenReturn(response);

        // When/Then
        mockMvc.perform(patch("/api/items/{id}", ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Updated Name"));
    }

    @Test
    void delete_givenExistingId_returns204() throws Exception {
        // Given
        doNothing().when(itemService).delete(ITEM_ID);

        // When/Then
        mockMvc.perform(delete("/api/items/{id}", ITEM_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_givenNonExistingId_returns404() throws Exception {
        // Given
        doThrow(new ItemNotFoundException(ITEM_ID)).when(itemService).delete(ITEM_ID);

        // When/Then
        mockMvc.perform(delete("/api/items/{id}", ITEM_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void search_givenQuery_returns200WithResults() throws Exception {
        // Given
        var page = new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1);
        when(itemService.search("test", 0, 20)).thenReturn(new SearchResult(page, 5L));

        // When/Then
        mockMvc.perform(get("/api/items/search").param("q", "test"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Search-Duration-Ms", "5"))
                .andExpect(jsonPath("$.content[0].itemId").value(ITEM_ID.toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void search_givenNoQuery_returns400() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/items/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void list_givenDefaults_returnsPaginatedResults() throws Exception {
        // Given
        var page = new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1);
        when(itemService.list(0, 20)).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].itemId").value(ITEM_ID.toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
