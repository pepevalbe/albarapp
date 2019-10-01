INSERT INTO product(id, code, name, factory_price, tax) VALUES
	(1, 1, 'Bandeja - Huevo XL - 20uds', 3.6, 0),
	(2, 2, 'Bandeja - Huevo L - 30uds', 5.1, 0),
	(3, 3, 'Bandeja - Huevo M - 30uds', 4.8, 0),
	(4, 4, 'Bandeja - Huevo S - 30uds', 4.5, 0),
	(5, 5, 'Estuche - Huevo S - 10uds', 1.5, 0),
	(6, 6, 'Estuche - Huevo S - 6uds', 0.9, 0),
	(7, 7, 'Gallina', 4, 6.5);
	
INSERT INTO customer(id, fiscal_id, code, name, alias, phone_number, email, address, province) VALUES
    (1, 'H35083187', 1, 'C.C Yumbo Centrum', 'jumbo', '928764196', 'info@yumbocentrum.com', 'Av. Estados Unidos, 54, 35100 Maspalomas, Las Palmas', 'Las Palmas'),
    (2, 'aldeaCIF', 2, 'Bar Lauremar', 'aldea', '928892141', null, 'Av. los Cardones, 21, 35470 La Aldea de San Nicolas de Tolentino', 'Las Palmas'),
    (3, 'locoDNI', 3, 'Loco de Vecindario', 'loco', null, null, null, null);

INSERT INTO customer_product_price(id, offered_price, customer_id, product_id) VALUES
    (1, 3.6, 1, 1),
	(2, 5.1, 1, 2),
    (3, 4.8, 1, 3),
	(4, 4.5, 1, 4),
    (5, 1.5, 1, 5),
	(6, 0.9, 1, 6),
	(7, 4, 1, 7),
    (8, 3.6, 2, 1),
    (9, 4, 3, 7);

INSERT INTO invoice(id, issued_timestamp) VALUES
    (201800000, 1569173243000),
    (201800001, 1569173243000),
    (201800002, 1569173243000);

INSERT INTO delivery_note(id, issued_timestamp, customer_id, invoice_id) VALUES
    (1, 1569173243000, 1, 201800000),
    (2, 1569173243000, 1, 201800000),
    (3, 1569173243000, 2, 201800001),
    (4, 1569173243000, 3, 201800002);

INSERT INTO delivery_note_item(id, quantity, price, product_id, delivery_note_id) VALUES
    (1, 3, 29.5, 1, 1),
    (2, 5, 29.0, 1, 2),
    (3, 2, 33, 1, 3),
    (4, 1, 5, 2, 3),
    (5, 3, 5, 2, 4);