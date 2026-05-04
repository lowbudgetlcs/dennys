package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.domain.FeatureRoute
import com.lowbudgetlcs.domain.event.adapter.`in`.web.EventRoutesV1
import com.lowbudgetlcs.domain.team.adapter.`in`.web.TeamRoutesV1
import org.koin.dsl.module


val routeModule = module {
    single<FeatureRoute> { EventRoutesV1(get(), get()) }
    single<FeatureRoute> { TeamRoutesV1(get()) }
}
