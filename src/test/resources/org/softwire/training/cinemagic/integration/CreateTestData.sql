INSERT INTO cinemas (name) VALUES ('Test Cinema');
INSERT INTO screens (name, cinema_id, rows, row_width) VALUES('Screen 1', 1, 10, 10);
INSERT INTO films (name, length_minutes) VALUES('Test Film', 93);
INSERT INTO showings (screen_id, film_id, time) VALUES(1, 1, NOW());