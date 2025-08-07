START TRANSACTION;
SET SEARCH_PATH = dennys;

ALTER TABLE riot_accounts 
  ADD COLUMN player_id INTEGER
  REFERENCES players(id);

ALTER TABLE players
  DROP COLUMN main_account_id;

DROP TABLE IF EXISTS riot_accounts_to_player;

COMMIT;
