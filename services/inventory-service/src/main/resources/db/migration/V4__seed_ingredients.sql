INSERT INTO ingredients (id, name, unit, min_stock) VALUES
    ('ing-coffee-beans', 'Coffee Beans', 'KG', 10.00),
    ('ing-milk', 'Milk', 'LITER', 5.00),
    ('ing-oat-milk', 'Oat Milk', 'LITER', 3.00),
    ('ing-almond-milk', 'Almond Milk', 'LITER', 3.00),
    ('ing-chocolate', 'Chocolate', 'KG', 2.00),
    ('ing-matcha', 'Matcha Powder', 'KG', 1.00),
    ('ing-vanilla-syrup', 'Vanilla Syrup', 'LITER', 2.00),
    ('ing-caramel-syrup', 'Caramel Syrup', 'LITER', 2.00),
    ('ing-ice', 'Ice', 'KG', 5.00),
    ('ing-chai-tea', 'Chai Tea', 'KG', 1.00);

INSERT INTO inventory_stock (id, ingredient_id, current_stock) VALUES
    ('stk-coffee-beans', 'ing-coffee-beans', 50.00),
    ('stk-milk', 'ing-milk', 30.00),
    ('stk-oat-milk', 'ing-oat-milk', 15.00),
    ('stk-almond-milk', 'ing-almond-milk', 15.00),
    ('stk-chocolate', 'ing-chocolate', 10.00),
    ('stk-matcha', 'ing-matcha', 5.00),
    ('stk-vanilla-syrup', 'ing-vanilla-syrup', 8.00),
    ('stk-caramel-syrup', 'ing-caramel-syrup', 8.00),
    ('stk-ice', 'ing-ice', 20.00),
    ('stk-chai-tea', 'ing-chai-tea', 5.00);
