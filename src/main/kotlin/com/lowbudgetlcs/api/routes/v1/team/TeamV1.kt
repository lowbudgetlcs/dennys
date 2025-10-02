package com.lowbudgetlcs.api.routes.v1.team

import com.lowbudgetlcs.api.dto.teams.NewTeamDto
import com.lowbudgetlcs.domain.services.team.TeamService
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.teamRoutesV1(teamService: TeamService) {
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
