package com.lowbudgetlcs.domain.models.riotaccount

data class RiotAccount(
    val id: Int,
    val riotPuuid: String,
    val playerId: Int?
)
