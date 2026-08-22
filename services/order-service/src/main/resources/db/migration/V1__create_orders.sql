CREATE TABLE orders (
    id                  VARCHAR(36) PRIMARY KEY,
    customer_id         VARCHAR(36) NOT NULL,
    status              VARCHAR(30) NOT NULL DEFAULT 'CREATED',
    subtotal            DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    tax                 DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    delivery_fee        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    delivery_type       VARCHAR(20) NOT NULL DEFAULT 'PICKUP',
    delivery_address_id VARCHAR(36),
    notes               TEXT,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
