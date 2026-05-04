package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.account.adapter.`in`.web.AccountRoutesV1
import com.lowbudgetlcs.domain.event.adapter.`in`.web.EventRoutesV1
import com.lowbudgetlcs.domain.player.adapter.`in`.web.PlayerRoutesV1
import com.lowbudgetlcs.domain.series.adapter.`in`.web.SeriesRoutesV1
import com.lowbudgetlcs.domain.team.adapter.`in`.web.TeamRoutesV1
import org.koin.dsl.module


val apiRouteModule = module {
    single<ApiRoute> { EventRoutesV1(get(), get()) }
    single<ApiRoute> { TeamRoutesV1(get()) }
    single<ApiRoute> { PlayerRoutesV1(get()) }
    single<ApiRoute> { AccountRoutesV1(get()) }
    single<ApiRoute> { SeriesRoutesV1(get()) }
}
