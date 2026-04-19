package com.noteslookup.dto;

import com.noteslookup.model.Pokemon;

import java.util.UUID;

public record PokemonResponse(
        UUID pokemonId,
        int pokedexNumber,
        String name,
        String type1,
        String type2,
        int total,
        int hp,
        int attack,
        int defense,
        int spAtk,
        int spDef,
        int speed,
        int generation,
        boolean legendary
) {
    public static PokemonResponse from(Pokemon p) {
        return new PokemonResponse(
                p.getPokemonId(),
                p.getPokedexNumber(),
                p.getName(),
                p.getType1(),
                p.getType2(),
                p.getTotal(),
                p.getHp(),
                p.getAttack(),
                p.getDefense(),
                p.getSpAtk(),
                p.getSpDef(),
                p.getSpeed(),
                p.getGeneration(),
                p.isLegendary()
        );
    }
}
