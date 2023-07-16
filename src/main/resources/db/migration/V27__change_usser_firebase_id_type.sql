ALTER TABLE users
    ALTER COLUMN firebase_id TYPE TEXT USING (firebase_id::TEXT);
