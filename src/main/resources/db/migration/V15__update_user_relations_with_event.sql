-- Добавление CASCADE для существующего внешнего ключа
ALTER TABLE users_relations_with_events
    DROP CONSTRAINT users_relations_with_events_event_relations_id_fkey,
    ADD CONSTRAINT users_relations_with_events_event_relations_id_fkey
        FOREIGN KEY (event_relations_id)
            REFERENCES events (id)
            ON DELETE CASCADE;

-- Добавление CASCADE для существующего внешнего ключа
ALTER TABLE users_relations_with_events
    DROP CONSTRAINT users_relations_with_events_user_relations_id_fkey,
    ADD CONSTRAINT users_relations_with_events_user_relations_id_fkey
        FOREIGN KEY (user_relations_id)
            REFERENCES users (id)
            ON DELETE CASCADE;

-- Добавление CASCADE для существующего внешнего ключа
ALTER TABLE events
    DROP CONSTRAINT events_organizer_id_fkey,
    ADD CONSTRAINT events_organizer_id_fkey
        FOREIGN KEY (organizer_id)
            REFERENCES users (id)
            ON DELETE CASCADE;