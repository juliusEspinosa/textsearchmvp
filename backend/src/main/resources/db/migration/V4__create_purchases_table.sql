CREATE TABLE purchases (
    purchase_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    buyer         VARCHAR(255) NOT NULL,
    item_id       UUID NOT NULL REFERENCES items(item_id),
    quantity      INTEGER NOT NULL CHECK (quantity > 0),
    purchased_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_purchases_buyer   ON purchases (buyer);
CREATE INDEX idx_purchases_item_id ON purchases (item_id);

-- Seed sample purchases using subselects to resolve item_id from item_name
INSERT INTO purchases (buyer, item_id, quantity) VALUES
('Alice',   (SELECT item_id FROM items WHERE item_name = 'Wireless Bluetooth Headphones'), 1),
('Bob',     (SELECT item_id FROM items WHERE item_name = 'USB-C Charging Cable 6ft'), 5),
('Charlie', (SELECT item_id FROM items WHERE item_name = 'Ergonomic Office Chair'), 1),
('Alice',   (SELECT item_id FROM items WHERE item_name = 'Mechanical Keyboard RGB'), 2),
('Diana',   (SELECT item_id FROM items WHERE item_name = 'Stainless Steel Water Bottle 32oz'), 3),
('Eve',     (SELECT item_id FROM items WHERE item_name = 'Portable Phone Charger 10000mAh'), 1),
('Bob',     (SELECT item_id FROM items WHERE item_name = 'Noise Cancelling Earbuds'), 1),
('Frank',   (SELECT item_id FROM items WHERE item_name = 'Laptop Stand Adjustable'), 2),
('Grace',   (SELECT item_id FROM items WHERE item_name = 'Webcam HD 1080p'), 1),
('Alice',   (SELECT item_id FROM items WHERE item_name = 'Wireless Mouse Ergonomic'), 4),
('Hank',    (SELECT item_id FROM items WHERE item_name = 'Monitor Arm Dual Mount'), 1),
('Diana',   (SELECT item_id FROM items WHERE item_name = 'Standing Desk Converter'), 1),
('Charlie', (SELECT item_id FROM items WHERE item_name = 'Cable Management Kit'), 3),
('Eve',     (SELECT item_id FROM items WHERE item_name = 'Desk Pad Leather Large'), 1),
('Bob',     (SELECT item_id FROM items WHERE item_name = 'Smart Power Strip 6 Outlet'), 2),
('Frank',   (SELECT item_id FROM items WHERE item_name = 'External SSD 1TB Portable'), 1),
('Grace',   (SELECT item_id FROM items WHERE item_name = 'HDMI Cable 4K 10ft'), 10),
('Hank',    (SELECT item_id FROM items WHERE item_name = 'Microphone USB Condenser'), 1),
('Alice',   (SELECT item_id FROM items WHERE item_name = 'LED Desk Lamp Dimmable'), 2),
('Diana',   (SELECT item_id FROM items WHERE item_name = 'Laptop Cooling Pad'), 1);
