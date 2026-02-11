package com.lowbudgetlcs.domain.services.player

import com.lowbudgetlcs.domain.models.player.NewPlayer
import com.lowbudgetlcs.domain.models.player.Player
import com.lowbudgetlcs.domain.models.player.PlayerId
import com.lowbudgetlcs.domain.models.player.PlayerName
import com.lowbudgetlcs.domain.models.player.account.AccountId

interface IPlayerService {
    fun getAllPlayers(): List<Player>

    fun getPlayer(id: PlayerId): Player

    fun createPlayer(player: NewPlayer): Player

    fun renamePlayer(
        playerId: PlayerId,
        newName: PlayerName,
    ): Player

    fun linkAccountToPlayer(
        playerId: PlayerId,
        accountId: AccountId,
    ): Player

    fun unlinkAccountFromPlayer(
        playerId: PlayerId,
        accountId: AccountId,
    ): Player
}
