package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.domain.models.riot.RiotApiException
import com.lowbudgetlcs.domain.models.riot.tournament.NewShortcode
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournament
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournamentId
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.IMetadataRepository
import com.lowbudgetlcs.routes.dto.riot.tournament.RiotShortcodeDto
import com.lowbudgetlcs.routes.dto.riot.tournament.RiotTournamentParametersDto
import com.lowbudgetlcs.routes.dto.riot.tournament.toShortcodeParametersDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*


class RiotTournamentGateway(
    private val metadataRepo: IMetadataRepository,
    private val client: HttpClient,
    private val apiKey: String,
    private val useStubs: Boolean,
    private val baseUrl: String = "https://americas.api.riotgames.com"
) : IRiotTournamentGateway {
    private val url: String by lazy {
        if (useStubs) "$baseUrl/lol/tournament-stub/v5" else "$baseUrl/lol/tournament/v5"
    }

    override suspend fun create(tournamentName: String): RiotTournament {
        val providerId = metadataRepo.getProviderId() ?: throw DatabaseException("Cannot find riot provider id.")
        val res = client.post("$url/tournaments") {
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
        riotTournamentId: RiotTournamentId, newShortcode: NewShortcode
    ): RiotShortcodeDto {
        val res: HttpResponse = client.post("$url/codes") {
            url {
                parameters.append("tournamentId", "${riotTournamentId.value}")
                parameters.append("count", "1")
            }
            headers {
                append("X-Riot-Token", apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(newShortcode.toShortcodeParametersDto())
        }
        when (res.status) {
            HttpStatusCode.OK -> {
                val codes = res.body<List<String>>()
                return RiotShortcodeDto(codes)
            }

            else -> {
                throw RiotApiException("Unexpected Riot API error: ${res.status}")
            }
        }
    }
}