package com.lowbudgetlcs.modules

import com.lowbudgetlcs.repositories.account.AccountRepository
import com.lowbudgetlcs.repositories.account.IAccountRepository
import com.lowbudgetlcs.repositories.event.EventRepository
import com.lowbudgetlcs.repositories.event.IEventRepository
import com.lowbudgetlcs.repositories.event.group.EventGroupRepository
import com.lowbudgetlcs.repositories.event.group.IEventGroupRepository
import com.lowbudgetlcs.repositories.game.GameRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.repositories.player.IPlayerRepository
import com.lowbudgetlcs.repositories.player.PlayerRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.session.ISessionRepository
import com.lowbudgetlcs.repositories.session.SessionRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import com.lowbudgetlcs.repositories.user.IUserRepository
import com.lowbudgetlcs.repositories.user.UserRepostitory
import org.koin.dsl.module

val repositoryModule =
    module {
        single<IGameRepository> { GameRepository(get()) }
        single<ITeamRepository> { TeamRepository(get()) }
        single<IEventRepository> { EventRepository(get()) }
        single<IEventGroupRepository> { EventGroupRepository(get()) }
        single<IPlayerRepository> { PlayerRepository(get()) }
        single<ISeriesRepository> { SeriesRepository(get()) }
        single<IAccountRepository> { AccountRepository(get()) }
        single<ISessionRepository> { SessionRepository(get()) }
        single<IUserRepository> { UserRepostitory(get()) }
    }
