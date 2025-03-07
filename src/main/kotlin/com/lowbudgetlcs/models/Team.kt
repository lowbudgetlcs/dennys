package com.lowbudgetlcs.models


import kotlinx.serialization.Serializable

/**
 * Represents a team.
 */
@Serializable
data class Team(
    val id: TeamId,
    val name: String,
    val logo: String?,
    val captain: PlayerId?,
    val division: DivisionId?,
    val teamData: List<TeamGameData>
)

/**
 * ID type for [Team]s.
 */
@Serializable
data class TeamId(val id: Int)

// TODO: Same comment as 'Player.kt'- this data should be queryable when needed. This solution is so heavy....

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