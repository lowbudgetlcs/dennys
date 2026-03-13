package com.lowbudgetlcs.domain.player

import com.lowbudgetlcs.domain.account.models.types.AccountId
import com.lowbudgetlcs.domain.player.models.NewPlayer
import com.lowbudgetlcs.domain.player.models.Player
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.player.models.types.PlayerName

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
