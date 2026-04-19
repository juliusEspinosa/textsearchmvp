package com.noteslookup.service;

import com.noteslookup.config.PokemonNotFoundException;
import com.noteslookup.model.Pokemon;
import com.noteslookup.repository.PokemonRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonServiceImplTest {

    @Mock
    private PokemonRepository pokemonRepository;

    @InjectMocks
    private PokemonServiceImpl pokemonService;

    private Pokemon samplePokemon() {
        return new Pokemon(25, "Pikachu", "Electric", null, 320, 35, 55, 40, 50, 50, 90, 1, false);
    }

    @Test
    void search_givenQuery_returnsPaginatedResults() {
        // Given
        var pokemon = samplePokemon();
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<>(List.of(pokemon), pageable, 1);
        when(pokemonRepository.search("pikachu", pageable)).thenReturn(page);

        // When
        var result = pokemonService.search("pikachu", 0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("Pikachu");
        verify(pokemonRepository).search("pikachu", pageable);
    }

    @Test
    void search_givenBlankQuery_returnsList() {
        // Given
        var pokemon = samplePokemon();
        var pageable = PageRequest.of(0, 20, Sort.by("pokedexNumber").ascending());
        var page = new PageImpl<>(List.of(pokemon), pageable, 1);
        when(pokemonRepository.findAll(pageable)).thenReturn(page);

        // When
        var result = pokemonService.search("", 0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(pokemonRepository).findAll(pageable);
    }

    @Test
    void getById_givenExistingId_returnsPokemon() {
        // Given
        var id = UUID.randomUUID();
        var pokemon = samplePokemon();
        when(pokemonRepository.findById(id)).thenReturn(Optional.of(pokemon));

        // When
        var result = pokemonService.getById(id);

        // Then
        assertThat(result.name()).isEqualTo("Pikachu");
        assertThat(result.pokedexNumber()).isEqualTo(25);
    }

    @Test
    void getById_givenNonExistingId_throwsPokemonNotFoundException() {
        // Given
        var id = UUID.randomUUID();
        when(pokemonRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> pokemonService.getById(id))
                .isInstanceOf(PokemonNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void list_givenPageAndSize_returnsPaginatedResults() {
        // Given
        var pokemon = samplePokemon();
        var pageable = PageRequest.of(0, 20, Sort.by("pokedexNumber").ascending());
        var page = new PageImpl<>(List.of(pokemon), pageable, 1);
        when(pokemonRepository.findAll(pageable)).thenReturn(page);

        // When
        var result = pokemonService.list(0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("Pikachu");
    }
}
