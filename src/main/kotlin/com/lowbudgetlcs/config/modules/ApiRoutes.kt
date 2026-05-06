package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.account.adapter.`in`.web.AccountRoutesV1
import com.lowbudgetlcs.domain.division.adapter.`in`.web.EventGroupRoutesV1
import com.lowbudgetlcs.domain.division.adapter.`in`.web.SeriesRoutesV1
import com.lowbudgetlcs.domain.player.adapter.`in`.web.PlayerRoutesV1
import com.lowbudgetlcs.domain.team.adapter.`in`.web.TeamRoutesV1
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val apiRouteModule = module {
    singleOf(::TeamRoutesV1) { bind<ApiRoute>() }
    singleOf(::PlayerRoutesV1) { bind<ApiRoute>() }
    singleOf(::AccountRoutesV1) { bind<ApiRoute>() }
    singleOf(::SeriesRoutesV1) { bind<ApiRoute>() }
    singleOf(::EventGroupRoutesV1) { bind<ApiRoute>() }
}
