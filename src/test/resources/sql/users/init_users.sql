insert into users(id, username, created_at, enable_geolocation, firebase_id, password, user_email, status)
VALUES (1000,'a', now(), true, 'test1', '$2a$10$oLwczdGVPQgQRb.eigM0deAW4fV.OYLHM3jXfls3gbb24dxIImJBm',
        'test1@gmail.com', 'ENABLED'),
       (1001, 'b',now(), true, 'test2', '$2a$10$oLwczdGVPQgQRb.eigM0deAW4fV.OYLHM3jXfls3gbb24dxIImJBm',
        'test2@gmail.com', 'ENABLED');