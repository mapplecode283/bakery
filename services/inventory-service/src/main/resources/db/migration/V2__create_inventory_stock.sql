CREATE TABLE inventory_stock (
    id              VARCHAR(36) PRIMARY KEY,
    ingredient_id   VARCHAR(36) NOT NULL REFERENCES ingredients(id),
    current_stock   DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(ingredient_id)
);
