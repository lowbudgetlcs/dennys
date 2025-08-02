package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.routes.dto.riot.account.AccountDto

interface IRiotAccountGateway {
    suspend fun getAccountByPuuid(puuid: String): AccountDto?
}