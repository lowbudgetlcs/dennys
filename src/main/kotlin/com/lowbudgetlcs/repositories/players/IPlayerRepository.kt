package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.models.*
import com.lowbudgetlcs.models.match.MatchParticipant
import com.lowbudgetlcs.repositories.IRepository
import com.lowbudgetlcs.repositories.IUniqueRepository


interface IPlayerRepository : IUniqueRepository<Player, PlayerId>, IRepository<Player> {
    /**
     * Saves [data] owned by [player] derived from [game] to storage. Returns [player] with
     * the new data if it succeeds, null otherwise.
     */
    fun savePlayerData(
        player: Player, game: Game, data: PlayerGameData
    ): Player?

    /**
     * Returns a [Player] that matches the given [puuid].
     */
    fun readByPuuid(puuid: String): Player?

    /**
     * Returns a [TeamId] if any member of [participants] belongs to a [Team] in
     * storage, null otherwise.
     */
    fun fetchTeamId(participants: List<MatchParticipant>): TeamId?
}