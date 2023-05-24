ALTER TABLE friends
    ADD subscribe_at TIMESTAMP WITH TIME ZONE;

ALTER TABLE friends
    ALTER COLUMN subscribe_at SET NOT NULL;

ALTER TABLE event_type_tags
    ADD CONSTRAINT pk_event_type_tags PRIMARY KEY (tag_id, type_id);

ALTER TABLE user_interests
    ADD CONSTRAINT pk_user_interests PRIMARY KEY (event_type_id, user_id);

ALTER TABLE events
    ADD CONSTRAINT uc_7993229c63b0b13a41b6099ad UNIQUE (event_name, start_time, organizer_id);

ALTER TABLE friends
    DROP COLUMN created_at;