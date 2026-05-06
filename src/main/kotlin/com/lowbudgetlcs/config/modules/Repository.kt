package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.domain.account.adapter.out.persistence.SqlAccountRepository
import com.lowbudgetlcs.domain.account.core.port.IAccountRepository
import com.lowbudgetlcs.domain.auth.adapter.out.persistence.AccessTokenRepository
import com.lowbudgetlcs.domain.auth.adapter.out.persistence.SessionRepository
import com.lowbudgetlcs.domain.auth.adapter.out.persistence.UserRepostitory
import com.lowbudgetlcs.domain.auth.core.port.IAccessTokenRepository
import com.lowbudgetlcs.domain.auth.core.port.ISessionRepository
import com.lowbudgetlcs.domain.auth.core.port.IUserRepository
import com.lowbudgetlcs.domain.division.adapter.out.persistence.SqlGameRepository
import com.lowbudgetlcs.domain.division.core.series.port.IGameRepository
import com.lowbudgetlcs.domain.player.adapter.out.persistence.PlayerRepository
import com.lowbudgetlcs.domain.player.core.port.IPlayerRepository
import com.lowbudgetlcs.domain.team.adapter.out.persistence.TeamRepository
import com.lowbudgetlcs.domain.team.core.port.ITeamRepository
import org.koin.dsl.module

val repositoryModule =
    module {
        single<IGameRepository> { SqlGameRepository(get()) }
        single<ITeamRepository> { TeamRepository(get()) }
        single<IPlayerRepository> { PlayerRepository(get()) }
        single<IAccountRepository> { SqlAccountRepository(get()) }
        single<ISessionRepository> { SessionRepository(get()) }
        single<IUserRepository> { UserRepostitory(get()) }
        single<IAccessTokenRepository> { AccessTokenRepository(get()) }
    }
