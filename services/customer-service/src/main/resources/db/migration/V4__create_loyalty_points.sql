CREATE TABLE IF NOT EXISTS loyalty_points (
    id          VARCHAR(36) PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    points      INTEGER NOT NULL,
    reason      VARCHAR(255) NOT NULL,
    order_id    VARCHAR(36),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_loyalty_customer ON loyalty_points(customer_id);
