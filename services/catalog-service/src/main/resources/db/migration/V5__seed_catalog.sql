-- Categories
INSERT INTO categories (id, name, description, sort_order) VALUES
    ('cat-hot-coffee', 'Hot Coffee', 'Classic hot coffee drinks', 1),
    ('cat-iced-coffee', 'Iced Coffee', 'Cold and refreshing coffee', 2),
    ('cat-tea', 'Tea', 'Premium tea selection', 3),
    ('cat-blended', 'Blended', 'Frozen blended beverages', 4);

-- Products
INSERT INTO products (id, name, description, base_price, category_id, popular) VALUES
    ('prod-americano', 'Americano', 'Espresso with hot water for a clean, bold flavor', 8.50, 'cat-hot-coffee', TRUE),
    ('prod-latte', 'Latte', 'Espresso with steamed milk and light foam', 10.50, 'cat-hot-coffee', TRUE),
    ('prod-cappuccino', 'Cappuccino', 'Espresso with thick milk foam', 10.50, 'cat-hot-coffee', FALSE),
    ('prod-mocha', 'Mocha', 'Espresso with chocolate, steamed milk, and whipped cream', 12.50, 'cat-hot-coffee', TRUE),
    ('prod-espresso', 'Espresso', 'Pure espresso shot, rich and intense', 7.00, 'cat-hot-coffee', FALSE),
    ('prod-caramel-macchiato', 'Caramel Macchiato', 'Vanilla syrup, milk, espresso, and caramel drizzle', 13.00, 'cat-hot-coffee', TRUE),
    ('prod-hot-chocolate', 'Hot Chocolate', 'Rich chocolate with steamed milk and whipped cream', 11.00, 'cat-hot-coffee', FALSE),
    ('prod-iced-americano', 'Iced Americano', 'Espresso with cold water over ice', 9.00, 'cat-iced-coffee', TRUE),
    ('prod-iced-latte', 'Iced Latte', 'Espresso with cold milk over ice', 11.00, 'cat-iced-coffee', TRUE),
    ('prod-cold-brew', 'Cold Brew', 'Slow-steeped cold brew coffee, smooth and bold', 12.00, 'cat-iced-coffee', TRUE),
    ('prod-matcha-latte', 'Matcha Latte', 'Japanese matcha with steamed milk', 13.00, 'cat-tea', TRUE),
    ('prod-chai-tea-latte', 'Chai Tea Latte', 'Spiced chai tea with steamed milk', 11.50, 'cat-tea', TRUE),
    ('prod-frappuccino', 'Frappuccino', 'Blended coffee with ice and your choice of flavor', 14.00, 'cat-blended', TRUE),
    ('prod-mango-smoothie', 'Mango Smoothie', 'Real mango blended with yogurt and ice', 13.50, 'cat-blended', FALSE);

-- Product Sizes for all products
DO $$
DECLARE
    prod RECORD;
BEGIN
    FOR prod IN SELECT id FROM products LOOP
        INSERT INTO product_sizes (id, product_id, name, price_multiplier) VALUES
            (gen_random_uuid()::text, prod.id, 'Small', 1.00),
            (gen_random_uuid()::text, prod.id, 'Medium', 1.30),
            (gen_random_uuid()::text, prod.id, 'Large', 1.60);
    END LOOP;
END $$;

-- Product Options for coffee products
DO $$
DECLARE
    prod RECORD;
BEGIN
    FOR prod IN SELECT id, name FROM products WHERE name NOT IN ('Matcha Latte', 'Chai Tea Latte', 'Hot Chocolate', 'Mango Smoothie') LOOP
        INSERT INTO product_options (id, product_id, name, price_adjustment) VALUES
            (gen_random_uuid()::text, prod.id, 'Extra Shot', 2.00),
            (gen_random_uuid()::text, prod.id, 'Oat Milk', 2.50),
            (gen_random_uuid()::text, prod.id, 'Almond Milk', 2.50),
            (gen_random_uuid()::text, prod.id, 'Vanilla Syrup', 1.50),
            (gen_random_uuid()::text, prod.id, 'Caramel Syrup', 1.50);
    END LOOP;
END $$;

-- Options for non-coffee drinks
INSERT INTO product_options (id, product_id, name, price_adjustment)
SELECT gen_random_uuid()::text, 'prod-matcha-latte', 'Oat Milk', 2.50;
INSERT INTO product_options (id, product_id, name, price_adjustment)
SELECT gen_random_uuid()::text, 'prod-matcha-latte', 'Vanilla Syrup', 1.50;
INSERT INTO product_options (id, product_id, name, price_adjustment)
SELECT gen_random_uuid()::text, 'prod-chai-tea-latte', 'Oat Milk', 2.50;
INSERT INTO product_options (id, product_id, name, price_adjustment)
SELECT gen_random_uuid()::text, 'prod-hot-chocolate', 'Extra Chocolate', 1.50;
INSERT INTO product_options (id, product_id, name, price_adjustment)
SELECT gen_random_uuid()::text, 'prod-hot-chocolate', 'Whipped Cream', 1.00;
