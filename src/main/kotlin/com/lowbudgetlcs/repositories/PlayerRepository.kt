package com.lowbudgetlcs.repositories

import migrations.Players

interface PlayerRepository {
    fun updateSummonerNameByPuuid(puuid: String, summonerName: String): Players?
    fun readByPuuid(puuid: String): Players?
}