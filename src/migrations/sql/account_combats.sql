--info:particpants [0]

CREATE TABLE account_combats (

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
damageSelfMitigated NUMERIC(10,2)
);