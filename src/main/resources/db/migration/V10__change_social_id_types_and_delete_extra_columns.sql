ALTER TABLE unknown_users
    ADD firebase_id VARCHAR(100);

ALTER TABLE unknown_users
    ALTER COLUMN firebase_id SET NOT NULL;

ALTER TABLE unknown_users
    ADD CONSTRAINT uc_unknown_users_firebase UNIQUE (firebase_id);

ALTER TABLE unknown_users
    DROP COLUMN bluetooth_id;

ALTER TABLE users
    DROP COLUMN bluetooth_id;

ALTER TABLE users
    DROP COLUMN google_id;

ALTER TABLE users
    DROP COLUMN vk_id;

ALTER TABLE users
    ADD google_id VARCHAR(255);

ALTER TABLE users
    ADD CONSTRAINT uc_users_google UNIQUE (google_id);

ALTER TABLE users
    ALTER COLUMN password DROP NOT NULL;

ALTER TABLE users
    ALTER COLUMN user_email DROP NOT NULL;

ALTER TABLE users
    ADD vk_id VARCHAR(255);

ALTER TABLE users
    ADD CONSTRAINT uc_users_vk UNIQUE (vk_id);