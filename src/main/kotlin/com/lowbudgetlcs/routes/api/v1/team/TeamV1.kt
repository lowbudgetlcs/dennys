package com.lowbudgetlcs.routes.api.v1.team

import com.lowbudgetlcs.domain.models.toTeamId
import com.lowbudgetlcs.domain.services.TeamService
import com.lowbudgetlcs.routes.dto.teams.NewTeamDto
import com.lowbudgetlcs.routes.dto.teams.toDto
import com.lowbudgetlcs.routes.dto.teams.toNewTeam
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.teamRoutesV1(
    teamService: TeamService
) {
    post<TeamRoutes> {
        logger.info("📩 Received POST /v1/team")
        val dto = call.receive<NewTeamDto>()
        val created = teamService.createTeam(dto.toNewTeam())
        call.respond(HttpStatusCode.Created, created.toDto())
    }

    get<TeamRoutes> {
        logger.info("📩 Received GET /v1/team")
        val teams = teamService.getAllTeams()
        call.respond(teams.map { it.toDto() })
    }

    get<TeamRoutes.ById> { route ->
        logger.info("📩 Received GET /v1/team/${route.teamId}")
        val team = teamService.getTeam(route.teamId.toTeamId())
        call.respond(team.toDto())
    }
}