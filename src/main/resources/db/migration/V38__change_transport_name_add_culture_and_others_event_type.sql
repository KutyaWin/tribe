UPDATE event_types
SET type_name    = 'Поездки',
    type_name_en = 'Trips'
WHERE type_name = 'Транспорт';

INSERT INTO event_types (type_name, type_name_en, dark_circle_animation_path,
                         dark_rectangle_animation_path, light_circle_animation_path,
                         light_rectangle_animation_path, priority)
VALUES ('Культура', 'Culture', 'culture_dark_onboard.json',
        'culture_dark_add.json', 'culture_light_onboard.json', 'culture_dark_add.json', 0);

UPDATE event_type_tags
SET type_id = (SELECT id FROM event_types WHERE type_name = 'Культура')
WHERE type_id IN (11, 10, 3);

UPDATE events
SET event_type = (SELECT id FROM event_types WHERE type_name = 'Культура')
WHERE event_type IN (11, 10, 3);

UPDATE user_interests
SET event_type_id = (SELECT id FROM event_types WHERE type_name = 'Культура')
WHERE event_type_id = 3;

DELETE
FROM user_interests
WHERE event_type_id IN (10, 11);

DELETE
FROM event_types
WHERE type_name = 'Фестиваль'
   OR type_name = 'Кино'
   OR type_name = 'Выставка';

INSERT INTO event_types (type_name, type_name_en, dark_circle_animation_path,
                         dark_rectangle_animation_path, light_circle_animation_path,
                         light_rectangle_animation_path, priority)
VALUES ('Другое', 'Others', null,
        'others_dark.json', null, 'others_light.json', 0);
