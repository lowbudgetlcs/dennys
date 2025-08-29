package com.lowbudgetlcs.domain.services.player

import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.domain.models.riot.account.RiotAccountId

interface IPlayerService {
    fun getAllPlayers(): List<PlayerWithAccounts>
    fun getPlayer(id: PlayerId): PlayerWithAccounts
    fun createPlayer(player: NewPlayer): PlayerWithAccounts
    fun renamePlayer(playerId: PlayerId, newName: String): PlayerWithAccounts
    fun linkAccountToPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts
    fun unlinkAccountFromPlayer(playerId: PlayerId, accountId: RiotAccountId): PlayerWithAccounts
}