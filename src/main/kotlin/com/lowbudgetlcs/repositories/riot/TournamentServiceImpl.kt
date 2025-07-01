package com.lowbudgetlcs.repositories.riot

import com.lowbudgetlcs.http.RiotApiClient
import com.lowbudgetlcs.http.RiotApiClient.apiKey
import com.lowbudgetlcs.models.tournament.CodesRequest
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TournamentServiceImpl : TournamentService {

    private val logger: Logger = LoggerFactory.getLogger(TournamentServiceImpl::class.java)

    override suspend fun generateTournamentCode(
        tournamentId: Int,
        count: Int,
        codesRequest: CodesRequest
    ): List<String> {
        return try {
            val response = RiotApiClient.riotHttpClient.post {
                contentType(ContentType.Application.Json)
                parameter("tournamentId", tournamentId)
                parameter("count", count)
                setBody(codesRequest)
                url(RiotRoutes.CODES)
                headers {
                    append("X-Riot-Token", apiKey)
                }
            }
            response.body()
        } catch (e: RedirectResponseException) {
            // 3xx responses
            logger.error("3xx Error: ${e.response.status.description}")
            emptyList()
        } catch (e: ClientRequestException) {
            // 4xx responses
            logger.error("4xx Error: ${e.response.status.description}")
            emptyList()
        } catch (e: ServerResponseException) {
            //5xx responses
            logger.error("5xx Error: ${e.response.status.description}")
            emptyList()
        } catch (e: Error) {
            logger.error("Error: ${e.message}")
            emptyList()
        }
    }

}