--info:particpants [0]

CREATE TABLE account_visions (

player_id INTEGER,
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
visionScore INTEGER,

CONSTRAINT account_player_id
FOREIGN KEY (player_id) REFERENCES players(id)
);

CREATE TABLE account_pings (

player_id INTEGER,

allInPings INTEGER,
assistMePings INTEGER,
basicPings INTEGER,

commandPings INTEGER,
dangerPings INTEGER,
enemyMissingPings INTEGER,
enemyVisionPings INTEGER,
holdPings INTEGER,
getBackPings INTEGER,
needVisionPings INTEGER,
onMyWayPings INTEGER,
pushPings INTEGER,
retreatPings INTEGER,

CONSTRAINT account_player_id
FOREIGN KEY (player_id) REFERENCES players(id)
);

CREATE TABLE account_combats (

player_id INTEGER,

spell1Casts INTEGER,
spell2Casts INTEGER,
spell3Casts INTEGER,
spell4Casts INTEGER,

kills INTEGER,
deaths INTEGER,
doubleKills INTEGER,
tripleKills INTEGER,
quadraKills INTEGER,
pentaKills INTEGER,

magicDamageDealt NUMERIC(10,2),
magicDamageTaken NUMERIC(10,2),
physicalDamageDealt NUMERIC(10,2),
physicalDamageTaken NUMERIC(10,2),

firstBloodAssist BOOLEAN,
firstBloodKill BOOLEAN,
goldEarned NUMERIC(10,2),

objectivesStolen INTEGER,
dragonKills INTEGER,

damageDealtToTurrets NUMERIC(10,2),
damageSelfMitigated NUMERIC(10,2),

CONSTRAINT account_player_id
FOREIGN KEY (player_id) REFERENCES players(id)
);