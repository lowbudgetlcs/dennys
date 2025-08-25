START TRANSACTION;

SET search_path = dennys;

ALTER TABLE event_groups ADD UNIQUE (name);
ALTER TABLE dennys.events ADD UNIQUE (name);

DROP TABLE IF EXISTS team_to_series;

CREATE TABLE team_to_series (
  team_id INTEGER NOT NULL,
  series_id INTEGER NOT NULL,
  PRIMARY KEY (team_id, series_id),

  CONSTRAINT team_to_series_team_id_fkey
    FOREIGN KEY (team_id) REFERENCES teams(id)
    ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT team_to_series_series_id_fkey
    FOREIGN KEY (series_id) REFERENCES series(id)
    ON UPDATE NO ACTION ON DELETE CASCADE
);

COMMIT;