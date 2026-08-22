CREATE TABLE refunds (
    id          VARCHAR(36) PRIMARY KEY,
    payment_id  VARCHAR(36) NOT NULL REFERENCES payments(id),
    order_id    VARCHAR(36) NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    reason      TEXT,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
