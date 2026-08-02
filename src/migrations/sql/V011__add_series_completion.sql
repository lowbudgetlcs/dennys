START TRANSACTION;
SET search_path = dennys;

ALTER TABLE series
  ADD COLUMN completed    BOOLEAN NOT NULL DEFAULT false,
  ADD COLUMN completed_at TIMESTAMPTZ,
  ADD COLUMN reopened_at  TIMESTAMPTZ;

UPDATE series s SET completed = true, completed_at = e.end_date
  FROM events e WHERE e.id = s.event_id AND e.end_date < now();

COMMIT;
