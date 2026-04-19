package com.noteslookup.controller.api;

import com.noteslookup.config.GlobalExceptionHandler;
import com.noteslookup.config.PokemonNotFoundException;
import com.noteslookup.dto.PokemonResponse;
import com.noteslookup.service.PokemonService;
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

@WebMvcTest(PokemonController.class)
@Import(GlobalExceptionHandler.class)
class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PokemonService pokemonService;

    private static final UUID POKEMON_ID = UUID.randomUUID();

    private PokemonResponse sampleResponse() {
        return new PokemonResponse(POKEMON_ID, 25, "Pikachu", "Electric", null,
                320, 35, 55, 40, 50, 50, 90, 1, false);
    }

    @Test
    void list_givenDefaults_returnsPaginatedResults() throws Exception {
        // Given
        var page = new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1);
        when(pokemonService.list(0, 20)).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/pokemon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Pikachu"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void search_givenQuery_returns200() throws Exception {
        // Given
        var page = new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1);
        when(pokemonService.search("pikachu", 0, 20)).thenReturn(page);

        // When/Then
        mockMvc.perform(get("/api/pokemon/search").param("q", "pikachu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Pikachu"));
    }

    @Test
    void getById_givenExistingId_returns200() throws Exception {
        // Given
        when(pokemonService.getById(POKEMON_ID)).thenReturn(sampleResponse());

        // When/Then
        mockMvc.perform(get("/api/pokemon/{id}", POKEMON_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pikachu"))
                .andExpect(jsonPath("$.pokedexNumber").value(25));
    }

    @Test
    void getById_givenNonExistingId_returns404() throws Exception {
        // Given
        when(pokemonService.getById(POKEMON_ID)).thenThrow(new PokemonNotFoundException(POKEMON_ID));

        // When/Then
        mockMvc.perform(get("/api/pokemon/{id}", POKEMON_ID))
                .andExpect(status().isNotFound());
    }
}
