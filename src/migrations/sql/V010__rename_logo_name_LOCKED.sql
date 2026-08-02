START TRANSACTION;
SET search_path = dennys;

ALTER TABLE teams RENAME COLUMN logo_name TO logo;

COMMIT;
