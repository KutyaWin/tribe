INSERT INTO event_types (dark_circle_animation, dark_rectangle_animation, light_circle_animation,
                         light_rectangle_animation, type_name, type_name_en)
VALUES ('tourism_dark.json', 'tourism_dark.json', 'tourism_light.json', 'tourism_light.json', 'Туризм', 'Tourism');

INSERT INTO event_types (dark_circle_animation, dark_rectangle_animation, light_circle_animation,
                         light_rectangle_animation, type_name, type_name_en)
VALUES ('walking_dark.json', 'walking_dark.json', 'walking_light.json', 'walking_light.json', 'Прогулки', 'Walking');

INSERT INTO event_types (dark_circle_animation, dark_rectangle_animation, light_circle_animation,
                         light_rectangle_animation, type_name, type_name_en)
VALUES ('animals_dark.json', 'animals_dark.json', 'animals_light.json', 'animals_light.json', 'Для животных', 'For animals');

UPDATE event_types
SET dark_circle_animation = 'sport_dark.json',
    light_circle_animation = 'sport_light.json',
    dark_rectangle_animation = 'sport_dark.json',
    light_rectangle_animation = 'sport_light.json'
WHERE type_name = 'Спорт';

UPDATE event_types
SET dark_circle_animation = 'auto_moto_dark.json',
    light_circle_animation = 'auto_moto_light.json',
    dark_rectangle_animation = 'auto_moto_dark.json',
    light_rectangle_animation = 'auto_moto_light.json'
WHERE type_name = 'Транспорт';

UPDATE event_types
SET dark_circle_animation = 'exhibition_dark.json',
    light_circle_animation = 'exhibition_light.json',
    dark_rectangle_animation = 'exhibition_dark.json',
    light_rectangle_animation = 'exhibition_light.json'
WHERE type_name = 'Выставка';

UPDATE event_types
SET dark_circle_animation = 'alcohol_dark.json',
    light_circle_animation = 'alcohol_light.json',
    dark_rectangle_animation = 'alcohol_dark.json',
    light_rectangle_animation = 'alcohol_light.json'
WHERE type_name = 'Посиделки';

UPDATE event_types
SET dark_circle_animation = 'learning_dark.json',
    light_circle_animation = 'learning_light.json',
    dark_rectangle_animation = 'learning_dark.json',
    light_rectangle_animation = 'learning_light.json'
WHERE type_name = 'Обучение';

UPDATE event_types
SET dark_circle_animation = 'music_dark.json',
    light_circle_animation = 'music_light.json',
    dark_rectangle_animation = 'music_dark.json',
    light_rectangle_animation = 'music_light.json'
WHERE type_name = 'Музыка';

UPDATE event_types
SET dark_circle_animation = 'children_dark.json',
    light_circle_animation = 'children_light.json',
    dark_rectangle_animation = 'children_dark.json',
    light_rectangle_animation = 'children_light.json'
WHERE type_name = 'Для детей';

UPDATE event_types
SET dark_circle_animation = 'holiday_dark&light.json',
    light_circle_animation = 'holiday_dark&light.json',
    dark_rectangle_animation = 'holiday_dark&light.json',
    light_rectangle_animation = 'holiday_dark&light.json'
WHERE type_name = 'Отдых за городом';

UPDATE event_types
SET dark_circle_animation = 'religion_dark.json',
    light_circle_animation = 'religion_light.json',
    dark_rectangle_animation = 'religion_dark.json',
    light_rectangle_animation = 'religion_light.json'
WHERE type_name = 'Религия';

UPDATE event_types
SET dark_circle_animation = 'film_dark.json',
    light_circle_animation = 'film_light.json',
    dark_rectangle_animation = 'film_dark&light.json',
    light_rectangle_animation = 'film_dark&light.json'
WHERE type_name = 'Кино';

UPDATE event_types
SET dark_circle_animation = 'festival_dark&light.json',
    light_circle_animation = 'festival_dark&light.json',
    dark_rectangle_animation = 'festival_dark&light.json',
    light_rectangle_animation = 'festival_dark&light.json'
WHERE type_name = 'Фестиваль';

ALTER TABLE event_types
    ALTER COLUMN dark_circle_animation TYPE VARCHAR(50),
    ALTER COLUMN light_circle_animation TYPE VARCHAR(50),
    ALTER COLUMN dark_rectangle_animation TYPE VARCHAR(50),
    ALTER COLUMN light_rectangle_animation TYPE VARCHAR(50);