package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.entities.*
import com.lowbudgetlcs.repositories.IEntityRepository
import kotlinx.serialization.Serializable
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant

@Serializable
data class PlayerPerformanceId(val id: Int)

interface IPlayerRepository : IEntityRepository<Player, PlayerId> {
    /**
     * Saves [data] owned by [player] from [game] to storage. Returns [player] with
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