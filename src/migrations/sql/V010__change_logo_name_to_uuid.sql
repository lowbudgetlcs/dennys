START TRANSACTION;
SET search_path = dennys;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE teams ADD COLUMN logo_key uuid NOT NULL DEFAULT uuid_generate_v4();
ALTER TABLE teams DROP COLUMN logo_name;


COMMIT;