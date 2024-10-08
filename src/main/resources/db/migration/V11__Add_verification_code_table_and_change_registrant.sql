CREATE TABLE phone_verification_code
(
    id                BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    verification_code INTEGER                                 NOT NULL,
    phone_number      VARCHAR(20)                             NOT NULL,
    request_time      TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    is_enable         BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_phone_verification_code PRIMARY KEY (id)
);

ALTER TABLE registrant
    ALTER COLUMN password TYPE VARCHAR(50) USING (password::VARCHAR(50));

ALTER TABLE registrant
    ALTER COLUMN password DROP NOT NULL;