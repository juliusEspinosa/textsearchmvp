package com.noteslookup.service;

import com.noteslookup.config.PokemonNotFoundException;
import com.noteslookup.dto.PokemonResponse;
import com.noteslookup.repository.PokemonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PokemonServiceImpl implements PokemonService {

    private final PokemonRepository pokemonRepository;

    public PokemonServiceImpl(PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PokemonResponse> list(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by("pokedexNumber").ascending());
        return pokemonRepository.findAll(pageable).map(PokemonResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PokemonResponse> search(String query, int page, int size) {
        if (query == null || query.isBlank()) {
            return list(page, size);
        }
        var pageable = PageRequest.of(page, size);
        return pokemonRepository.search(query.trim(), pageable).map(PokemonResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public PokemonResponse getById(UUID id) {
        var pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> new PokemonNotFoundException(id));
        return PokemonResponse.from(pokemon);
    }
}
