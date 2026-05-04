package com.lowbudgetlcs.domain.player.core.port

import com.lowbudgetlcs.domain.account.core.model.types.AccountId
import com.lowbudgetlcs.domain.player.core.model.NewPlayer
import com.lowbudgetlcs.domain.player.core.model.Player
import com.lowbudgetlcs.domain.player.core.model.PlayerWithTeams
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerName

interface IPlayerService {
    suspend fun getAllPlayers(): List<Player>
    suspend fun getPlayer(id: PlayerId): Player
    suspend fun getPlayerWithTeams(id: PlayerId): PlayerWithTeams
    suspend fun createPlayer(player: NewPlayer): Player
    suspend fun renamePlayer(
        playerId: PlayerId,
        newName: PlayerName,
    ): Player
    suspend fun linkAccountToPlayer(
        playerId: PlayerId,
        accountId: AccountId,
    ): Player
    suspend fun unlinkAccountFromPlayer(
        playerId: PlayerId,
        accountId: AccountId,
    ): Player
}
