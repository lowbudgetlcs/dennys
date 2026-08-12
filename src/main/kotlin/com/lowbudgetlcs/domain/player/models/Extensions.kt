package com.lowbudgetlcs.domain.player.models

import com.lowbudgetlcs.domain.account.models.Account
import com.lowbudgetlcs.domain.player.models.types.PlayerId
import com.lowbudgetlcs.domain.player.models.types.PlayerName
import com.lowbudgetlcs.domain.team.models.Team

// Type Extensions
fun Int.toPlayerId(): PlayerId = PlayerId(this)

fun String.toPlayerName(): PlayerName = PlayerName(this)

// Class Extensions
fun NewPlayer.toPlayer(id: PlayerId): Player =
    Player(
        id = id,
        name = name,
    )

fun Player.toPlayerWithTeams(accounts: List<Account>, teams: List<Team>): PlayerWithTeams = PlayerWithTeams(
    id = id,
    name = name,
    accounts = accounts,
    teams = teams
)

fun Player.toPlayerWithAccounts(accounts: List<Account>): PlayerWithAccounts = PlayerWithAccounts(
    id = id,
    name = name,
    accounts = accounts
)
