ALTER TABLE users_relations_with_events
    ADD CONSTRAINT users_relations_with_events_unique UNIQUE (user_relations_id, event_relations_id);