ALTER TABLE broadcasts
    ADD triger_key VARCHAR(255);

ALTER TABLE broadcasts
    ALTER COLUMN subject_id SET NOT NULL;