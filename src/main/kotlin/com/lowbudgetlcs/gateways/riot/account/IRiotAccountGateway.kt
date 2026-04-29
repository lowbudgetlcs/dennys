package com.lowbudgetlcs.gateways.riot.account

import com.lowbudgetlcs.domain.account.models.RiotAccount
import com.lowbudgetlcs.domain.account.models.Puuid
import com.lowbudgetlcs.gateways.riot.RiotApiException

interface IRiotAccountGateway {
    /**
     * @throws IllegalArgumentException if the PUUID is invalid (400)
     * @throws NoSuchElementException if no Riot account is found (404)
     * @throws RiotApiException for other Riot API failures
     */
    suspend fun getAccountByPuuid(puuid: Puuid): RiotAccount
}
