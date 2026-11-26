START TRANSACTION;
SET search_path = dennys;

-- Superseded by riot_match_data and the _v1 stats views.
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM player_game_facts) OR EXISTS (SELECT 1 FROM player_champ_selects)
    OR EXISTS (SELECT 1 FROM player_inventories) OR EXISTS (SELECT 1 FROM player_combats)
    OR EXISTS (SELECT 1 FROM player_vision) OR EXISTS (SELECT 1 FROM player_pings)
    OR EXISTS (SELECT 1 FROM player_farming) THEN
    RAISE EXCEPTION 'V018 refuses to drop the V001 stats tables because they hold rows';
  END IF;
END $$;

DROP TABLE player_game_facts, player_champ_selects, player_inventories, player_combats,
  player_vision, player_pings, player_farming;

COMMIT;
