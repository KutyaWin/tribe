INSERT INTO event_types(type_name, type_name_en)
VALUES ('Спорт','Sport'),
       ('Транспорт', 'Transportation'),
       ('Выставка', 'Exhibition'),
       ('Посиделки', 'Party'),
       ('Обучения','Education'),
       ('Музыка','Music'),
       ('Для детей', 'For children'),
       ('Отдых за городом','Countryside retreat'),
       ('Религия', 'Religion'),
       ('Кино', 'Cinema'),
       ('Фестиваль', 'Festivals');

INSERT INTO tags(tag_name, tag_name_en)
VALUES ('велоспорт','cycling'),
       ('бег','running'),
       ('гребля','sailing'),
       ('хоккей','hockey'),
       ('пинг понг', 'ping pong'),
       ('волейбол','volleyball'),
       ('футбол','football'),
       ('баскетбол','basketball'),
       ('покер','poker'),
       ('шахматы','chess'),
       ('бокс','boxing'),
       ('вин чун','wing chun'),
       ('скейтбординг', 'skateboarding'),
       ('химия', 'chemistry'),
       ('физика', 'physics'),
       ('математика', 'math'),
       ('авто', 'cars'),
       ('мото', 'motorcycles'),
       ('гараж', 'garage'),
       ('тюнинг', 'tuning'),
       ('экскурсии', 'excursions'),
       ('музеи', 'museums'),
       ('бар', 'bar'),
       ('ресторан', 'restaurant'),
       ('квартира', 'apartment'),
       ('паб', 'pub'),
       ('настолки', 'board games'),
       ('медики', 'medical'),
       ('it', 'it'),
       ('коучи', 'coaches'),
       ('художники', 'artists'),
       ('мамы и папы', 'moms and dads'),
       ('концерт', 'concert'),
       ('дискотека', 'disco'),
       ('филармония', 'philharmonic'),
       ('пианино', 'piano'),
       ('рок', 'rock'),
       ('метал', 'metal'),
       ('поп', 'pop'),
       ('реп', 'rap'),
       ('хип хоп', 'hip hop');

INSERT INTO event_type_tags(type_id, tag_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (1, 7),
       (1, 8),
       (1, 9),
       (1, 10),
       (1, 11),
       (1, 12),
       (1, 13),
       (5, 14),
       (5, 15),
       (5, 16),
       (2, 17),
       (2, 18),
       (2, 19),
       (3, 20),
       (3, 21),
       (3, 22),
       (4, 23),
       (4, 24),
       (4, 25),
       (4, 26),
       (4, 27),
       (5, 28),
       (5, 29),
       (5, 30),
       (5, 31),
       (5, 32),
       (6, 33),
       (6, 34),
       (6, 35),
       (6, 36),
       (6, 37),
       (6, 38),
       (6, 39),
       (6, 40),
       (6, 41);