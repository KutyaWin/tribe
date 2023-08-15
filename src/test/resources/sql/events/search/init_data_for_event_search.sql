-- password = string
insert into users(id, created_at, enable_geolocation, firebase_id, password, user_email, username, status, role)
VALUES (1000, now(), true, 'test1', '$2a$10$oLwczdGVPQgQRb.eigM0deAW4fV.OYLHM3jXfls3gbb24dxIImJBm',
        'test1@gmail.com', 'asak', 'ENABLED', 'USER'),
       (1001, now(), true, 'test2', '$2a$10$oLwczdGVPQgQRb.eigM0deAW4fV.OYLHM3jXfls3gbb24dxIImJBm',
        'test2@gmail.com', 'asal', 'ENABLED', 'USER');

insert into event_types(id, type_name, type_name_en)
VALUES (1000, 'концерт', 'concert'),
       (1001, 'вечеринка', 'party');

insert into event_addresses(id, city, event_latitude, event_longitude)
VALUES (1000, 'Москва', 00000.00001, 00000.00002),
       (1001, 'Питер', 00000.00002, 00000.00001),
       (1002, 'Томск', 00000.00002, 00000.00001),
       (1003, 'Воронеж', 00000.00002, 00000.00001)
;

insert into events(id, created_at, start_time, end_time, event_name, event_status, is_eighteen_year_limit, is_free,
                   is_presence_of_alcohol, is_private, send_to_all_users_by_interests, show_event_in_search,
                   event_address_id, event_type, organizer_id, event_description)
VALUES (1000, now(), now(), now(), 'КУОК. BESKONECHNIY TOUR', 'PUBLISHED', false, false, false, false, true,
        true, 1000, 1000, 1000,
        'Больше шоу, больше рейва, специальные гости и презентация нового альбома — это 13 только часть того, что вас ждет.'),
       (1001, now(), now(), now(), 'INSTASAMKA & MONEYKEN', 'PUBLISHED', false, true, false, true, true,
        true, 1003, 1001, 1001,
        'Приготовьтесь: INSTASAMKA x MONEYKEN выступят с КУОК большим концертом и специальной программой 13 апреля в A2 Plaza beq"'),
       (1002, now(), now(), now(), 'Скриптонит', 'PUBLISHED', false, false, false, true, true,
        true, 1001, 1001, 1001,
        'Приготовьтесь: КУОК INSTASAMKA A2 Green'),
       (1003, now(), now(), now(), 'INSTASAMKA', 'PUBLISHED', false, false, false, true, true,
        true, 1001, 1001, 1001,
        'Приготовьтесь: INSTASAMKA x MONEYKEN выступят с КУОК большим концертом и специальной программой 13 апреля в "A2 Green Concert"')
;

insert into tags(id, tag_name, tag_name_en)
VALUES (1000, 'пивец', 'singer'),
       (1001, 'пивица', 'singer_girl');

insert into events_tags(event_id, tag_id)
VALUES (1000, 1000),
       (1001, 1001),
       (1002, 1000),
       (1002, 1001),
       (1003, 1000);

insert into users_relations_with_events(id, is_favorite, is_invited, is_participant, is_want_to_go,
                                        event_relations_id, user_relations_id)
VALUES (1000, false, false, false, false, 1000, 1000),
       (1001, true, true, false, false, 1001, 1001);

