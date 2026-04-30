package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.domain.account.adapter.out.persistence.SqlAccountRepository
import com.lowbudgetlcs.domain.account.core.port.IAccountRepository
import com.lowbudgetlcs.domain.event.adapter.out.persistence.SqlEventRepository
import com.lowbudgetlcs.domain.event.core.port.IEventRepository
import com.lowbudgetlcs.domain.event.adapter.out.persistence.SqlEventGroupRepository
import com.lowbudgetlcs.domain.event.core.port.IEventGroupRepository
import com.lowbudgetlcs.repositories.game.GameRepository
import com.lowbudgetlcs.repositories.game.IGameRepository
import com.lowbudgetlcs.domain.player.core.port.IPlayerRepository
import com.lowbudgetlcs.domain.player.adapter.out.persistence.PlayerRepository
import com.lowbudgetlcs.repositories.series.ISeriesRepository
import com.lowbudgetlcs.repositories.series.SeriesRepository
import com.lowbudgetlcs.repositories.session.ISessionRepository
import com.lowbudgetlcs.repositories.session.SessionRepository
import com.lowbudgetlcs.repositories.team.ITeamRepository
import com.lowbudgetlcs.repositories.team.TeamRepository
import com.lowbudgetlcs.repositories.tokens.AccessTokenRepository
import com.lowbudgetlcs.repositories.tokens.IAccessTokenRepository
import com.lowbudgetlcs.repositories.user.IUserRepository
import com.lowbudgetlcs.repositories.user.UserRepostitory
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
