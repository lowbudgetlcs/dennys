package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.domain.models.riot.RiotApiException
import com.lowbudgetlcs.domain.models.riot.tournament.NewRiotTournament
import com.lowbudgetlcs.domain.models.riot.tournament.NewShortcode
import com.lowbudgetlcs.domain.models.riot.tournament.Shortcode
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournament
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import com.lowbudgetlcs.domain.models.riot.tournament.toShortcode
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.IMetadataRepository
import com.lowbudgetlcs.routes.dto.riot.tournament.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*


class RiotTournamentGateway(
    private val metadataRepo: IMetadataRepository,
    private val client: HttpClient,
    private val apiKey: String,
    private val useStubs: Boolean,
    private val baseUrl: String = "https://americas.api.riotgames.com"
) : IRiotTournamentGateway {
    private val url: String by lazy {
        if (useStubs) "$baseUrl/lol/tournament-stub/v5/tournaments" else "$baseUrl/lol/tournament/v5/tournaments"
    }

    override suspend fun create(tournamentName: String): RiotTournament {
        val providerId = metadataRepo.getProviderId() ?: throw DatabaseException("Cannot find riot provider id.")
        val res = client.post(url) {
            headers {
                append("X-Riot-Token", apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(RiotTournamentParametersDto(tournamentName, providerId))
        }
        when (res.status) {
            HttpStatusCode.OK -> return RiotTournament(
                id = res.body<Int>().toRiotTournamentId(), name = tournamentName
            )

            else -> {
                throw RiotApiException("Unexpected Riot API error: ${res.status}")
            }
        }
    }

    override suspend fun getCode(
        riotTournament: RiotTournament, newShortcode: NewShortcode
    ): Shortcode {
        val res = client.post(url) {
            url {
                parameters.append("count", "1")
                parameters.append("tournamentId", "${riotTournament.id.value}")
            }
            headers {
                append("X-Riot-Token", apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(newShortcode.toShortcodeParametersDto())
        }
        when (res.status) {
            HttpStatusCode.OK -> return res.body<List<String>>()[0].toShortcode()
            else ->  throw RiotApiException("Unexpected Riot API error: ${res.status}")
        }
    }
}