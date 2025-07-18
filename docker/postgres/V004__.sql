CREATE TABLE games (
  id SERIAL PRIMARY KEY,
  shortcode TEXT UNIQUE NOT NULL,
  game_num INT NOT NULL,
  winner_id INT,
  loser_id INT,
  callback_result JSONB,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

ALTER TABLE games
  ADD CONSTRAINT fk_winner_id
  FOREIGN KEY (winner_id)
  REFERENCES teams(id);

ALTER TABLE games
  ADD CONSTRAINT fk_loser_id
  FOREIGN KEY (loser_id)
  REFERENCES teams(id);