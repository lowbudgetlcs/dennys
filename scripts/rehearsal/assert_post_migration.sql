SET search_path = dennys;

DO $$
DECLARE
  n BIGINT;
BEGIN
  SELECT count(*) INTO n FROM information_schema.columns
    WHERE table_schema = 'dennys' AND table_name = 'series'
      AND column_name IN ('completed', 'completed_at', 'reopened_at');
  IF n <> 0 THEN RAISE EXCEPTION 'series still carries % of the V011 columns', n; END IF;

  SELECT count(*) INTO n FROM information_schema.columns
    WHERE table_schema = 'dennys' AND table_name = 'series'
      AND column_name = 'status' AND is_nullable = 'NO';
  IF n <> 1 THEN RAISE EXCEPTION 'series.status is missing or nullable'; END IF;

  SELECT count(*) INTO n FROM series WHERE status = 'COMPLETE';
  IF n <> 1308 THEN RAISE EXCEPTION 'expected 1308 COMPLETE series, found %', n; END IF;

  SELECT count(*) INTO n FROM series WHERE status = 'IN_PROGRESS';
  IF n <> 2 THEN RAISE EXCEPTION 'expected 2 IN_PROGRESS series with codes issued, found %', n; END IF;

  SELECT count(*) INTO n FROM series WHERE status = 'NOT_STARTED';
  IF n <> 10 THEN RAISE EXCEPTION 'expected 10 NOT_STARTED series, found %', n; END IF;

  SELECT count(*) INTO n FROM pg_constraint
    WHERE conrelid = 'dennys.series'::regclass AND conname = 'series_status_check';
  IF n <> 1 THEN RAISE EXCEPTION 'series_status_check is missing'; END IF;

  SELECT count(*) INTO n FROM tournament_codes;
  IF n <> 2180 THEN RAISE EXCEPTION 'expected 2180 tournament codes, found %', n; END IF;

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

  SELECT count(*) INTO n FROM flyway_schema_history WHERE success AND version IN ('011', '012', '013', '014');
  IF n <> 4 THEN RAISE EXCEPTION 'ledger does not record V011-V014 as applied (found %/4)', n; END IF;

  SELECT count(*) INTO n FROM flyway_schema_history WHERE type = 'BASELINE' AND version = '010';
  IF n <> 1 THEN RAISE EXCEPTION 'ledger has no baseline row at V010'; END IF;

  RAISE NOTICE 'all post-migration assertions passed';
END $$;
