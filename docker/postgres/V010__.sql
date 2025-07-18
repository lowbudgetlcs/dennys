ALTER TABLE player_performances ALTER COLUMN player_id SET NOT NULL;

ALTER TABLE player_performances ALTER COLUMN game_id SET NOT NULL;

ALTER TABLE player_performances ALTER COLUMN team_id SET NOT NULL;

ALTER TABLE player_performances ALTER COLUMN division_id SET NOT NULL;

ALTER TABLE player_performances ADD UNIQUE (player_id, game_id);

ALTER TABLE player_performances ALTER COLUMN game_id SET NOT NULL;

ALTER TABLE player_performances ALTER COLUMN team_id SET NOT NULL;

ALTER TABLE player_performances ALTER COLUMN division_id SET NOT NULL;

ALTER TABLE team_performances ADD UNIQUE (team_id, game_id);

ALTER TABLE player_game_data ALTER COLUMN player_performance_id SET NOT NULL;

ALTER TABLE player_game_data ALTER COLUMN champion_name TYPE TEXT;

ALTER TABLE team_game_data ALTER COLUMN team_performance_id SET NOT NULL;