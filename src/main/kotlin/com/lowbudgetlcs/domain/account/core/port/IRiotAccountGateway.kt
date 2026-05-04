package com.lowbudgetlcs.domain.account.core.port

import com.lowbudgetlcs.domain.account.core.model.RiotAccount
import com.lowbudgetlcs.domain.account.core.model.types.Puuid

interface IRiotAccountGateway {
    /**
     * @throws IllegalArgumentException if the PUUID is invalid (400)
     * @throws NoSuchElementException if no Riot account is found (404)
     * @throws com.lowbudgetlcs.gateways.riot.RiotApiException for other Riot API failures
     */
    suspend fun getAccountByPuuid(puuid: Puuid): RiotAccount
}
