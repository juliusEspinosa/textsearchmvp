package com.noteslookup.service;

import com.noteslookup.config.DigimonNotFoundException;
import com.noteslookup.model.Digimon;
import com.noteslookup.repository.DigimonRepository;
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
class DigimonServiceImplTest {

    @Mock
    private DigimonRepository digimonRepository;

    @InjectMocks
    private DigimonServiceImpl digimonService;

    private Digimon sampleDigimon() {
        return new Digimon("Agumon", "https://wikimon.net/Agumon", "https://wikimon.net/File:Agumon.jpg", "A reptile digimon");
    }

    @Test
    void search_givenNameOnly_searchesByName() {
        // Given
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<>(List.of(sampleDigimon()), pageable, 1);
        when(digimonRepository.searchByName("agumon", pageable)).thenReturn(page);

        // When
        var result = digimonService.search("agumon", null, "and", 0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().name()).isEqualTo("Agumon");
        verify(digimonRepository).searchByName("agumon", pageable);
    }

    @Test
    void search_givenDescriptionOnly_searchesByDescription() {
        // Given
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<>(List.of(sampleDigimon()), pageable, 1);
        when(digimonRepository.searchByDescription("reptile", pageable)).thenReturn(page);

        // When
        var result = digimonService.search(null, "reptile", "and", 0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(digimonRepository).searchByDescription("reptile", pageable);
    }

    @Test
    void search_givenBothWithAndMode_searchesWithAnd() {
        // Given
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<>(List.of(sampleDigimon()), pageable, 1);
        when(digimonRepository.searchByNameAndDescription("agumon", "reptile", pageable)).thenReturn(page);

        // When
        var result = digimonService.search("agumon", "reptile", "and", 0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(digimonRepository).searchByNameAndDescription("agumon", "reptile", pageable);
    }

    @Test
    void search_givenBothWithOrMode_searchesWithOr() {
        // Given
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<>(List.of(sampleDigimon()), pageable, 1);
        when(digimonRepository.searchByNameOrDescription("agumon", "reptile", pageable)).thenReturn(page);

        // When
        var result = digimonService.search("agumon", "reptile", "or", 0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(digimonRepository).searchByNameOrDescription("agumon", "reptile", pageable);
    }

    @Test
    void search_givenBlankBoth_returnsList() {
        // Given
        var pageable = PageRequest.of(0, 20, Sort.by("name").ascending());
        var page = new PageImpl<>(List.of(sampleDigimon()), pageable, 1);
        when(digimonRepository.findAll(pageable)).thenReturn(page);

        // When
        var result = digimonService.search("", "", "and", 0, 20);

        // Then
        assertThat(result.getContent()).hasSize(1);
        verify(digimonRepository).findAll(pageable);
    }

    @Test
    void getById_givenExistingId_returnsDigimon() {
        // Given
        var id = UUID.randomUUID();
        when(digimonRepository.findById(id)).thenReturn(Optional.of(sampleDigimon()));

        // When
        var result = digimonService.getById(id);

        // Then
        assertThat(result.name()).isEqualTo("Agumon");
    }

    @Test
    void getById_givenNonExistingId_throwsDigimonNotFoundException() {
        // Given
        var id = UUID.randomUUID();
        when(digimonRepository.findById(id)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> digimonService.getById(id))
                .isInstanceOf(DigimonNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
