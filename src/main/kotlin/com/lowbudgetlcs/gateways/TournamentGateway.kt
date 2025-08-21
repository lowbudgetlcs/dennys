package com.lowbudgetlcs.gateways

import com.lowbudgetlcs.domain.models.tournament.NewTournament
import com.lowbudgetlcs.domain.models.tournament.Tournament
import com.lowbudgetlcs.domain.models.tournament.toTournamentId
import com.lowbudgetlcs.repositories.IMetadataRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class CreateTournamentRequest(val name: String, val providerId: Int)

class TournamentGateway(
    private val metadataRepo: IMetadataRepository,
    private val client: HttpClient,
    private val apiKey: String,
    private val stub: Boolean,
    private val baseUrl: String = "https://americas.api.riotgames.com"
) : ITournamentGateway {
    override suspend fun create(tournament: NewTournament): Tournament {
        val name = tournament.name
        val providerId = metadataRepo.getProviderId() ?: throw NoSuchElementException("Cannot find riot provider id.")
        // TODO: This is ugly.
        val url = if (!stub) "$baseUrl/lol/tournament/v5/tournament" else "$baseUrl/lol/tournament-stub/v5/tournament"
        val res = client.post(url) {
            headers {
                append("X-Riot-Token", apiKey)
            }
            contentType(ContentType.Application.Json)
            setBody(CreateTournamentRequest(name, providerId))
        }
        if (res.status.value in 200..299) {
            return Tournament(
                id = res.body<Int>().toTournamentId(), name = name
            )
        } else {
            throw Exception("Faield to create Riot tournament.")
        }
    }
}