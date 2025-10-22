START TRANSACTION;
SET search_path = dennys;

UPDATE series SET games_to_win = (games_to_win * 2) - 1;
ALTER TABLE series RENAME COLUMN games_to_win TO total_games;

COMMIT;
