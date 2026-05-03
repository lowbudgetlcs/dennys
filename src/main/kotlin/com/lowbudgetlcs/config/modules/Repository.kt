package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.domain.account.adapter.out.persistence.SqlAccountRepository
import com.lowbudgetlcs.domain.account.core.port.IAccountRepository
import com.lowbudgetlcs.domain.event.adapter.out.persistence.SqlEventGroupRepository
import com.lowbudgetlcs.domain.event.adapter.out.persistence.SqlEventRepository
import com.lowbudgetlcs.domain.event.core.port.IEventGroupRepository
import com.lowbudgetlcs.domain.event.core.port.IEventRepository
import com.lowbudgetlcs.domain.player.adapter.out.persistence.PlayerRepository
import com.lowbudgetlcs.domain.player.core.port.IPlayerRepository
import com.lowbudgetlcs.domain.series.adapter.out.persistence.GameRepository
import com.lowbudgetlcs.domain.series.adapter.out.persistence.SeriesRepository
import com.lowbudgetlcs.domain.series.core.port.IGameRepository
import com.lowbudgetlcs.domain.series.core.port.ISeriesRepository
import com.lowbudgetlcs.domain.team.adapter.out.persistence.TeamRepository
import com.lowbudgetlcs.domain.team.core.port.ITeamRepository
import com.lowbudgetlcs.domain.auth.core.port.ISessionRepository
import com.lowbudgetlcs.domain.auth.adapter.out.persistence.SessionRepository
import com.lowbudgetlcs.domain.auth.adapter.out.persistence.AccessTokenRepository
import com.lowbudgetlcs.domain.auth.core.port.IAccessTokenRepository
import com.lowbudgetlcs.domain.auth.core.port.IUserRepository
import com.lowbudgetlcs.domain.auth.adapter.out.persistence.UserRepostitory
import org.koin.dsl.module

val repositoryModule =
    module {
        single<IGameRepository> { GameRepository(get()) }
        single<ITeamRepository> { TeamRepository(get()) }
        single<IEventRepository> { SqlEventRepository(get()) }
        single<IEventGroupRepository> { SqlEventGroupRepository(get()) }
        single<IPlayerRepository> { PlayerRepository(get()) }
        single<ISeriesRepository> { SeriesRepository(get()) }
        single<IAccountRepository> { SqlAccountRepository(get()) }
        single<ISessionRepository> { SessionRepository(get()) }
        single<IUserRepository> { UserRepostitory(get()) }
        single<IAccessTokenRepository> { AccessTokenRepository(get()) }
    }
