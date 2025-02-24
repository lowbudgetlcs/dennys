package com.lowbudgetlcs.repositories.players

import com.lowbudgetlcs.entities.*
import com.lowbudgetlcs.repositories.IEntityRepository
import kotlinx.serialization.Serializable
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant

@Serializable
data class PlayerPerformanceId(val id: Int)

interface IPlayerRepository : IEntityRepository<Player, PlayerId> {
    fun createPlayerData(
        player: Player, game: Game, data: PlayerGameData
    ): Player

    fun readByPuuid(puuid: String): Player?
    fun fetchTeamId(participants: List<MatchParticipant>): TeamId?
}