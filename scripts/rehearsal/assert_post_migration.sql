SET search_path = dennys;

DO $$
DECLARE
  n BIGINT;
BEGIN
  SELECT count(*) INTO n FROM information_schema.columns
    WHERE table_schema = 'dennys' AND table_name = 'series'
      AND column_name IN ('completed', 'completed_at', 'reopened_at');
  IF n <> 3 THEN RAISE EXCEPTION 'series is missing V011 columns (found %/3)', n; END IF;

  SELECT count(*) INTO n FROM series WHERE completed;
  IF n <> 1308 THEN RAISE EXCEPTION 'expected 1308 backfilled series, found %', n; END IF;

  SELECT count(*) INTO n FROM series WHERE NOT completed;
  IF n <> 12 THEN RAISE EXCEPTION 'expected 12 open series in the unfinished event, found %', n; END IF;

  SELECT count(*) INTO n FROM series WHERE completed AND completed_at IS NULL;
  IF n <> 0 THEN RAISE EXCEPTION '% backfilled series have a null completed_at', n; END IF;

  SELECT count(*) INTO n FROM series s JOIN events e ON e.id = s.event_id
    WHERE s.completed AND s.completed_at <> e.end_date;
  IF n <> 0 THEN RAISE EXCEPTION '% series were not stamped with their event end_date', n; END IF;

  SELECT count(*) INTO n FROM series WHERE reopened_at IS NOT NULL;
  IF n <> 0 THEN RAISE EXCEPTION 'backfill wrote reopened_at on % series', n; END IF;

  SELECT count(*) INTO n FROM tournament_codes;
  IF n <> 2178 THEN RAISE EXCEPTION 'expected 2178 tournament codes, found %', n; END IF;

  SELECT count(*) INTO n FROM information_schema.columns
    WHERE table_schema = 'dennys' AND table_name = 'tournament_codes' AND column_name = 'number';
  IF n <> 0 THEN RAISE EXCEPTION 'tournament_codes still carries the number column'; END IF;

  SELECT count(*) INTO n FROM information_schema.columns
    WHERE table_schema = 'dennys' AND table_name = 'tournament_codes'
      AND column_name = 'created_at' AND is_nullable = 'NO';
  IF n <> 1 THEN RAISE EXCEPTION 'tournament_codes.created_at is missing or nullable'; END IF;

  SELECT count(*) INTO n FROM tournament_codes WHERE created_at IS NULL;
  IF n <> 0 THEN RAISE EXCEPTION '% tournament codes have a null created_at', n; END IF;

  IF to_regclass('dennys.games') IS NULL THEN RAISE EXCEPTION 'games was not recreated'; END IF;

  SELECT count(*) INTO n FROM games;
  IF n <> 0 THEN RAISE EXCEPTION 'the new games table is not empty (% rows)', n; END IF;

  SELECT count(*) INTO n FROM pg_constraint
    WHERE conrelid = 'dennys.games'::regclass AND conname = 'games_series_number_unique';
  IF n <> 1 THEN RAISE EXCEPTION 'games_series_number_unique is missing'; END IF;

  SELECT count(*) INTO n FROM pg_constraint
    WHERE conname = 'game_results_game_id_fkey'
      AND conrelid = 'dennys.game_results'::regclass
      AND confrelid = 'dennys.games'::regclass;
  IF n <> 1 THEN RAISE EXCEPTION 'game_results_game_id_fkey does not point at the new games'; END IF;

  SELECT count(*) INTO n FROM pg_trigger t
    JOIN pg_class c ON c.oid = t.tgrelid
    JOIN pg_namespace ns ON ns.oid = c.relnamespace
    WHERE ns.nspname = 'dennys' AND t.tgname = 'set_game_number';
  IF n <> 0 THEN RAISE EXCEPTION 'the set_game_number trigger survived'; END IF;

  SELECT count(*) INTO n FROM pg_proc p
    JOIN pg_namespace ns ON ns.oid = p.pronamespace
    WHERE ns.nspname = 'dennys' AND p.proname = 'assign_game_number';
  IF n <> 0 THEN RAISE EXCEPTION 'assign_game_number() survived'; END IF;

  SELECT count(*) INTO n FROM pg_class c
    JOIN pg_namespace ns ON ns.oid = c.relnamespace
    WHERE ns.nspname = 'dennys'
      AND c.relname IN ('tournament_codes_pkey', 'tournament_codes_shortcode_key', 'tournament_codes_id_seq');
  IF n <> 3 THEN RAISE EXCEPTION 'renamed indexes/sequence incomplete (found %/3)', n; END IF;

  SELECT count(*) INTO n FROM pg_constraint
    WHERE conrelid = 'dennys.tournament_codes'::regclass
      AND conname IN ('tournament_codes_blue_team_id_fkey',
                      'tournament_codes_red_team_id_fkey',
                      'tournament_codes_series_id_fkey');
  IF n <> 3 THEN RAISE EXCEPTION 'renamed foreign keys incomplete (found %/3)', n; END IF;

  SELECT count(*) INTO n FROM player_game_facts;
  IF n <> 50 THEN RAISE EXCEPTION 'player_game_facts lost rows (% of 50)', n; END IF;

  SELECT count(*) INTO n FROM flyway_schema_history WHERE success AND version IN ('011', '012');
  IF n <> 2 THEN RAISE EXCEPTION 'ledger does not record V011 and V012 as applied (found %/2)', n; END IF;

  SELECT count(*) INTO n FROM flyway_schema_history WHERE type = 'BASELINE' AND version = '010';
  IF n <> 1 THEN RAISE EXCEPTION 'ledger has no baseline row at V010'; END IF;

  RAISE NOTICE 'all post-migration assertions passed';
END $$;
