package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.domain.FeatureRoute
import com.lowbudgetlcs.domain.account.adapter.`in`.web.AccountRoutesV1
import com.lowbudgetlcs.domain.event.adapter.`in`.web.EventRoutesV1
import com.lowbudgetlcs.domain.player.adapter.`in`.web.PlayerRoutesV1
import com.lowbudgetlcs.domain.series.adapter.`in`.web.SeriesRoutesV1
import com.lowbudgetlcs.domain.team.adapter.`in`.web.TeamRoutesV1
import org.koin.dsl.module


val apiRouteModule = module {
    single<FeatureRoute> { EventRoutesV1(get(), get()) }
    single<FeatureRoute> { TeamRoutesV1(get()) }
    single<FeatureRoute> { PlayerRoutesV1(get()) }
    single<FeatureRoute> { AccountRoutesV1(get()) }
    single<FeatureRoute> { SeriesRoutesV1(get()) }
}
