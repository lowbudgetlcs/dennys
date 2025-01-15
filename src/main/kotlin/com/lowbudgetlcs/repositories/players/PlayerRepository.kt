package com.lowbudgetlcs.repositories.players

import migrations.Players

interface PlayerRepository {
    fun updateSummonerNameByPuuid(puuid: String, summonerName: String): Players?
    fun readByPuuid(puuid: String): Players?
}