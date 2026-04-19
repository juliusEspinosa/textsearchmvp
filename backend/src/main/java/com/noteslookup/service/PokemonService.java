package com.noteslookup.service;

import com.noteslookup.dto.PokemonResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface PokemonService {

    Page<PokemonResponse> list(int page, int size);

    Page<PokemonResponse> search(String query, int page, int size);

    PokemonResponse getById(UUID id);
}
