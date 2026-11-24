START TRANSACTION;
SET search_path = dennys;

-- Riot payloads are stored as-is; versioned views are the only readers of their shape.
CREATE TABLE riot_match_data (
  game_id INTEGER PRIMARY KEY REFERENCES games(id),
  match JSONB NOT NULL,
  timeline JSONB,
  fetched_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

COMMIT;
