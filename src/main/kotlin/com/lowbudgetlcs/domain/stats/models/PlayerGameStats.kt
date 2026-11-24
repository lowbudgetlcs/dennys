package com.lowbudgetlcs.domain.stats.models

import com.lowbudgetlcs.domain.event.models.types.EventId
import com.lowbudgetlcs.domain.event.models.types.EventStage
import com.lowbudgetlcs.domain.eventgroup.models.types.EventGroupId
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.series.game.models.types.GameId
import com.lowbudgetlcs.domain.series.models.types.SeriesId
import com.lowbudgetlcs.domain.team.models.types.TeamId

data class PlayerGameStats(
    val gameId: GameId,
    val seriesId: SeriesId,
    val eventId: EventId,
    val eventGroupId: EventGroupId?,
    val stage: EventStage,
    val teamId: TeamId?,
    val playerId: PlayerId?,
    val gameDurationSeconds: Int,
    val puuid: String?,
    val riotIdGameName: String?,
    val riotIdTagline: String?,
    val championId: Int,
    val championName: String?,
    val position: String?,
    val win: Boolean,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val cs: Int,
    val goldEarned: Int,
    val champLevel: Int,
    val damageToChampions: Int,
    val damageTaken: Int,
    val visionScore: Int,
    val wardsPlaced: Int,
    val wardsKilled: Int,
    val controlWardsBought: Int,
    val voidGrubs: Int,
)
