-- Seed data for demo purposes

INSERT INTO categories (name, description, active, created_at) VALUES
('Electronics', 'Devices and gadgets', TRUE, CURRENT_TIMESTAMP),
('Books', 'Printed and electronic books', TRUE, CURRENT_TIMESTAMP),
('Accessories', 'Miscellaneous accessories', TRUE, CURRENT_TIMESTAMP);

INSERT INTO products (name, price, currency, active, created_at) VALUES
('Widget A', 19.99, 'USD', TRUE, CURRENT_TIMESTAMP),
('Gadget B', 49.50, 'EUR', TRUE, CURRENT_TIMESTAMP),
('Book Example', 9.99, 'USD', TRUE, CURRENT_TIMESTAMP);
