ALTER TABLE events
    ALTER COLUMN event_description TYPE TEXT USING (event_description::TEXT);
