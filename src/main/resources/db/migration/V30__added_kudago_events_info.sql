ALTER TABLE events
    ADD external_publication_date date;

ALTER TABLE events
    ADD is_from_kudago BOOLEAN DEFAULT FALSE;

ALTER TABLE events
    ADD kudago_id BIGINT;