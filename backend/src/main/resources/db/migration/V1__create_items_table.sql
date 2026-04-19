CREATE TABLE items (
    item_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_name     VARCHAR(255) NOT NULL,
    notes         TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    search_vector tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('english', coalesce(item_name, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(notes, '')), 'B')
    ) STORED
);

CREATE INDEX idx_items_search_vector ON items USING GIN (search_vector);
CREATE INDEX idx_items_name_trgm     ON items USING GIN (item_name gin_trgm_ops);
