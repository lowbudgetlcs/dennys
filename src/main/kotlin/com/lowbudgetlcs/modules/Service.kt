package com.lowbudgetlcs.modules

import com.lowbudgetlcs.domain.services.account.AccountService
import com.lowbudgetlcs.domain.services.account.IAccountService
import com.lowbudgetlcs.domain.services.event.EventService
import com.lowbudgetlcs.domain.services.event.IEventService
import com.lowbudgetlcs.domain.services.event.group.EventGroupService
import com.lowbudgetlcs.domain.services.event.group.IEventGroupService
import com.lowbudgetlcs.domain.services.game.GameService
import com.lowbudgetlcs.domain.services.game.IGameService
import com.lowbudgetlcs.domain.services.player.IPlayerService
import com.lowbudgetlcs.domain.services.player.PlayerService
import com.lowbudgetlcs.domain.services.series.ISeriesService
import com.lowbudgetlcs.domain.services.series.SeriesService
import com.lowbudgetlcs.domain.services.team.ITeamService
import com.lowbudgetlcs.domain.services.team.TeamService
import org.koin.dsl.module

val serviceModule =
    module {
        single<IGameService> { GameService(get(), get(), get(), get(), get()) }
        single<ITeamService> { TeamService(get()) }
        single<IEventService> { EventService(get(), get(), get(), get()) }
        single<IEventGroupService> { EventGroupService(get(), get()) }
        single<IPlayerService> { PlayerService(get(), get()) }
        single<ISeriesService> { SeriesService(get(), get()) }
        single<IAccountService> { AccountService(get(), get()) }
    }
