package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.domain.FeatureRoute
import com.lowbudgetlcs.domain.event.adapter.`in`.web.EventRoutesV1
import org.koin.dsl.module


val routeModule = module {
    single<FeatureRoute> { EventRoutesV1(get(), get()) }
}
