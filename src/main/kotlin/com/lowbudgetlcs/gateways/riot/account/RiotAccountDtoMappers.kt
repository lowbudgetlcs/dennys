package com.lowbudgetlcs.gateways.riot.account

import com.lowbudgetlcs.domain.account.core.model.RiotAccount
import com.lowbudgetlcs.domain.account.core.model.types.toPuuid

fun RiotAccountDto.toRiotAccount(): RiotAccount = RiotAccount(this.puuid.toPuuid())
