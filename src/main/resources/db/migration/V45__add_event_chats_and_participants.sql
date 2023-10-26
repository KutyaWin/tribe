INSERT INTO chat (is_group, event_id)
SELECT true AS is_group, id AS event_id
FROM events;

INSERT INTO chat_participant (chat_id, participant_id)
SELECT chat.id AS chat_id, organizer_id AS participant_id
FROM events
         JOIN chat ON events.id = chat.event_id
WHERE events.id IN (SELECT id FROM events);