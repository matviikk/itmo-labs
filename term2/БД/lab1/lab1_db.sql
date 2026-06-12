CREATE TYPE sex AS ENUM('м','ж');
CREATE TYPE superpower AS ENUM('предвидение',
'телепатия', 
'теликинез', 
'исцеление', 
'невидимость', 
'левитация');
CREATE TYPE condition AS ENUM('живой',
'призрак');

CREATE TABLE people (
id SERIAL PRIMARY KEY,
full_name VARCHAR(255) NOT NULL,
date_of_birth DATE NOT NULL,
this_superpower superpower,
this_sex sex not null,
this_condition condition not null
);

CREATE TABLE planets (
id SERIAL PRIMARY KEY,
name_planet VARCHAR(255) NOT NULL,
description_planet TEXT
);

CREATE TABLE cities (
id SERIAL PRIMARY KEY,
name_city VARCHAR(255) NOT NULL,
population_city INT,
year_city INT,
planet_id INTEGER,
FOREIGN KEY (planet_id) REFERENCES planets (id)
);

CREATE TABLE scene (
id SERIAL PRIMARY KEY,
people_id INTEGER,
cities_id INTEGER,
FOREIGN KEY (people_id) REFERENCES people (id),
FOREIGN KEY (cities_id) REFERENCES cities (id),
actions_mc VARCHAR(255) NOT NULL,
actions_mc_with_mc VARCHAR(255) NOT NULL,
aim_mc VARCHAR(255) NOT NULL,
date_of_scene DATE NOT NULL
);

CREATE TABLE minor_characters (
people_id INTEGER NOT NULL REFERENCES people (id),
cities_id INTEGER NOT NULL REFERENCES cities (id),
CONSTRAINT minor_characters_pk PRIMARY KEY (people_id, cities_id)
);

INSERT INTO people (full_name, date_of_birth, this_superpower, this_sex, this_condition)
VALUES ('Хедрон Михайлович Воронин', '15.05.3984000', 'предвидение', 'м', 'живой'),
('Марина Владимировна Сергеева', '22.08.1910', 'невидимость', 'ж', 'призрак'),
('Игорь Александрович Николаев', '03.03.3983999', NULL, 'м', 'живой'),
('Елена Петровна Кузнецова', '29.07.1899', 'левитация', 'ж', 'призрак');

INSERT INTO planets (name_planet, description_planet)
VALUES ('Диаспар', 'Преемственная неизменность'),
('Терра Нова', 'Неограниченная динамика'),
('Лиридония', 'Гармоничное равновесие');

INSERT INTO cities (name_city, population_city, year_city, planet_id)
VALUES ('Солнечногорск', 4, 5958493, 1),
('Небесный Порт', 6, 5958499, 2),
('Звездопадск', 9, 5958576, 3);

INSERT INTO scene (people_id, cities_id, actions_mc, actions_mc_with_mc, aim_mc, date_of_scene)
VALUES (1, 1, 'Сидел в полном молчании', 'Потревожить призраков века', 'Предотвратить грядущее', '26.08.3984020'),
(1, 2, 'Медленно шел по улице', 'Договориться с призраками века', 'Остановить надвигающуюся угрозу', '11.05.3984021'),
(1, 3, 'Задумчиво глядел на небо', 'Сражаться с призраками века', 'Предотвратить катастрофические изменения', '09.12.3984025');

INSERT INTO minor_characters (people_id, cities_id)
VALUES (1, 1),
(2, 1),
(3, 1),
(4, 1);