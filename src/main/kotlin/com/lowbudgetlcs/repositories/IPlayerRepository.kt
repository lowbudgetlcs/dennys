package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.models.*
import com.lowbudgetlcs.models.match.MatchParticipant


interface IPlayerRepository {
    /* Returns all Players from storage */
    fun getAll(): List<Player>

    /* Fetch a player by id, returns null if not found */
    fun get(id: PlayerId): Player?

    /* Fetch a player by puuid, returns null if not found */
    fun get(puuid: String): Player?

    /* Update a player in storage, returns null if operations fails */
    fun update(entity: Player): Player?

    /**
     * Saves [data] owned by [player] derived from [game] to storage. Returns [player] with
     * the new data if it succeeds, null otherwise.
     */
    fun savePlayerData(
        player: Player, game: Game, data: PlayerGameData
    ): Player?

    /**
     * Returns a [TeamId] if any member of [participants] belongs to a [Team] in
     * storage, null otherwise.
     */
    fun fetchTeamId(participants: List<MatchParticipant>): TeamId?
}