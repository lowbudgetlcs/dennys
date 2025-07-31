package com.lowbudgetlcs.domain.services

import com.lowbudgetlcs.domain.models.NewPlayer
import com.lowbudgetlcs.domain.models.PlayerId
import com.lowbudgetlcs.domain.models.PlayerWithAccounts
import com.lowbudgetlcs.repositories.IPlayerRepository

class PlayerService(private val playerRepository: IPlayerRepository) {
    fun createPlayer(player: NewPlayer): PlayerWithAccounts? {
        return playerRepository.insert(player)
    }

    fun getPlayer(id: PlayerId): PlayerWithAccounts? {
        return playerRepository.getById(id)
    }

    fun getAllPlayers(): List<PlayerWithAccounts> = playerRepository.getAll()
}
