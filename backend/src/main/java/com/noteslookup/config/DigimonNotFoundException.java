package com.noteslookup.config;

import java.util.UUID;

public class DigimonNotFoundException extends RuntimeException {
    public DigimonNotFoundException(UUID id) {
        super("Digimon not found: " + id);
    }
}
