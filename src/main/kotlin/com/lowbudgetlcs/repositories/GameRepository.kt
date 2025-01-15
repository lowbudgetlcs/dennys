package com.lowbudgetlcs.repositories

import migrations.Games

interface GameRepository {
    fun readByShortcode(shortCode: String): Games?
    fun updateWinnerLoserCallbackById(winnerId: Int, loserId: Int, callback: String, id: Int): Boolean
}