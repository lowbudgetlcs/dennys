ALTER TABLE games
  ADD COLUMN series_id INT NOT NULL;

ALTER TABLE games
  ADD CONSTRAINT fk_series_id
  FOREIGN KEY (series_id)
  REFERENCES series(id);