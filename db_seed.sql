INSERT INTO `nationality` (`id`, `name`) VALUES
(1, 'España'),
(2, 'Estados Unidos'),
(3, 'Mexico'),
(4, 'Inglaterra'),
(5, 'Alemania'),
(6, 'Japon'),
(7, 'China'),
(8, 'Italia'),
(9, 'Canada'),
(10, 'Rusia');

INSERT INTO `actor` (`id`, `name`) VALUES
(1, 'Sung Kang'),
(2, 'Lucas Black'),
(3, 'Vin Diesel'),
(4, 'Paul Walker'),
(5, 'Bow Wow'),
(6, 'Jamie Foxx'),
(7, 'Christoph Waltz'),
(8, 'Leonardo DiCaprio'),
(9, 'Kerry Washington'),
(10, 'Samuel L. Jackson'),
(11, 'Harrison Ford'),
(12, 'Karen Allen'),
(13, 'Paul Freeman'),
(14, 'Ronald Lacey'),
(15, 'John Rhys-Davies'),
(16, 'Denholm Elliott'),
(17, 'Thomas McDonell'),
(18, 'Michelle Pfeiffer'),
(19, 'Eva Green'),
(20, 'Johnny Depp'),
(21, 'Jonny Lee Miller');

INSERT INTO `director` (`id`, `name`) VALUES
(1, 'Tarantino'),
(2, 'Justin Lin'),
(3, 'Steven Spilverg'),
(4, 'Tim Burton'),
(5, 'Peter Jackson'),
(6, 'Woody Allen'),
(7, 'Wes Anderson'),
(8, 'David Lynch');

INSERT INTO `distributor` (`id`, `name`) VALUES
(1, 'Paramount Pictures'),
(2, '20th Century Studios'),
(3, 'Sony Pictures'),
(4, 'Warner Bros'),
(5, 'Universal Pictures'),
(6, 'Columbia Pictures');

INSERT INTO `genre` (`id`, `name`) VALUES
(1, 'Acción'),
(2, 'Animación'),
(3, 'Ciencia Ficción.'),
(4, 'Drama'),
(5, 'Fantasía'),
(6, 'Terror');

INSERT INTO `room` (`id`, `name`, `depth`, `seats`) VALUES
(1, 'A', 15, 13),
(2, 'B', 17, 20),
(3, 'C', 12, 18),
(4, 'D', 14, 20),
(5, 'E', 16, 22),
(6, 'F', 8, 10);

INSERT INTO `user` (`id`, `name`, `username`, `email`, `password`, `role_id`, `created_at`) VALUES
(1, 'Admin', 'admin', 'admin@admin.com', '$2a$10$AhjGV6Oh3Lh7E54iIUrFS.OoepOxJ7yQ28cJRsh0baHYJ8T0AGTc.', 2, '2024-01-10 16:40:56'),
(2, 'javier', 'javi', 'javier@gmail.com', '$2a$10$.rvT.Cw3MZ6ejyaamSfy9uCvWS3XyQlVe7CJTGxGvIp3YwxAj.QVS', 1, '2024-01-10 17:28:00'),
(3, 'Alejandro', 'alex', 'alejandro@gmail.com', '$2a$10$fjGjDVtM7JNOTeV1txP5MOrmMdTFKSfKewYsnrsUC4hWaINaHXZ1G', 1, '2024-01-10 17:30:28'),
(4, 'Carlos', 'carlos', 'carlos@gmail.com', '$2a$10$oTVjsQO8nw4o0MR/c9k./.OywnEjMc9na.l7KTJdRfSjGrGEHcDrG', 1, '2024-01-10 17:30:53'),

INSERT INTO `card` (`id`, `title`, `card_number`, `expiration`, `cvv`, `user_id`, `created_at`) VALUES
(1, 'Javier Lombardía Castro', 4322321053405962, '2026-01-16', '123', 2, '2024-01-10 13:52:27'),
(2, 'Alejandro Santamaría Mercado', 1234567890123456, '2026-03-26', '123', 3, '2024-01-10 13:52:28'),
(3, 'Carlos González Rubio', 0987654321653085, '2025-11-14', '123', 4, '2024-01-10 13:52:29');

INSERT INTO `movie` (`id`, `name`, `web`, `original_title`, `duration`, `year`, `synopsis`, `genre_id`, `nationality_id`, `distributor_id`, `director_id`, `age_classification_id`) VALUES
(1, 'Fast and furious 3', 'https://www.universalpictures.es', 'The Fast and the Furious: Tokyo Drift', 104, 2006, 'Shaun Boswell es un chico rebelde cuya única conexión con el mundo es a través de las carreras ilegales. Cuando la policía le amenaza con encarcelarle, se va a pasar una temporada con su tío, un militar destinado en Japón.', 1, 2, 5, 2, 5),
(2, 'Frankenweenie', 'https://www.dreamworks.com/', 'Frankenweenie', 97, 2012, 'El experimento científico que lleva a cabo el pequeño Víctor Frankenstein para hacer resucitar su adorado Sparky le obligará a afrontar terribles situaciones cuyas de imprevisibles consecuencias.', 2, 2, 4, 4, 2),
(3, 'Django Desencadenado', 'https://www.sonypictures.es', 'Django Unchained', 165, 2012, 'Un antiguo esclavo une sus fuerzas con un cazador de recompensas alemán que lo libera y ayuda a cazar a los criminales más buscados del Sur, todo ello con la esperanza de encontrar a su esposa perdida hace mucho tiempo.', 1, 2, 3, 1, 4),
(4, 'Indiana Jones Raiders of the Lost Ark', 'https://www.paramountpictures.com', 'Raiders of the Lost Ark', 140, 1981, 'En 1936 el arqueólogo estadounidense Indiana Jones viaja a un templo peruano para recuperar una estatuilla, sin embargo es interceptado por René Belloq, un colega con quien tiene una rivalidad. Tras una persecución por integrantes de una tribu salvaje, Belloq se hace con la estatuilla y Jones escapa a bordo de un hidroavión. De vuelta en Estados Unidos, un par de agentes de inteligencia del ejército le informan a Jones que, al interceptar unos telegramas nazis, se percataron de que las fuerzas alemanas se encuentran excavando en algún sitio de Tanis, Egipto. En uno de los telegramas se menciona a Abner Ravenwood, el antiguo mentor de Indiana. Con esta información, el aventurero deduce que los nazis buscan el Arca de la Alianza para volverse «invencibles», así que acepta involucrarse en una misión para impedirlo.', 1, 2, 1, 3, 3),
(5, 'Sombras Tenebrosas', 'https://warnerbros.es', 'Dark Shadows', 140, 2012, 'Barnabas, un playboy impenitente, un hombre rico y poderoso comete el error de romperle el corazón a Angelique Bouchard. Ella, que es una bruja, lo condena a un destino peor que la muerte: lo convierte en vampiro y lo entierra vivo.', 6, 9, 4, 4, 5);

INSERT INTO `movie_actor` (`movie_id`, `actor_id`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(3, 6),
(3, 7),
(3, 8),
(3, 9),
(3, 10),
(4, 11),
(4, 12),
(4, 13),
(4, 14),
(4, 15),
(4, 16),
(5, 17),
(5, 18),
(5, 19),
(5, 20),
(5, 21);

INSERT INTO `session` (`id`, `datetime`, `movie_id`, `room_id`) VALUES
(1, '2024-03-13 17:30:00', 1, 1),
(2, '2024-03-15 18:00:00', 2, 2),
(3, '2024-03-11 19:30:00', 3, 3),
(4, '2024-03-18 20:00:00', 4, 4),
(5, '2024-03-21 21:30:00', 5, 6);

INSERT INTO `payment` (`id`, `reference`, `amount`, `card_title`, `card_number`, `user_id`, `created_at`) VALUES 
(1, 'a1', '5', 'Javier Lombardía Castro', '4322321053405962', '2', current_timestamp());

INSERT INTO `ticket` (`id`, `depth`, `seat`, `code`, `session_id`, `user_id`, `payment_id`, `created_at`) VALUES 
(1, '1', '2', 'asdfa', '1', '2', '1', current_timestamp());

