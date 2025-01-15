package com.lowbudgetlcs.repositories.games

import migrations.Games

interface GameRepository {
    fun readByShortcode(shortCode: String): Games?
    fun updateWinnerLoserCallbackById(winnerId: Int, loserId: Int, callback: String, id: Int): Boolean
    fun countTeamWinsBySeries(seriesId: Int, winnerId: Int): Int
}