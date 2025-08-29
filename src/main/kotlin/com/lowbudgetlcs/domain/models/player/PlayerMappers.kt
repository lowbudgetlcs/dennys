package com.lowbudgetlcs.domain.models.player

import com.lowbudgetlcs.domain.models.riot.account.RiotAccount

fun NewPlayer.toPlayer(id: PlayerId): Player = Player(
    id = id, name = name
)

fun Player.toPlayerWithAccounts(accounts: List<RiotAccount>): PlayerWithAccounts = PlayerWithAccounts(
    id = id, name = name, accounts = accounts
)