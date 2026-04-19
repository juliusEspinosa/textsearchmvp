package com.noteslookup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteslookup.controller.request.CreateItemRequest;
import com.noteslookup.controller.request.UpdateItemRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ItemCrudIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("itemsearch_test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init-test.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullCrudLifecycle_postGetPatchGetDeleteGet404() throws Exception {
        // POST - create item
        var createRequest = new CreateItemRequest("Integration Test Item", "Initial notes");
        var createResult = mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemName").value("Integration Test Item"))
                .andExpect(jsonPath("$.notes").value("Initial notes"))
                .andExpect(jsonPath("$.itemId").isNotEmpty())
                .andReturn();

        var itemId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("itemId").asText();

        // GET - retrieve created item
        mockMvc.perform(get("/api/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Integration Test Item"));

        // PATCH - update item
        var updateRequest = new UpdateItemRequest("Updated Integration Item", "Updated notes");
        mockMvc.perform(patch("/api/items/{id}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Updated Integration Item"))
                .andExpect(jsonPath("$.notes").value("Updated notes"));

        // GET - verify update
        mockMvc.perform(get("/api/items/{id}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName").value("Updated Integration Item"));

        // DELETE - remove item
        mockMvc.perform(delete("/api/items/{id}", itemId))
                .andExpect(status().isNoContent());

        // GET - verify deletion returns 404
        mockMvc.perform(get("/api/items/{id}", itemId))
                .andExpect(status().isNotFound());
    }
}
