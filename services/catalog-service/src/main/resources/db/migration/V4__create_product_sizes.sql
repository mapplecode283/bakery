CREATE TABLE product_sizes (
    id              VARCHAR(36) PRIMARY KEY,
    product_id      VARCHAR(36) NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    name            VARCHAR(50) NOT NULL,
    price_multiplier DECIMAL(5,2) NOT NULL DEFAULT 1.00
);

CREATE INDEX idx_sizes_product ON product_sizes(product_id);
