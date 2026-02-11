package com.lowbudgetlcs.domain.models.player

fun NewPlayer.toPlayer(id: PlayerId): Player =
    Player(
        id = id,
        name = name,
    )
