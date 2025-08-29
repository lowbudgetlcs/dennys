package com.lowbudgetlcs.api.routes.v1.player

import com.lowbudgetlcs.domain.services.player.PlayerService
import com.lowbudgetlcs.api.dto.players.AccountLinkRequestDto
import com.lowbudgetlcs.api.dto.players.NewPlayerDto
import com.lowbudgetlcs.api.dto.players.PatchPlayerDto
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.routing.*


fun Route.playerRoutesV1(
    playerService: PlayerService
) {
    route("/player") {
        install(RequestValidation) {
            validate<NewPlayerDto> { dto ->
                when {
                    dto.name.isBlank() -> ValidationResult.Invalid("Player name cannot be blank")
                    else -> ValidationResult.Valid
                }
            }

            validate<PatchPlayerDto> { dto ->
                when {
                    dto.name.isBlank() -> ValidationResult.Invalid("New name cannot be blank")
                    else -> ValidationResult.Valid
                }
            }

            validate<AccountLinkRequestDto> { dto ->
                when {
                    dto.accountId <= 0 -> ValidationResult.Invalid("Account ID must be greater than 0")
                    else -> ValidationResult.Valid
                }
            }
        }
        playerEndpointsV1(playerService)
    }
}