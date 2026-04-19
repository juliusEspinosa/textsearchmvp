CREATE TABLE digimon (
    digimon_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(255) NOT NULL,
    wiki_url      TEXT,
    image_url     TEXT,
    description   TEXT,
    name_vector   tsvector GENERATED ALWAYS AS (
        to_tsvector('english', coalesce(name, ''))
    ) STORED,
    desc_vector   tsvector GENERATED ALWAYS AS (
        to_tsvector('english', coalesce(description, ''))
    ) STORED
);

CREATE INDEX idx_digimon_name_vector ON digimon USING GIN (name_vector);
CREATE INDEX idx_digimon_desc_vector ON digimon USING GIN (desc_vector);
CREATE INDEX idx_digimon_name_trgm ON digimon USING GIN (name gin_trgm_ops);
