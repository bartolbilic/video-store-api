INSERT INTO genre (id, name) VALUES (1, 'COMEDY');
INSERT INTO genre (id, name) VALUES (2, 'FAMILY');
INSERT INTO genre (id, name) VALUES (3, 'ADVENTURE');
INSERT INTO genre (id, name) VALUES (4, 'CRIME');
INSERT INTO genre (id, name) VALUES (5, 'DRAMA');

INSERT INTO movie (id, title, description) VALUES (1, 'Home Alone', 'An eight-year-old troublemaker must protect his house from a pair of burglars when he is accidentally left home alone by his family during Christmas vacation.');
INSERT INTO movie (id, title, description) VALUES (2, 'Home Alone 2: Lost in New York', 'One year after Kevin McCallister was left home alone and had to defeat a pair of bumbling burglars, he accidentally finds himself stranded in New York City - and the same criminals are not far behind.');

INSERT INTO movie_genres(movies_id, genres_id) VALUES (1, 1);
INSERT INTO movie_genres(movies_id, genres_id) VALUES (1, 5);
INSERT INTO movie_genres(movies_id, genres_id) VALUES (2, 1);
INSERT INTO movie_genres(movies_id, genres_id) VALUES (2, 3);
INSERT INTO movie_genres(movies_id, genres_id) VALUES (2, 4);


