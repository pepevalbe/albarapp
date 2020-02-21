INSERT INTO user(id, email, password, name, surname, role) VALUES ('0', 'admin@albarapp.com', '$2a$10$eB4FAVGW3ykdZCpofs1zwO.zKHup.Y6B21hPpWCbfqH1dyD8uhJHS', 'admin-name', 'admin-surname', 'ADMIN');
INSERT INTO user(id, email, password, name, surname, role) VALUES ('1', 'user@albarapp.com', '$2a$10$1A6X0IvozT15pfnbeDGBVujKF70vthvAyIJvuAKdotFY/MOcfo0NO', 'user-name', 'user-surname', 'USER');

INSERT INTO product(id, code, name, factory_price, tax, aecoc_gtin) VALUES
	(1, 1, 'Huevo XL', 0.15, 0, '666666'),
	(2, 2, 'Huevo L', 0.13, 0, '55555'),
	(3, 3, 'Huevo M', 0.11, 0, '44444'),
	(4, 4, 'Huevo S', 0.09, 0, '3333'),
	(5, 5, 'Huevo M - Estuche 10uds', 0.13, 0, '2222'),
	(6, 6, 'Huevo M - Estuche 6uds', 0.13, 0, '1111'),
	(7, 7, 'Gallina', 4, 3, null);
	
INSERT INTO customer(id, fiscal_id, code, name, alias, phone_number, email, address, province, receiver_gln, buyer_gln, ship_gln, payer_gln, invoicee_gln) VALUES
    (1, 'H35083187', 1, 'C.C Yumbo Centrum', 'jumbo', '928764196', 'info@yumbocentrum.com', 'Av. Estados Unidos, 54, 35100 Maspalomas, Las Palmas', 'Las Palmas', null, null, null, null, null),
    (2, 'B00000000', 2, 'Bar Lauremar', 'aldea', '928892141', null, 'Av. los Cardones, 21, 35470 La Aldea de San Nicolas de Tolentino', 'Las Palmas', '12345', '12345', '12345', '12345', '12345'),
    (3, '80000000G', 3, 'Loco de Vecindario', 'loco', null, null, null, null, null, null, null, null, null);

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

INSERT INTO invoice(id, issued_timestamp, customer_id) VALUES
    (201800000, 1569173243000, 1),
    (201800001, 1569173243000, 2),
    (201800002, 1569173243000, 3);

INSERT INTO delivery_note(id, issued_timestamp, customer_id, invoice_id) VALUES
    (201800000, 1569173243000, 1, 201800000),
    (201800001, 1569173243000, 1, 201800000),
    (201800002, 1569173243000, 2, 201800001),
    (201800003, 1569173243000, 3, 201800002);

INSERT INTO delivery_note_item(id, quantity, price, product_id, delivery_note_id) VALUES
    (1, 3, 29.5, 1, 201800000),
    (2, 5, 29.0, 1, 201800001),
    (3, 2, 33, 1, 201800002),
    (4, 1, 5, 2, 201800002),
    (5, 3, 5, 2, 201800003);