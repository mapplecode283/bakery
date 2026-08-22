CREATE TABLE order_items (
    id              VARCHAR(36) PRIMARY KEY,
    order_id        VARCHAR(36) NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id      VARCHAR(36) NOT NULL,
    product_name    VARCHAR(255) NOT NULL,
    size            VARCHAR(50),
    quantity        INTEGER NOT NULL DEFAULT 1,
    unit_price      DECIMAL(10,2) NOT NULL,
    subtotal        DECIMAL(10,2) NOT NULL,
    options_json    JSONB
);

CREATE INDEX idx_items_order ON order_items(order_id);
