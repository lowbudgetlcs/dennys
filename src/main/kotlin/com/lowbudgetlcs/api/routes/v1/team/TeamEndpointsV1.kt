package com.lowbudgetlcs.api.routes.v1.team

import com.lowbudgetlcs.api.dto.teams.NewTeamDto
import com.lowbudgetlcs.api.dto.teams.toDto
import com.lowbudgetlcs.api.dto.teams.toNewTeam
import com.lowbudgetlcs.api.logCall
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.domain.team.ITeamService
import com.lowbudgetlcs.domain.team.models.toTeamId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.teamEndpointsV1(teamService: ITeamService) {
    post<TeamResourcesV1> {
        call.setCidContext {
            logCall(call)
            val dto = call.receive<NewTeamDto>()
            logger.debug(dto.toString())
            val created = teamService.createTeam(dto.toNewTeam())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }
    get<TeamResourcesV1> {
        call.setCidContext {
            logCall(call)
            val teams = teamService.getAllTeams()
            call.respond(teams.map { it.toDto() })
        }
    }
    get<TeamResourcesV1.ById> { route ->
        call.setCidContext {
            logCall(call)
            val team = teamService.getTeam(route.teamId.toTeamId())
            call.respond(team.toDto())
        }
    }
}
