package com.lowbudgetlcs.entities


import kotlinx.serialization.Serializable

/**
 * Represents a team.
 */
@Serializable
data class Team(
    override val id: TeamId,
    val name: String,
    val logo: String?,
    val captain: PlayerId?,
    val division: DivisionId?,
    val teamData: List<TeamGameData>
) : Entity<TeamId>

/**
 * ID type for [Team]s.
 */
@Serializable
data class TeamId(val id: Int)

/**
 * In-game stats owned by a [Team].
 */
@Serializable
data class TeamGameData(
    val win: Boolean,
    val side: RiftSide,
    val gold: Int,
    val gameLength: Long,
    val kills: Objective,
    val barons: Objective,
    val grubs: Objective,
    val dragons: Objective,
    val heralds: Objective,
    val towers: Objective,
    val inhibitors: Objective
)

@Serializable
enum class RiftSide { BLUE, RED }

/**
 * Represents in-game objectives.
 */
@Serializable
data class Objective(
    val kills: Int = 0, val first: Boolean = false
)