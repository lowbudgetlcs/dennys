package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.domain.account.core.AccountService
import com.lowbudgetlcs.domain.account.core.port.IAccountService
import com.lowbudgetlcs.domain.auth.core.AuthService
import com.lowbudgetlcs.domain.auth.core.UserService
import com.lowbudgetlcs.domain.auth.core.port.IAuthService
import com.lowbudgetlcs.domain.auth.core.port.IUserService
import com.lowbudgetlcs.domain.player.core.PlayerService
import com.lowbudgetlcs.domain.player.core.port.IPlayerService
import com.lowbudgetlcs.domain.team.core.TeamService
import com.lowbudgetlcs.domain.team.core.port.ITeamService
import org.koin.core.qualifier.named
import org.koin.dsl.module

val serviceModule =
    module {
        single<ITeamService> { TeamService(get(), get()) }
        single<IPlayerService> { PlayerService(get(), get(), get()) }
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
