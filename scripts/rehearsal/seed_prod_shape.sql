-- Reproduces production's shape as audited 2026-08-10: 1308 series whose events have
-- all ended, 2178 tournament codes, 0 game_results. Series 1309-1320 sit in an event
-- that has not ended and must survive V011's backfill as completed = false.

SET search_path = dennys;

INSERT INTO event_groups (id, name) VALUES (1, 'Rehearsal');

INSERT INTO events (id, name, riot_tournament_id, start_date, end_date, event_group_id, status) VALUES
  (1, 'Season 14', 1401, '2025-01-06T00:00:00Z', '2025-06-30T00:00:00Z', 1, 'COMPLETED'),
  (2, 'Season 15', 1501, '2025-07-07T00:00:00Z', '2025-11-30T00:00:00Z', 1, 'COMPLETED'),
  (3, 'Season 16', 1601, '2026-01-05T00:00:00Z', '2026-05-31T00:00:00Z', 1, 'COMPLETED'),
  (4, 'Season 17', 1701, now() - interval '7 days', now() + interval '90 days', 1, 'ACTIVE');

INSERT INTO teams (id, name, event_id)
SELECT n, 'Team ' || n, ((n - 1) / 10) + 1
FROM generate_series(1, 40) AS n;

INSERT INTO series (id, event_id, total_games, stage)
SELECT n, ((n - 1) / 436) + 1, CASE WHEN n % 3 = 0 THEN 5 ELSE 3 END, 'REGULAR_SEASON'
FROM generate_series(1, 1308) AS n;

INSERT INTO series (id, event_id, total_games, stage)
SELECT n, 4, 3, 'PLAYOFFS'
FROM generate_series(1309, 1320) AS n;

INSERT INTO team_to_series (team_id, series_id)
SELECT ((s.event_id - 1) * 10) + 1 + (s.id % 5), s.id FROM series s
UNION ALL
SELECT ((s.event_id - 1) * 10) + 6 + (s.id % 5), s.id FROM series s;

INSERT INTO games (id, shortcode, blue_team_id, red_team_id, series_id)
SELECT n,
       'REH-' || lpad(n::text, 8, '0'),
       ((s.event_id - 1) * 10) + 1,
       ((s.event_id - 1) * 10) + 6,
       s.id
FROM generate_series(1, 2178) AS n
JOIN series s ON s.id = ((n - 1) % 1308) + 1;

INSERT INTO player_game_facts (side, shortcode)
SELECT CASE WHEN n % 2 = 0 THEN 100 ELSE 200 END, 'REH-' || lpad(n::text, 8, '0')
FROM generate_series(1, 50) AS n;

SELECT setval(pg_get_serial_sequence('dennys.events', 'id'), 4);
SELECT setval(pg_get_serial_sequence('dennys.teams', 'id'), 40);
SELECT setval(pg_get_serial_sequence('dennys.series', 'id'), 1320);
SELECT setval(pg_get_serial_sequence('dennys.games', 'id'), 2178);
