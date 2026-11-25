package com.lowbudgetlcs.api.dto.stats

import com.lowbudgetlcs.domain.stats.models.ChampionAggregate
import com.lowbudgetlcs.domain.stats.models.PlayerAggregate
import com.lowbudgetlcs.domain.stats.models.TeamAggregate
import com.lowbudgetlcs.domain.stats.models.TeamRecord

private fun Long.per(games: Int): Double = if (games == 0) 0.0 else toDouble() / games

fun PlayerAggregate.toDto(): PlayerAggregateDto =
    PlayerAggregateDto(
        playerId = playerId.value,
        playerName = playerName,
        games = games,
        wins = wins,
        seriesPlayed = seriesPlayed,
        seriesWon = seriesWon,
        seriesLost = seriesLost,
        kills = totals.kills,
        killsPerGame = totals.kills.per(games),
        deaths = totals.deaths,
        deathsPerGame = totals.deaths.per(games),
        assists = totals.assists,
        assistsPerGame = totals.assists.per(games),
        cs = totals.cs,
        csPerGame = totals.cs.per(games),
        goldEarned = totals.goldEarned,
        goldEarnedPerGame = totals.goldEarned.per(games),
        damageToChampions = totals.damageToChampions,
        damageToChampionsPerGame = totals.damageToChampions.per(games),
        damageTaken = totals.damageTaken,
        damageTakenPerGame = totals.damageTaken.per(games),
        visionScore = totals.visionScore,
        visionScorePerGame = totals.visionScore.per(games),
        wardsPlaced = totals.wardsPlaced,
        wardsPlacedPerGame = totals.wardsPlaced.per(games),
        wardsKilled = totals.wardsKilled,
        wardsKilledPerGame = totals.wardsKilled.per(games),
        controlWardsBought = totals.controlWardsBought,
        controlWardsBoughtPerGame = totals.controlWardsBought.per(games),
        voidGrubs = totals.voidGrubs,
        voidGrubsPerGame = totals.voidGrubs.per(games),
        gameDurationSeconds = totals.gameDurationSeconds,
        gameDurationSecondsPerGame = totals.gameDurationSeconds.per(games),
    )

fun ChampionAggregate.toDto(): ChampionAggregateDto =
    ChampionAggregateDto(
        championId = championId,
        championName = championName,
        picks = picks,
        wins = wins,
        bans = bans,
        kills = totals.kills,
        killsPerGame = totals.kills.per(picks),
        deaths = totals.deaths,
        deathsPerGame = totals.deaths.per(picks),
        assists = totals.assists,
        assistsPerGame = totals.assists.per(picks),
        cs = totals.cs,
        csPerGame = totals.cs.per(picks),
        goldEarned = totals.goldEarned,
        goldEarnedPerGame = totals.goldEarned.per(picks),
        damageToChampions = totals.damageToChampions,
        damageToChampionsPerGame = totals.damageToChampions.per(picks),
        damageTaken = totals.damageTaken,
        damageTakenPerGame = totals.damageTaken.per(picks),
        visionScore = totals.visionScore,
        visionScorePerGame = totals.visionScore.per(picks),
        wardsPlaced = totals.wardsPlaced,
        wardsPlacedPerGame = totals.wardsPlaced.per(picks),
        wardsKilled = totals.wardsKilled,
        wardsKilledPerGame = totals.wardsKilled.per(picks),
        controlWardsBought = totals.controlWardsBought,
        controlWardsBoughtPerGame = totals.controlWardsBought.per(picks),
        voidGrubs = totals.voidGrubs,
        voidGrubsPerGame = totals.voidGrubs.per(picks),
        gameDurationSeconds = totals.gameDurationSeconds,
        gameDurationSecondsPerGame = totals.gameDurationSeconds.per(picks),
    )

fun TeamAggregate.toDto(): TeamAggregateDto =
    TeamAggregateDto(
        teamId = teamId.value,
        teamName = teamName,
        games = games,
        wins = wins,
        firstBloods = firstBloods,
        firstBloodsPerGame = firstBloods.per(games),
        firstTowers = firstTowers,
        firstTowersPerGame = firstTowers.per(games),
        firstDragons = firstDragons,
        firstDragonsPerGame = firstDragons.per(games),
        firstBarons = firstBarons,
        firstBaronsPerGame = firstBarons.per(games),
        kills = kills,
        killsPerGame = kills.per(games),
        towerKills = towerKills,
        towerKillsPerGame = towerKills.per(games),
        inhibitorKills = inhibitorKills,
        inhibitorKillsPerGame = inhibitorKills.per(games),
        dragonKills = dragonKills,
        dragonKillsPerGame = dragonKills.per(games),
        baronKills = baronKills,
        baronKillsPerGame = baronKills.per(games),
        riftHeraldKills = riftHeraldKills,
        riftHeraldKillsPerGame = riftHeraldKills.per(games),
        hordeKills = hordeKills,
        hordeKillsPerGame = hordeKills.per(games),
        gameDurationSeconds = gameDurationSeconds,
        gameDurationSecondsPerGame = gameDurationSeconds.per(games),
    )

fun TeamRecord.toDto(): TeamRecordDto =
    TeamRecordDto(
        teamId = teamId.value,
        teamName = teamName,
        seriesWon = seriesWon,
        seriesLost = seriesLost,
        gamesWon = gamesWon,
        gamesLost = gamesLost,
    )
