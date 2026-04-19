package com.noteslookup.controller.api;

import com.noteslookup.dto.PokemonResponse;
import com.noteslookup.service.PokemonService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PokemonResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(pokemonService.search(q, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PokemonResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(pokemonService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PokemonResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(pokemonService.list(page, size));
    }
}
