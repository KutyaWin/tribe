-- password = string

SET TIME ZONE '+00:00';

insert into users(id, birthday, username, has_email_authentication, has_google_authentication, has_vk_authentication,
                  has_whatsapp_authentication, has_telegram_authentication,
                  phone_number, created_at, enable_geolocation, firebase_id,
                  google_id, vk_id, password, user_email, status, role)
VALUES (1000, now(), 'alak', true, false, false, false, false, '1234560', now(), true, 'test1', 'testG1',
        'testV1', '$2a$10$oLwczdGVPQgQRb.eigM0deAW4fV.OYLHM3jXfls3gbb24dxIImJBm',
        'test1@gmail.com', 'ENABLED', 'USER'),
       (1001, now(), 'alam', true, false, false, false, false, '1234561', now(), true, 'test2', 'testG2', 'testV2',
        '$2a$10$oLwczdGVPQgQRb.eigM0deAW4fV.OYLHM3jXfls3gbb24dxIImJBm',
        'test2@gmail.com','ENABLED', 'USER');

insert into friends(id, relationship_status, user_who_get_follower, user_who_made_following, subscribe_at)
VALUES (5, 'SUBSCRIBE', 1000, 1001, now());

insert into event_types(id, type_name)
VALUES (1000, 'type_name_ru1000'),
       (1001, 'type_name_ru1001');

insert into user_interests(user_id, event_type_id)
VALUES (1000, 1000),
       (1001, 1001);

insert into email_verification_code(id, reset_code, email, request_time, is_enable)
VALUES (1000, 1111, 'test11@gmail.com', now(), true),
       (1001, 2222, 'test22@gmail.com', CURRENT_TIMESTAMP - INTERVAL '5 minutes' , true);

insert into profession(id, name)
VALUES (1000, 'profession_name_ru1000'),
       (1001, 'profession_name_ru1001');

insert into user_profession(user_id, profession_id)
VALUES (1000, 1000),
       (1001, 1001);