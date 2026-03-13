START TRANSACTION;
SET search_path = dennys;

ALTER TABLE series ALTER COLUMN event_id SET NOT NULL;
ALTER TABLE games ADD UNIQUE (shortcode);

COMMIT;
