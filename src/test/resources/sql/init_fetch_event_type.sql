insert into users(id, bluetooth_id, created_at, enable_geolocation, firebase_id, password, user_email)
VALUES (1000, 'test1', now(), true, 'test1', 'test1', 'test1');

insert into event_types(id, type_name, type_name_en)
VALUES (1000, 'test_ru1000', 'test_en1000'),
       (1001, 'test_ru1001', 'test_en1001');

insert into tags(id, tag_name, tag_name_en)
VALUES (1000, 'tags_ru1000', 'tags_en1000');

insert into event_type_tags(type_id, tag_id)
VALUES (1000,1000);

insert into events(id, created_at, start_time, end_time, event_name, event_status, is_eighteen_year_limit, is_free,
                   is_presence_of_alcohol, is_private, send_to_all_users_by_interests, show_event_in_search,
                   event_type, organizer_id)
VALUES (1000, now(), now(), now(), 'test1', 'PUBLISHED', false, false, false, false, true, true, 1000, 1000),
       (1001, now(), now(), now(), 'test2', 'PUBLISHED', false, false, false, true, true, true, 1001, 1000);