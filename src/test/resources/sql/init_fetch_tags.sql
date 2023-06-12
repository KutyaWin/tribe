insert into tags(id, tag_name, tag_name_en)
VALUES (1000, 'random_ru', 'random_en'),
       (1001, 'random_ru2', 'random_en2'),
       (1002, 'random_ru3', 'random_en3');

insert into event_types(id, type_name)
values (1000, 'test1'),
       (1001, 'test2');

insert into users(id, created_at, enable_geolocation, firebase_id, password, user_email)
VALUES (1000, now(), true, 'test1', 'test1', 'test1');

insert into events(id, created_at, start_time, end_time, event_name, event_status, is_eighteen_year_limit, is_free,
                   is_presence_of_alcohol, is_private, send_to_all_users_by_interests, show_event_in_search,
                   event_type, organizer_id)
VALUES (1000, now(), now(), now(), 'test1', 'PUBLISHED', false, false, false, false, true, true, 1000, 1000),
       (1001, now(), now(), now(), 'test2', 'PUBLISHED', false, false, false, true, true, true, 1001, 1000);

insert into events_tags(event_id, tag_id)
VALUES (1000, 1000),
       (1000, 1001),
       (1001, 1001),
       (1001, 1002);

insert into event_type_tags(type_id, tag_id)
VALUES (1000, 1000),
       (1000, 1001),
       (1001, 1002);