CREATE TABLE IF NOT EXISTS deliveries (
    id              VARCHAR(36) PRIMARY KEY,
    order_id        VARCHAR(36) NOT NULL,
    driver_id       VARCHAR(36) REFERENCES drivers(id),
    pickup_address  TEXT,
    delivery_address TEXT,
    status          VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    pickup_at       TIMESTAMP WITH TIME ZONE,
    delivered_at    TIMESTAMP WITH TIME ZONE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deliveries_order ON deliveries(order_id);
CREATE INDEX idx_deliveries_driver ON deliveries(driver_id);
