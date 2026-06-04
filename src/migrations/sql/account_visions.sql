--info:particpants [0]

CREATE TABLE account_visions (

riotIdGameName TEXT(90) NOT NULL,
riotIdTagline TEXT(60),

controlWardsPlaced INTEGER,
stealthWardsPlaced INTEGER,
wardTakedowns INTEGER,
wardTakedownsBefore20M INTEGER,
wardsGuarded INTEGER,
detectorWardsPlaced INTEGER,
sightWardsBoughtInGame INTEGER,
visionWardsBoughtInGame INTEGER,
wardsKilled INTEGER,
wardsPlaced INTEGER,

visionScoreAdvantageLaneOpponent NUMERIC(10,2),
visionScorePerMinute NUMERIC(10,2),
visionScore INTEGER
);