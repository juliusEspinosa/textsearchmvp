package com.noteslookup.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "digimon")
public class Digimon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "digimon_id")
    private UUID digimonId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "wiki_url", columnDefinition = "TEXT")
    private String wikiUrl;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    protected Digimon() {
    }

    public Digimon(String name, String wikiUrl, String imageUrl, String description) {
        this.name = name;
        this.wikiUrl = wikiUrl;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public UUID getDigimonId() { return digimonId; }
    public String getName() { return name; }
    public String getWikiUrl() { return wikiUrl; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
}
