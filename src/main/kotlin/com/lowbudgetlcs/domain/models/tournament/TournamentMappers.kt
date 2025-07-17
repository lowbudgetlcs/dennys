package com.lowbudgetlcs.domain.models.tournament

fun NewTournament.toTournament(id: TournamentId): Tournament = Tournament(id = id, name = name)