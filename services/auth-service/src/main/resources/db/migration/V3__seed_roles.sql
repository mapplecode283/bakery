INSERT INTO roles (id, name) VALUES
    ('role-admin', 'ROLE_ADMIN'),
    ('role-customer', 'ROLE_CUSTOMER'),
    ('role-driver', 'ROLE_DRIVER')
ON CONFLICT (id) DO NOTHING;
