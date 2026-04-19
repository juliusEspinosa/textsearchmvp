package com.noteslookup.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteslookup.config.GlobalExceptionHandler;
import com.noteslookup.config.PurchaseNotFoundException;
import com.noteslookup.controller.request.CreatePurchaseRequest;
import com.noteslookup.dto.PurchaseResponse;
import com.noteslookup.service.PurchaseService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseController.class)
@Import(GlobalExceptionHandler.class)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseService purchaseService;

    private static final UUID PURCHASE_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final Instant NOW = Instant.now();

    private PurchaseResponse sampleResponse() {
        return new PurchaseResponse(PURCHASE_ID, "Alice", ITEM_ID, "Wireless Mouse", 3, NOW);
    }

    @Test
    void create_givenValidRequest_returns201() throws Exception {
        // Given
        var request = new CreatePurchaseRequest("Alice", ITEM_ID, 3);
        when(purchaseService.create(any(CreatePurchaseRequest.class))).thenReturn(sampleResponse());

        // When/Then
        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.purchaseId").value(PURCHASE_ID.toString()))
                .andExpect(jsonPath("$.buyer").value("Alice"))
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    void create_givenInvalidBody_returns400() throws Exception {
        // Given — blank buyer and zero quantity
        var request = new CreatePurchaseRequest("", ITEM_ID, 0);

        // When/Then
        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_givenExistingId_returns200() throws Exception {
        // Given
        when(purchaseService.getById(PURCHASE_ID)).thenReturn(sampleResponse());

        // When/Then
        mockMvc.perform(get("/api/purchases/{id}", PURCHASE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchaseId").value(PURCHASE_ID.toString()))
                .andExpect(jsonPath("$.buyer").value("Alice"));
    }

    @Test
    void getById_givenNonExistingId_returns404() throws Exception {
        // Given
        when(purchaseService.getById(PURCHASE_ID)).thenThrow(new PurchaseNotFoundException(PURCHASE_ID));

        // When/Then
        mockMvc.perform(get("/api/purchases/{id}", PURCHASE_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void search_givenQuery_returns200WithResults() throws Exception {
        // Given
        var page = new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1);
        when(purchaseService.search("mouse", 0, 20)).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/purchases/search").param("q", "mouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].itemName").value("Wireless Mouse"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void list_givenDefaults_returnsPaginatedResults() throws Exception {
        // Given
        var page = new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1);
        when(purchaseService.list(0, 20)).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/purchases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].purchaseId").value(PURCHASE_ID.toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void delete_givenExistingId_returns204() throws Exception {
        // Given
        doNothing().when(purchaseService).delete(PURCHASE_ID);

        // When/Then
        mockMvc.perform(delete("/api/purchases/{id}", PURCHASE_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_givenNonExistingId_returns404() throws Exception {
        // Given
        doThrow(new PurchaseNotFoundException(PURCHASE_ID)).when(purchaseService).delete(PURCHASE_ID);

        // When/Then
        mockMvc.perform(delete("/api/purchases/{id}", PURCHASE_ID))
                .andExpect(status().isNotFound());
    }
}
