package com.lowbudgetlcs.api.routes.v1.team

import com.lowbudgetlcs.api.dto.teams.*
import com.lowbudgetlcs.api.logCall
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.config.StorageConfig
import com.lowbudgetlcs.domain.team.ITeamService
import com.lowbudgetlcs.domain.team.models.toTeamId
import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.patch
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.teamRoutesV1(
    teamService: ITeamService,
    storageConfig: StorageConfig,
) {
    route("/team") {
        post<TeamResources> {
            call.setCidContext {
                logCall(call)
                val dto = call.receive<NewTeamDto>()
                logger.debug(dto.toString())
                val created = teamService.createTeam(dto.toNewTeam())
                call.respond(HttpStatusCode.Created, created.toDto(storageConfig.logobucketurl))
            }
        }
        get<TeamResources> {
            call.setCidContext {
                logCall(call)
                val teams = teamService.getAllTeams()
                call.respond(teams.map { it.toDto(storageConfig.logobucketurl) })
            }
        }
        get<TeamResources.ById> { route ->
            call.setCidContext {
                logCall(call)
                val team = teamService.getTeam(route.teamId.toTeamId())
                call.respond(team.toDto(storageConfig.logobucketurl))
            }
        }
        patch<TeamResources.ById> { route ->
            call.setCidContext {
                logCall(call)
                val dto = call.receive<PatchTeamDto>()
                val updated = teamService.patchTeam(route.teamId.toTeamId(), dto.toTeamUpdate())
                call.respond(updated.toDto(storageConfig.logobucketurl))
            }
        }
    }
}
