package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.domain.ApiRoute
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.AddTeamToEvent
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.CreateEvent
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.EventCreateSeries
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.EventDeleteSeries
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.GetAllEvents
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.GetEvent
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.GetEventWithSeries
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.GetEventWithTeams
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.PatchEvent
import com.lowbudgetlcs.domain.division.adapter.`in`.web.event.RemoveTeamFromEvent
import com.lowbudgetlcs.domain.division.adapter.out.persistence.SqlEventGroupRepository
import com.lowbudgetlcs.domain.division.adapter.out.persistence.SqlEventRepository
import com.lowbudgetlcs.domain.division.adapter.out.persistence.SqlSeriesRepository
import com.lowbudgetlcs.domain.division.core.application.queries.GetEventWithSeriesQuery
import com.lowbudgetlcs.domain.division.core.event.port.IEventGroupRepository
import com.lowbudgetlcs.domain.division.core.event.port.IEventRepository
import com.lowbudgetlcs.domain.division.core.event.services.EventGroupService
import com.lowbudgetlcs.domain.division.core.event.services.EventService
import com.lowbudgetlcs.domain.division.core.series.port.ISeriesRepository
import com.lowbudgetlcs.domain.division.core.series.services.SeriesService
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val divisionModule = module {
    // Api
    singleOf(::AddTeamToEvent) { bind<ApiRoute>() }
    singleOf(::CreateEvent) { bind<ApiRoute>() }
    singleOf(::EventCreateSeries) { bind<ApiRoute>() }
    singleOf(::EventDeleteSeries) { bind<ApiRoute>() }
    singleOf(::GetAllEvents) { bind<ApiRoute>() }
    singleOf(::GetEvent) { bind<ApiRoute>() }
    singleOf(::GetEventWithSeries) { bind<ApiRoute>() }
    singleOf(::GetEventWithTeams) { bind<ApiRoute>() }
    singleOf(::PatchEvent) { bind<ApiRoute>() }
    singleOf(::RemoveTeamFromEvent) { bind<ApiRoute>() }

    // Repositories
    single<IEventRepository> { SqlEventRepository(get()) }
    single<ISeriesRepository> { SqlSeriesRepository(get()) }
    single<IEventGroupRepository> { SqlEventGroupRepository(get()) }

    // Services
    singleOf(::EventService)
    singleOf(::EventGroupService)
    singleOf(::SeriesService)

    // Use Cases
    singleOf(::GetEventWithSeriesQuery)
}
