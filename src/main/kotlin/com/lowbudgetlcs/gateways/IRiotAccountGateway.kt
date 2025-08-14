package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.domain.models.riot.RiotApiException
import com.lowbudgetlcs.routes.dto.riot.account.AccountDto

interface IRiotAccountGateway {

    /**
     * @throws IllegalArgumentException if the PUUID is invalid (400)
     * @throws NoSuchElementException if no Riot account is found (404)
     * @throws RiotApiException for other Riot API failures
     */
    suspend fun getAccountByPuuid(puuid: String): AccountDto
}