-- password = string

insert into users(id, username, phone_number, created_at, enable_geolocation, firebase_id,
                  google_id, vk_id, password, user_email, status)
VALUES (1000, 'alak', '1234560', now(), true, 'test1', 'testG1',
        'testV1', '$2a$10$oLwczdGVPQgQRb.eigM0deAW4fV.OYLHM3jXfls3gbb24dxIImJBm',
        'test1@gmail.com', 'ENABLED'),
       (1001, 'alam', '1234561', now(), true, 'test2', 'testG2', 'testV2',
        '$2a$10$oLwczdGVPQgQRb.eigM0deAW4fV.OYLHM3jXfls3gbb24dxIImJBm',
        'test2@gmail.com','ENABLED');

insert into friends(id, relationship_status, user_who_get_follower, user_who_made_following, subscribe_at)
VALUES (1, 'SUBSCRIBE', 1000, 1001, now());

insert into event_types(id, type_name)
VALUES (1000, 'type_name_ru1000'),
       (1001, 'type_name_ru1001');

insert into user_interests(user_id, event_type_id)
VALUES (1000, 1000),
       (1001, 1000);