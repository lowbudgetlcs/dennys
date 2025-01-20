package com.lowbudgetlcs.models


import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import no.stelar7.api.r4j.basic.constants.types.lol.TeamType
import no.stelar7.api.r4j.pojo.lol.match.v5.ObjectiveStats

@Serializable
data class TeamId(val id: Int)

@Serializable
data class Team(
    val id: TeamId,
    val name: String,
    val logo: String?,
    val captain: PlayerId?,
    val division: DivisionId?,
    val teamData: List<TeamGameData>
)

@Serializable
data class TeamGameData(
    val win: Boolean,
    val side: TeamType,
    val gold: Int,
    val gameLength: Long,
    val kills: Int,
    @Contextual val barons: ObjectiveStats,
    @Contextual val dragons: ObjectiveStats,
    @Contextual val horde: ObjectiveStats,
    @Contextual val riftHerald: ObjectiveStats,
    @Contextual val towers: ObjectiveStats,
    @Contextual val inhibitors: ObjectiveStats,
)