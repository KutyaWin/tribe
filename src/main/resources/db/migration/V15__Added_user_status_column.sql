ALTER TABLE users
    ADD status VARCHAR(255);

UPDATE users SET status = 'ENABLED';

ALTER TABLE users
    ALTER COLUMN status SET NOT NULL;