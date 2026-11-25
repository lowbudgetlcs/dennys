package com.lowbudgetlcs.api.dto.stats

import com.lowbudgetlcs.domain.stats.models.GameStats
import com.lowbudgetlcs.domain.stats.models.PlayerGameStats
import com.lowbudgetlcs.domain.stats.models.TeamGameStats

fun GameStats.toDto(): GameStatsDto = GameStatsDto(players.map { it.toDto() }, teams.map { it.toDto() })

fun PlayerGameStats.toDto(): PlayerGameStatsDto =
    PlayerGameStatsDto(
        gameId = gameId.value,
        seriesId = seriesId.value,
        eventId = eventId.value,
        eventGroupId = eventGroupId?.value,
        stage = stage,
        teamId = teamId?.value,
        playerId = playerId?.value,
        gameDurationSeconds = gameDurationSeconds,
        puuid = puuid,
        riotIdGameName = riotIdGameName,
        riotIdTagline = riotIdTagline,
        championId = championId,
        championName = championName,
        position = position,
        win = win,
        kills = kills,
        deaths = deaths,
        assists = assists,
        cs = cs,
        goldEarned = goldEarned,
        champLevel = champLevel,
        damageToChampions = damageToChampions,
        damageTaken = damageTaken,
        visionScore = visionScore,
        wardsPlaced = wardsPlaced,
        wardsKilled = wardsKilled,
        controlWardsBought = controlWardsBought,
        voidGrubs = voidGrubs,
    )

fun TeamGameStats.toDto(): TeamGameStatsDto =
    TeamGameStatsDto(
        gameId = gameId.value,
        seriesId = seriesId.value,
        eventId = eventId.value,
        eventGroupId = eventGroupId?.value,
        stage = stage,
        teamId = teamId?.value,
        side = side,
        gameDurationSeconds = gameDurationSeconds,
        win = win,
        firstBlood = firstBlood,
        firstTower = firstTower,
        firstDragon = firstDragon,
        firstBaron = firstBaron,
        kills = kills,
        towerKills = towerKills,
        inhibitorKills = inhibitorKills,
        dragonKills = dragonKills,
        baronKills = baronKills,
        riftHeraldKills = riftHeraldKills,
        hordeKills = hordeKills,
        bans = bans,
    )
