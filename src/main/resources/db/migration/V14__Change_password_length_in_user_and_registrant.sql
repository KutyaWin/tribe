ALTER TABLE registrant
    ALTER COLUMN password TYPE VARCHAR(100) USING (password::VARCHAR(100));

ALTER TABLE users
    ALTER COLUMN password TYPE VARCHAR(100) USING (password::VARCHAR(100));