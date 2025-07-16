CREATE TABLE meta (
  id INT PRIMARY KEY,
  season_name TEXT NOT NULL,
  provider_id INT NOT NULL
);

CREATE TABLE divisions (
  id SERIAL PRIMARY KEY,
  name TEXT UNIQUE NOT NULL,
  tournament_id INT NOT NULL
);