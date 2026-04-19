CREATE TABLE pokemon (
    pokemon_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pokedex_number INTEGER NOT NULL,
    name          VARCHAR(255) NOT NULL,
    type1         VARCHAR(50) NOT NULL,
    type2         VARCHAR(50),
    total         INTEGER NOT NULL,
    hp            INTEGER NOT NULL,
    attack        INTEGER NOT NULL,
    defense       INTEGER NOT NULL,
    sp_atk        INTEGER NOT NULL,
    sp_def        INTEGER NOT NULL,
    speed         INTEGER NOT NULL,
    generation    INTEGER NOT NULL,
    legendary     BOOLEAN NOT NULL DEFAULT FALSE,
    search_vector tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('english', coalesce(name, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(type1, '')), 'B') ||
        setweight(to_tsvector('english', coalesce(type2, '')), 'B')
    ) STORED
);

CREATE INDEX idx_pokemon_search_vector ON pokemon USING GIN (search_vector);
CREATE INDEX idx_pokemon_name_trgm ON pokemon USING GIN (name gin_trgm_ops);
