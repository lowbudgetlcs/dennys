package com.lowbudgetlcs.repositories

import com.lowbudgetlcs.bridges.LblcsDatabaseBridge
import migrations.Games

class GameRepositoryImpl : GameRepository, Repository<Games, Int> {
    private val lblcs = LblcsDatabaseBridge.db
    override fun readAll(): List<Games> {
        TODO("Not yet implemented")
    }

    override fun readById(id: Int) = lblcs.gamesQueries.readById(id).executeAsOneOrNull()

    override fun create(entity: Games): Games {
        TODO("Not yet implemented")
    }

    override fun update(entity: Games): Games = TODO("Not yet implemented")

    override fun delete(entity: Games): Games {
        TODO("Not yet implemented")
    }

    override fun readByShortcode(shortCode: String) = lblcs.gamesQueries.readByShortcode(shortCode).executeAsOneOrNull()

    override fun updateWinnerLoserCallbackById(winnerId: Int, loserId: Int, callback: String, id: Int): Boolean =
        lblcs.gamesQueries.updateGame(
            winner_id = winnerId, loser_id = loserId, callback_result = callback, id = id
        ).executeAsOneOrNull() != null
}