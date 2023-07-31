insert into users (birthday, created_at, enable_geolocation,
                   firebase_id, first_name, last_name,
                   password, phone_number, user_avatar,
                   user_email, username, has_email_authentication,
                   has_google_authentication, has_telegram_authentication,
                   has_vk_authentication, has_whatsapp_authentication,
                   google_id, vk_id, status, role)
values ('1970-01-01', CURRENT_TIMESTAMP, false, 'no_firebase', 'Tribual', 'Tribe',
        '$2a$10$O7u6Wd4Qpa3MLC8otaJtpeIm7WwisKSaKyAB9wJzxfEWfiUiZT6HK',
        '+0-000-00-00', null, 'tribe@tribual.ru', 'Tribe', true,
        false, false, false, false, null, null, 'ENABLED', 'ADMIN');


insert into user_interests (user_id, event_type_id)
values ((select id from users where username = 'Tribe'), 1),
       ((select id from users where username = 'Tribe'), 2),
       ((select id from users where username = 'Tribe'), 3),
       ((select id from users where username = 'Tribe'), 4),
       ((select id from users where username = 'Tribe'), 5),
       ((select id from users where username = 'Tribe'), 6),
       ((select id from users where username = 'Tribe'), 7),
       ((select id from users where username = 'Tribe'), 8),
       ((select id from users where username = 'Tribe'), 9),
       ((select id from users where username = 'Tribe'), 10),
       ((select id from users where username = 'Tribe'), 11),
       ((select id from users where username = 'Tribe'), 12),
       ((select id from users where username = 'Tribe'), 13),
       ((select id from users where username = 'Tribe'), 14),
       ((select id from users where username = 'Tribe'), 15);



