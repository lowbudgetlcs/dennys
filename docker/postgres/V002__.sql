CREATE TABLE players (
  id SERIAL PRIMARY KEY,
  riot_puuid CHAR(78) UNIQUE NOT NULL,
  summoner_name TEXT NOT NULL
);