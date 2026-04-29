package com.lowbudgetlcs.gateways.riot.tournament

import com.lowbudgetlcs.domain.event.models.RiotTournament
import com.lowbudgetlcs.domain.event.models.ShortcodeOptions
import com.lowbudgetlcs.domain.event.models.toRiotTournamentId
import com.lowbudgetlcs.domain.event.models.EventName
import com.lowbudgetlcs.domain.event.models.RiotTournamentId
import com.lowbudgetlcs.gateways.riot.RiotApiException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RiotTournamentGateway(
    private val client: HttpClient,
    private val apiKey: String,
    private val useStubs: Boolean,
    private val providerId: Int,
    private val baseUrl: String = "https://americas.api.riotgames.com",
) : IRiotTournamentGateway {
    private val url: String by lazy {
        if (useStubs) "$baseUrl/lol/tournament-stub/v5" else "$baseUrl/lol/tournament/v5"
    }
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun create(tournamentName: EventName): RiotTournament {
        logger.debug("Creating tournament named '$tournamentName'...")
        val res =
            client.post("$url/tournaments") {
                headers {
                    append("X-Riot-Token", apiKey)
                }
                contentType(ContentType.Application.Json)
                setBody(RiotTournamentParametersDto(tournamentName.value, providerId))
            }
        when (res.status) {
            HttpStatusCode.OK -> return RiotTournament(
                id = res.body<Int>().toRiotTournamentId(),
                name = tournamentName,
            )

            else -> {
                throw RiotApiException("Unexpected Riot API error: ${res.status}")
            }
        }
    }

    override suspend fun getCode(
        riotTournamentId: RiotTournamentId,
        options: ShortcodeOptions,
    ): RiotShortcodeDto {
        logger.debug("Fetching tournament code for tournament '$riotTournamentId'...")
        val body = options.toShortcodeParametersDto()
        logger.debug(body.toString())
        val res: HttpResponse =
            client.post("$url/codes") {
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
