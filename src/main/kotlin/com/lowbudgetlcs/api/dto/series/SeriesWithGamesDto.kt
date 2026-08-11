package com.lowbudgetlcs.api.dto.series

import com.lowbudgetlcs.api.dto.games.GameDto
import com.lowbudgetlcs.api.dto.games.TournamentCodeDto
import com.lowbudgetlcs.domain.event.models.types.EventStage
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class SeriesWithGamesDto(
    val id: Int,
    val eventId: Int?,
    val teamIds: List<Int>,
    val totalGames: Int,
    val eventStage: EventStage,
    val completed: Boolean,
    @Contextual
    val completedAt: Instant? = null,
    @Contextual
    val reopenedAt: Instant? = null,
    val tournamentCodes: List<TournamentCodeDto>,
    val games: List<GameDto>,
    @Contextual
    val lastCodeIssuedAt: Instant? = null,
    @Contextual
    val lastGameAt: Instant? = null,
)
