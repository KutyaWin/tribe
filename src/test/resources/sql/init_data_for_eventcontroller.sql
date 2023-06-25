-- password = string
insert into users(id, created_at, enable_geolocation, firebase_id, password, user_email, status)
VALUES (1000, now(), true, 'test1', '$2a$10$oLwczdGVPQgQRb.eigM0deAW4fV.OYLHM3jXfls3gbb24dxIImJBm',
        'test1@gmail.com', 'ENABLED'),
       (1001, now(), true, 'test2', '$2a$10$oLwczdGVPQgQRb.eigM0deAW4fV.OYLHM3jXfls3gbb24dxIImJBm',
        'test2@gmail.com', 'ENABLED');

insert into event_types(id, type_name, type_name_en)
VALUES (1000, 'type_name_ru1000', 'type_name_en1000'),
       (1001, 'type_name_ru1001', 'type_name_en1001');

insert into event_addresses(id, city, event_latitude, event_longitude)
VALUES (1000, 'city1', 00000.00001, 00000.00002),
       (1001, 'city2', 00000.00002, 00000.00001);

insert into events(id, created_at, start_time, end_time, event_name, event_status, is_eighteen_year_limit, is_free,
                   is_presence_of_alcohol, is_private, send_to_all_users_by_interests, show_event_in_search,
                   event_address_id, event_type, organizer_id)
VALUES (1000, now(), now(), now(), 'eventname1', 'VERIFICATION_PENDING', false, false, false, false, true,
        true, 1000, 1000, 1000),
       (1001, now(), now(), now(), 'eventname2', 'PUBLISHED', false, false, false, true, true, true,1001, 1001, 1001);

insert into event_avatars(id, avatar_url, event_id)
VALUES (1000, '2023-06-21/c1b00948-d59a-4fef-8c99-d6f59e611545.jpg', 1000),
        (1001, '2023-06-22/c1b00948-d59a-4fef-8c99-d6f59e611545.jpg', 1001);

insert into users_relations_with_events(id, is_favorite, is_invited, is_participant, is_viewed, is_want_to_go,
                                        event_relations_id, user_relations_id)
VALUES (1000, false, false, false, true, false, 1000, 1000),
       (1001, true, true, false, false, false, 1001, 1001);
