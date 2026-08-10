-- V012 re-adds game_results_game_id_fkey against a brand-new empty games table and Postgres
-- validates it immediately, so a single pre-existing row aborts the migration. Production was
-- audited at 0 rows; this leg proves CI notices when that stops being true.

SET search_path = dennys;

INSERT INTO game_results (game_id, winner_team_id, loser_team_id) VALUES (1, 1, 6);
