package com.lowbudgetlcs.domain.player.core.port

import com.lowbudgetlcs.domain.account.core.model.types.AccountId
import com.lowbudgetlcs.domain.player.core.model.NewPlayer
import com.lowbudgetlcs.domain.player.core.model.Player
import com.lowbudgetlcs.domain.player.core.model.PlayerWithTeams
import com.lowbudgetlcs.domain.player.core.model.types.PlayerId
import com.lowbudgetlcs.domain.player.core.model.types.PlayerName

interface IPlayerService {
    fun getAllPlayers(): List<Player>

    fun getPlayer(id: PlayerId): Player

    fun getPlayerWithTeams(id: PlayerId): PlayerWithTeams

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
