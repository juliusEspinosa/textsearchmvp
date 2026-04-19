package com.noteslookup.controller.api;

import com.noteslookup.config.DigimonNotFoundException;
import com.noteslookup.config.GlobalExceptionHandler;
import com.noteslookup.dto.DigimonResponse;
import com.noteslookup.service.DigimonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DigimonController.class)
@Import(GlobalExceptionHandler.class)
class DigimonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DigimonService digimonService;

    private static final UUID DIGIMON_ID = UUID.randomUUID();

    private DigimonResponse sampleResponse() {
        return new DigimonResponse(DIGIMON_ID, "Agumon", "https://wikimon.net/Agumon",
                "https://wikimon.net/File:Agumon.jpg", "A reptile digimon");
    }

    @Test
    void list_givenDefaults_returns200() throws Exception {
        // Given
        var page = new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1);
        when(digimonService.list(0, 20)).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/digimon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Agumon"));
    }

    @Test
    void search_givenNameParam_returns200() throws Exception {
        // Given
        var page = new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1);
        when(digimonService.search("agumon", null, "and", 0, 20)).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/digimon/search").param("name", "agumon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Agumon"));
    }

    @Test
    void search_givenBothParamsAndMode_returns200() throws Exception {
        // Given
        var page = new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1);
        when(digimonService.search("agumon", "reptile", "or", 0, 20)).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/digimon/search")
                        .param("name", "agumon")
                        .param("description", "reptile")
                        .param("mode", "or"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Agumon"));
    }

    @Test
    void getById_givenExistingId_returns200() throws Exception {
        // Given
        when(digimonService.getById(DIGIMON_ID)).thenReturn(sampleResponse());

        // When/Then
        mockMvc.perform(get("/api/digimon/{id}", DIGIMON_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Agumon"));
    }

    @Test
    void getById_givenNonExistingId_returns404() throws Exception {
        // Given
        when(digimonService.getById(DIGIMON_ID)).thenThrow(new DigimonNotFoundException(DIGIMON_ID));

        // When/Then
        mockMvc.perform(get("/api/digimon/{id}", DIGIMON_ID))
                .andExpect(status().isNotFound());
    }
}
