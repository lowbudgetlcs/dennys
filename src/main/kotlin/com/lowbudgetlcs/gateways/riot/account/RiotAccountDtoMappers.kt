package com.lowbudgetlcs.gateways.riot.account

import com.lowbudgetlcs.domain.models.player.account.RiotAccount
import com.lowbudgetlcs.domain.models.player.account.toPuuid

fun RiotAccountDto.toRiotAccount(): RiotAccount = RiotAccount(this.puuid.toPuuid())
