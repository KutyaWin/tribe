ALTER TABLE event_addresses
    ALTER COLUMN house_number TYPE VARCHAR(30) USING house_number::varchar(30);