CREATE TABLE inventory_movements (
    id              VARCHAR(36) PRIMARY KEY,
    ingredient_id   VARCHAR(36) NOT NULL REFERENCES ingredients(id),
    movement_type   VARCHAR(10) NOT NULL,
    quantity        DECIMAL(10,2) NOT NULL,
    reason          VARCHAR(255),
    reference_id    VARCHAR(36),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_movements_ingredient ON inventory_movements(ingredient_id);
