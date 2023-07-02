insert into event_types(id, type_name, type_name_en)
VALUES (1000, 'type_name_ru1000', 'type_name_en1000'),
       (1001, 'type_name_ru1001', 'type_name_en1001');

insert into events(id, created_at, start_time, end_time, event_name, event_status, is_eighteen_year_limit, is_free,
                   is_presence_of_alcohol, is_private, send_to_all_users_by_interests, show_event_in_search,
                   event_type, organizer_id)
VALUES (1000, now(), now(), now(), 'eventname1', 'PUBLISHED', false, false, false, false, true, true, 1000, 1000),
       (1001, now(), now(), now(), 'eventname2', 'PUBLISHED', false, false, false, true, true, true, 1001, 1000);