package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.api.dto.riot.tournament.RiotShortcodeDto
import com.lowbudgetlcs.api.dto.riot.tournament.RiotTournamentParametersDto
import com.lowbudgetlcs.api.dto.riot.tournament.toShortcodeParametersDto
import com.lowbudgetlcs.domain.models.riot.RiotApiException
import com.lowbudgetlcs.domain.models.riot.tournament.NewShortcode
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournament
import com.lowbudgetlcs.domain.models.riot.tournament.RiotTournamentId
import com.lowbudgetlcs.domain.models.riot.tournament.toRiotTournamentId
import com.lowbudgetlcs.repositories.DatabaseException
import com.lowbudgetlcs.repositories.metadata.IMetadataRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory


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
    private val logger: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    override suspend fun create(tournamentName: String): RiotTournament {
        logger.debug("Creating tournament named '$tournamentName'...")
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
        logger.debug("Fetching tournament code for tournament '$riotTournamentId'...")
        val body = newShortcode.toShortcodeParametersDto()
        logger.debug(body.toString())
        val res: HttpResponse = client.post("$url/codes") {
            url {
                parameters.append("tournamentId", "${riotTournamentId.value}")
                parameters.append("count", "1")
            }
            headers {
                append("X-Riot-Token", apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        when (res.status) {
            HttpStatusCode.OK -> {
                logger.debug("Successfully created codes.")
                val codes = res.body<List<String>>()
                return RiotShortcodeDto(codes)
            }

            else -> {
                logger.warn("Failed to create codes.")
                throw RiotApiException("Unexpected Riot API error: ${res.status}")
            }
        }
    }
}