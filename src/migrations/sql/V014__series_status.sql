START TRANSACTION;
SET search_path = dennys;

ALTER TABLE series ADD COLUMN status TEXT;

-- An issued code means play has started, even before any result is recorded.
UPDATE series s SET status = CASE
  WHEN s.completed THEN 'COMPLETE'
  WHEN EXISTS (SELECT 1 FROM games g WHERE g.series_id = s.id)
    OR EXISTS (SELECT 1 FROM tournament_codes c WHERE c.series_id = s.id) THEN 'IN_PROGRESS'
  ELSE 'NOT_STARTED'
END;

ALTER TABLE series
  ALTER COLUMN status SET NOT NULL,
  ALTER COLUMN status SET DEFAULT 'NOT_STARTED',
  ADD CONSTRAINT series_status_check
    CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETE', 'CANCELLED')),
  DROP COLUMN completed,
  DROP COLUMN completed_at,
  DROP COLUMN reopened_at;

COMMIT;
