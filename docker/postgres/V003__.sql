CREATE TABLE teams (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  logo TEXT,
  captain_id INT,
  division_id INT
);

ALTER TABLE teams
  ADD CONSTRAINT fk_captain_id
  FOREIGN KEY (captain_id)
  REFERENCES players(id)
  ON DELETE SET NULL;

ALTER TABLE players 
  ADD COLUMN team_id INT
  CONSTRAINT fk_team_id
  REFERENCES teams(id)
  ON DELETE SET NULL;

ALTER TABLE teams
  ADD CONSTRAINT fk_division_id
  FOREIGN KEY (division_id)
  REFERENCES divisions(id);