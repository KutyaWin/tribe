ALTER TABLE event_type_tags
    DROP CONSTRAINT pk_event_type_tags;

ALTER TABLE users
    ALTER COLUMN user_email TYPE VARCHAR(255) USING (user_email::VARCHAR(255));