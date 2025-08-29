package com.lowbudgetlcs.domain.models.riot.account

import com.lowbudgetlcs.domain.models.PlayerId

fun NewRiotAccount.toRiotAccount(accountId: RiotAccountId, playerId: PlayerId): RiotAccount = RiotAccount(
    id = accountId,
    riotPuuid = riotPuuid,
    playerId = playerId
)