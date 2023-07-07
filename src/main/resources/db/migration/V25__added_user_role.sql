ALTER TABLE users
    ADD role VARCHAR(255);

UPDATE users
    SET role = 'USER';


ALTER TABLE users
    ALTER COLUMN role SET NOT NULL;