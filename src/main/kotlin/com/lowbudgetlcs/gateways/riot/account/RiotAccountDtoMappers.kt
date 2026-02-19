package com.lowbudgetlcs.gateways.riot.account

import com.lowbudgetlcs.domain.account.models.RiotAccount
import com.lowbudgetlcs.domain.account.models.toPuuid

fun RiotAccountDto.toRiotAccount(): RiotAccount = RiotAccount(this.puuid.toPuuid())
