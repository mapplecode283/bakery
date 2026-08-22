CREATE TABLE product_options (
    id               VARCHAR(36) PRIMARY KEY,
    product_id       VARCHAR(36) NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    name             VARCHAR(100) NOT NULL,
    price_adjustment DECIMAL(10,2) NOT NULL DEFAULT 0.00
);

CREATE INDEX idx_options_product ON product_options(product_id);
