package com.noteslookup.dto;

import com.noteslookup.model.Digimon;

import java.util.UUID;

public record DigimonResponse(
        UUID digimonId,
        String name,
        String wikiUrl,
        String imageUrl,
        String description
) {
    public static DigimonResponse from(Digimon d) {
        return new DigimonResponse(
                d.getDigimonId(),
                d.getName(),
                d.getWikiUrl(),
                d.getImageUrl(),
                d.getDescription()
        );
    }
}
