START TRANSACTION;
SET search_path = dennys;

-- The only readers of Riot's JSON shape. Missing keys read as 0 so Riot dropping a field never breaks
-- a row. Columns may be added; renaming or removing one means a _v2. Event context comes through
-- series, never teams, because teams.event_id is going away. Teams come from the PUUID-resolved
-- game result, since nothing stops a lobby seating teams on the sides other than the code's;
-- the code's sides are only the fallback for a game with no result.
CREATE VIEW player_game_stats_v1 AS
SELECT
  g.id AS game_id,
  g.series_id,
  s.event_id,
  e.event_group_id,
  s.stage,
  COALESCE(
    CASE WHEN (p->>'win')::boolean THEN gr.winner_team_id ELSE gr.loser_team_id END,
    CASE (p->>'teamId')::int WHEN 100 THEN tc.blue_team_id WHEN 200 THEN tc.red_team_id END
  ) AS team_id,
  ra.player_id,
  COALESCE((rmd.match->'info'->>'gameDuration')::int, 0) AS game_duration_s,
  p->>'puuid' AS puuid,
  p->>'riotIdGameName' AS riot_id_game_name,
  p->>'riotIdTagline' AS riot_id_tagline,
  COALESCE((p->>'championId')::int, 0) AS champion_id,
  p->>'championName' AS champion_name,
  p->>'teamPosition' AS position,
  COALESCE((p->>'win')::boolean, false) AS win,
  COALESCE((p->>'kills')::int, 0) AS kills,
  COALESCE((p->>'deaths')::int, 0) AS deaths,
  COALESCE((p->>'assists')::int, 0) AS assists,
  COALESCE((p->>'totalMinionsKilled')::int, 0) + COALESCE((p->>'neutralMinionsKilled')::int, 0) AS cs,
  COALESCE((p->>'goldEarned')::int, 0) AS gold_earned,
  COALESCE((p->>'champLevel')::int, 0) AS champ_level,
  COALESCE((p->>'totalDamageDealtToChampions')::int, 0) AS damage_to_champions,
  COALESCE((p->>'totalDamageTaken')::int, 0) AS damage_taken,
  COALESCE((p->>'visionScore')::int, 0) AS vision_score,
  COALESCE((p->>'wardsPlaced')::int, 0) AS wards_placed,
  COALESCE((p->>'wardsKilled')::int, 0) AS wards_killed,
  COALESCE((p->>'visionWardsBoughtInGame')::int, 0) AS control_wards_bought
FROM riot_match_data rmd
JOIN games g ON g.id = rmd.game_id
JOIN series s ON s.id = g.series_id
JOIN events e ON e.id = s.event_id
LEFT JOIN tournament_codes tc ON tc.id = g.tournament_code_id
LEFT JOIN game_results gr ON gr.game_id = g.id
CROSS JOIN LATERAL jsonb_array_elements(COALESCE(rmd.match->'info'->'participants', '[]'::jsonb)) AS p
LEFT JOIN riot_accounts ra ON ra.riot_puuid = p->>'puuid';

CREATE VIEW team_game_stats_v1 AS
SELECT
  g.id AS game_id,
  g.series_id,
  s.event_id,
  e.event_group_id,
  s.stage,
  COALESCE(
    CASE WHEN (t->>'win')::boolean THEN gr.winner_team_id ELSE gr.loser_team_id END,
    CASE (t->>'teamId')::int WHEN 100 THEN tc.blue_team_id WHEN 200 THEN tc.red_team_id END
  ) AS team_id,
  CASE (t->>'teamId')::int WHEN 100 THEN 'BLUE' WHEN 200 THEN 'RED' END AS side,
  COALESCE((rmd.match->'info'->>'gameDuration')::int, 0) AS game_duration_s,
  COALESCE((t->>'win')::boolean, false) AS win,
  COALESCE((t#>>'{objectives,champion,first}')::boolean, false) AS first_blood,
  COALESCE((t#>>'{objectives,tower,first}')::boolean, false) AS first_tower,
  COALESCE((t#>>'{objectives,dragon,first}')::boolean, false) AS first_dragon,
  COALESCE((t#>>'{objectives,baron,first}')::boolean, false) AS first_baron,
  COALESCE((t#>>'{objectives,champion,kills}')::int, 0) AS kills,
  COALESCE((t#>>'{objectives,tower,kills}')::int, 0) AS tower_kills,
  COALESCE((t#>>'{objectives,inhibitor,kills}')::int, 0) AS inhibitor_kills,
  COALESCE((t#>>'{objectives,dragon,kills}')::int, 0) AS dragon_kills,
  COALESCE((t#>>'{objectives,baron,kills}')::int, 0) AS baron_kills,
  COALESCE((t#>>'{objectives,riftHerald,kills}')::int, 0) AS rift_herald_kills,
  COALESCE((t#>>'{objectives,horde,kills}')::int, 0) AS horde_kills,
  COALESCE((t#>>'{objectives,atakhan,kills}')::int, 0) AS atakhan_kills,
  ARRAY(
    SELECT (b->>'championId')::int
    FROM jsonb_array_elements(COALESCE(t->'bans', '[]'::jsonb)) AS b
    WHERE COALESCE((b->>'championId')::int, -1) <> -1
    ORDER BY (b->>'pickTurn')::int
  ) AS bans
FROM riot_match_data rmd
JOIN games g ON g.id = rmd.game_id
JOIN series s ON s.id = g.series_id
JOIN events e ON e.id = s.event_id
LEFT JOIN tournament_codes tc ON tc.id = g.tournament_code_id
LEFT JOIN game_results gr ON gr.game_id = g.id
CROSS JOIN LATERAL jsonb_array_elements(COALESCE(rmd.match->'info'->'teams', '[]'::jsonb)) AS t;

COMMIT;
