package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.config.RiotConfig
import com.lowbudgetlcs.gateways.riot.account.IRiotAccountGateway
import com.lowbudgetlcs.gateways.riot.account.RiotAccountGateway
import com.lowbudgetlcs.gateways.riot.tournament.IRiotTournamentGateway
import com.lowbudgetlcs.gateways.riot.tournament.RiotTournamentGateway
import org.koin.dsl.module

val gatewayModule =
    module {
        single<IRiotAccountGateway> {
            RiotAccountGateway(
                client = get(),
                apiKey = get<RiotConfig>().key,
            )
        }
        single<IRiotTournamentGateway> {
            RiotTournamentGateway(
                client = get(),
                apiKey = get<RiotConfig>().key,
                useStubs = get<RiotConfig>().usestubs,
                providerId = get<RiotConfig>().providerid,
            )
        }
    }
