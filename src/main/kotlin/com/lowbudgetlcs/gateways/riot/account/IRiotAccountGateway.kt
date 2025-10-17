package com.lowbudgetlcs.gateways.riot.account

import com.lowbudgetlcs.api.dto.riot.account.RiotAccountDto
import com.lowbudgetlcs.domain.models.riot.RiotApiException

interface IRiotAccountGateway {
    /**
     * @throws IllegalArgumentException if the PUUID is invalid (400)
     * @throws NoSuchElementException if no Riot account is found (404)
     * @throws RiotApiException for other Riot API failures
     */
    suspend fun getAccountByPuuid(puuid: String): RiotAccountDto
}
