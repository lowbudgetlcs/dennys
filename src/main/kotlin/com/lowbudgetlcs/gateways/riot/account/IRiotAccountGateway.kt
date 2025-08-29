package com.lowbudgetlcs.gateways.riot.account

import com.lowbudgetlcs.domain.models.riot.RiotApiException
import com.lowbudgetlcs.api.dto.riot.account.RiotAccountDto

interface IRiotAccountGateway {

    /**
     * @throws IllegalArgumentException if the PUUID is invalid (400)
     * @throws NoSuchElementException if no Riot account is found (404)
     * @throws RiotApiException for other Riot API failures
     */
    suspend fun getAccountByPuuid(puuid: String): RiotAccountDto
}