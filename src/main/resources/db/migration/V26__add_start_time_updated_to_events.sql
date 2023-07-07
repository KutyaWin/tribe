ALTER TABLE events
    ADD is_start_time_updated BOOLEAN DEFAULT FALSE;

ALTER TABLE events
    ALTER COLUMN is_start_time_updated SET NOT NULL;