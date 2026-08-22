CREATE TABLE products (
    id          VARCHAR(36) PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    image_url   VARCHAR(500),
    base_price  DECIMAL(10,2) NOT NULL,
    category_id VARCHAR(36) NOT NULL REFERENCES categories(id),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    popular     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_active ON products(active);
