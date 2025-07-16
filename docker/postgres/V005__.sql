CREATE TABLE series (
  id SERIAL PRIMARY KEY,
  division_id INT NOT NULL,
  winner_id INT,
  loser_id INT
);

ALTER TABLE series
  ADD CONSTRAINT fk_division_id
  FOREIGN KEY (division_id)
  REFERENCES divisions(id);

ALTER TABLE series
  ADD CONSTRAINT fk_winner_id
  FOREIGN KEY (winner_id)
  REFERENCES teams(id);

ALTER TABLE series
  ADD CONSTRAINT fk_loser_id
  FOREIGN KEY (loser_id)
  REFERENCES teams(id);

CREATE TABLE team_to_series (
  id SERIAL PRIMARY KEY,
  team_id INT NOT NULL,
  series_id INT NOT NULL
);

ALTER TABLE team_to_series
  ADD CONSTRAINT fk_team_id
  FOREIGN KEY (team_id)
  REFERENCES teams(id);

ALTER TABLE team_to_series
  ADD CONSTRAINT fk_series_id
  FOREIGN KEY (series_id)
  REFERENCES series(id);