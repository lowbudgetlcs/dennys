package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.domain.account.core.AccountService
import com.lowbudgetlcs.domain.account.core.port.IAccountService
import com.lowbudgetlcs.domain.auth.AuthService
import com.lowbudgetlcs.domain.auth.IAuthService
import com.lowbudgetlcs.domain.event.core.EventService
import com.lowbudgetlcs.domain.event.core.port.IEventService
import com.lowbudgetlcs.domain.eventgroup.EventGroupService
import com.lowbudgetlcs.domain.eventgroup.IEventGroupService
import com.lowbudgetlcs.domain.player.IPlayerService
import com.lowbudgetlcs.domain.player.PlayerService
import com.lowbudgetlcs.domain.series.ISeriesService
import com.lowbudgetlcs.domain.series.SeriesService
import com.lowbudgetlcs.domain.team.ITeamService
import com.lowbudgetlcs.domain.team.TeamService
import com.lowbudgetlcs.domain.user.IUserService
import com.lowbudgetlcs.domain.user.UserService
import org.koin.core.qualifier.named
import org.koin.dsl.module

val serviceModule =
    module {
        single<ITeamService> { TeamService(get(), get()) }
        single<IEventService> { EventService(get(), get(), get(), get()) }
        single<IEventGroupService> { EventGroupService(get(), get()) }
        single<IPlayerService> { PlayerService(get(), get(), get()) }
        single<ISeriesService> { SeriesService(get(), get(), get(), get(), get()) }
        single<IAccountService> { AccountService(get(), get(), get()) }
        single<IAuthService> {
            AuthService(
                get(),
                get(),
                get(),
                passwordHasher = get(named("argon2")),
                tokenHasher = get(named("sha256")),
                get(),
            )
        }
        single<IUserService> { UserService(get()) }
    }
