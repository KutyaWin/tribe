ALTER TABLE users
    ADD google_id BIGINT;

ALTER TABLE users
    ADD has_email_authentication BOOLEAN;

ALTER TABLE users
    ADD has_google_authentication BOOLEAN;

ALTER TABLE users
    ADD has_telegram_authentication BOOLEAN;

ALTER TABLE users
    ADD has_vk_authentication BOOLEAN;

ALTER TABLE users
    ADD has_whatsapp_authentication BOOLEAN;

ALTER TABLE users
    ADD vk_id BIGINT;

ALTER TABLE users
    ADD CONSTRAINT uc_users_google UNIQUE (google_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_vk UNIQUE (vk_id);

ALTER TABLE users
    DROP COLUMN social_id;