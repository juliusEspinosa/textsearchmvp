package com.noteslookup.config;

import java.util.UUID;

public class PokemonNotFoundException extends RuntimeException {
    public PokemonNotFoundException(UUID id) {
        super("Pokemon not found: " + id);
    }
}
