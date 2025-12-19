package com.lowbudgetlcs.api.routes.v1.team

import com.lowbudgetlcs.api.dto.teams.NewTeamDto
import com.lowbudgetlcs.domain.services.team.ITeamService
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.routing.*

fun Route.teamRoutesV1(teamService: ITeamService) {
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
