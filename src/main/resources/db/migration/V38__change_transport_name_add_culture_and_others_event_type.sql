UPDATE event_types
SET type_name    = 'Поездки',
    type_name_en = 'Trips'
WHERE type_name = 'Транспорт';

INSERT INTO event_types (type_name, type_name_en, dark_circle_animation_path,
                         dark_rectangle_animation_path, light_circle_animation_path,
                         light_rectangle_animation_path, priority)
VALUES ('Культура', 'Culture', 'culture_dark_onboard.json',
        'culture_dark_add.json', 'culture_light_onboard.json', 'culture_dark_add.json', 0);

INSERT INTO event_types (type_name, type_name_en, dark_circle_animation_path,
                         dark_rectangle_animation_path, light_circle_animation_path,
                         light_rectangle_animation_path, priority)
VALUES ('Игры', 'Games', 'games_dark_onboard.json',
        'games_dark_add.json', 'games_light_onboard.json', 'games_dark_add.json', 0);

INSERT INTO event_types (type_name, type_name_en, dark_circle_animation_path,
                         dark_rectangle_animation_path, light_circle_animation_path,
                         light_rectangle_animation_path, priority)
VALUES ('Шопинг', 'Shopping', 'shopping_dark_onboard.json',
        'shopping_dark_add.json', 'shopping_light_onboard.json', 'shopping_dark_add.json', 0);

DELETE
FROM event_type_tags
WHERE type_id IN (SELECT id FROM event_types WHERE type_name = 'Фестиваль' OR type_name = 'Кино' OR type_name = 'Выставка');

UPDATE event_type_tags
SET type_id = (SELECT id FROM event_types WHERE type_name = 'Культура')
WHERE type_id IN (SELECT id FROM event_types WHERE type_name = 'Музыка');

DELETE
FROM user_interests
WHERE event_type_id IN (SELECT id
                        FROM event_types
                        WHERE type_name = 'Фестиваль' OR type_name = 'Кино' OR type_name = 'Выставка');

UPDATE user_interests
SET event_type_id = (SELECT id FROM event_types WHERE type_name = 'Культура')
WHERE event_type_id = (SELECT id FROM event_types WHERE type_name = 'Музыка');

UPDATE events
SET event_type = (SELECT id FROM event_types WHERE type_name = 'Культура')
WHERE event_type IN
      (SELECT id
       FROM event_types
       WHERE type_name = 'Фестиваль' OR type_name = 'Кино' OR type_name = 'Выставка' OR type_name = 'Музыка');

DELETE
FROM event_types
WHERE type_name = 'Фестиваль'
   OR type_name = 'Кино'
   OR type_name = 'Выставка'
   OR type_name = 'Музыка';

INSERT INTO event_types (type_name, type_name_en, dark_circle_animation_path,
                         dark_rectangle_animation_path, light_circle_animation_path,
                         light_rectangle_animation_path, priority)
VALUES ('Другое', 'Others', null,
        'others_dark.json', null, 'others_light.json', 0);
