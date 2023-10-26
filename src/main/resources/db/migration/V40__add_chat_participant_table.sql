CREATE TABLE chat_participant
(
    chat_id        BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    CONSTRAINT pk_chat_participant PRIMARY KEY (chat_id, participant_id)
);

ALTER TABLE chat_participant
    ADD CONSTRAINT fk_chapar_on_chat FOREIGN KEY (chat_id) REFERENCES chat (id);

ALTER TABLE chat_participant
    ADD CONSTRAINT fk_chapar_on_user FOREIGN KEY (participant_id) REFERENCES users (id);