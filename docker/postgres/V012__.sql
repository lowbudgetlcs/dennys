CREATE INDEX players_summoner_name_idx ON players (summoner_name);

CREATE INDEX teams_name_idx ON teams (name);

CREATE INDEX games_winner_id_idx ON games (winner_id);

CREATE INDEX games_loser_id_idx ON games (loser_id);

CREATE INDEX series_winner_id_idx ON series (winner_id);

CREATE INDEX series_loser_id_idx ON series (loser_id);

CREATE INDEX player_performances_player_id_idx ON player_performances (player_id);

CREATE INDEX player_performances_game_id_idx ON player_performances (game_id);

CREATE INDEX player_performances_team_id_idx ON player_performances (team_id);

CREATE INDEX player_performances_division_id_idx ON player_performances (division_id);

CREATE INDEX team_performances_team_id_idx ON team_performances (team_id);

CREATE INDEX team_performances_game_id_idx ON team_performances (game_id);

CREATE INDEX team_performances_division_id_idx ON team_performances (division_id);

CREATE INDEX games_series_id_idx ON games (series_id);

CREATE INDEX draft_lobbies_lobby_code_idx ON draft_lobbies (lobby_code);