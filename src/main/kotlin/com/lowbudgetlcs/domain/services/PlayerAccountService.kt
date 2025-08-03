package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.domain.models.RiotPuuid
import com.lowbudgetlcs.gateways.IRiotAccountGateway
import com.lowbudgetlcs.repositories.IPlayerRepository
import com.lowbudgetlcs.routes.dto.riot.account.AccountDto

class PlayerAccountService(
    private val playerRepository: IPlayerRepository,
    private val riotAccountGateway: IRiotAccountGateway
) {

    suspend fun addAccountToPlayer(playerId: PlayerId, riotPuuid: String): PlayerWithAccounts? {
        riotAccountGateway.getAccountByPuuid(riotPuuid)
            ?: throw IllegalArgumentException("Riot account not found")

        val allPlayers = playerRepository.getAll()
        val alreadyOwned = allPlayers.any { player ->
            player.accounts.any { it.riotPuuid.value == riotPuuid && player.id != playerId }
        }
        if (alreadyOwned) throw IllegalStateException("Riot account already assigned to a different player")

        // Create an Account record, it returns an account object
        val riotAccount = playerRepository.createAccountRecord(RiotPuuid(riotPuuid))

        // Assign to player
        return playerRepository.insertAccountToPlayer(playerId, riotAccount.id)
    }


}