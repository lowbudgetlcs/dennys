package com.lowbudgetlcs.api.routes.v1.team

import com.lowbudgetlcs.domain.services.TeamService
import com.lowbudgetlcs.api.dto.teams.NewTeamDto
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.routing.*

fun Route.teamRoutesV1(
    teamService: TeamService
) {
    route("/team") {
        install(RequestValidation) {
            validate<NewTeamDto> { dto ->
                when {
                    dto.name.isBlank() -> ValidationResult.Invalid("Team name cannot be blank.")
                    else -> ValidationResult.Valid
                }
            }
        }
        teamEndpointsV1(teamService)
    }
}