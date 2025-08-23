package com.lowbudgetlcs.routes.api.v1.team

import com.lowbudgetlcs.domain.services.TeamService
import com.lowbudgetlcs.routes.dto.teams.NewTeamDto
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

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