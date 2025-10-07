package com.lowbudgetlcs.api.routes.v1.account

import com.lowbudgetlcs.api.dto.accounts.NewAccountDto
import com.lowbudgetlcs.domain.services.account.AccountService
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

fun Route.accountRoutesV1(accountService: AccountService) {
    route("/account") {
        install(RequestValidation) {
            validate<NewAccountDto> { dto ->
                when {
                    dto.riotPuuid.isBlank() -> ValidationResult.Invalid("PUUID cannot be blank")
                    else -> ValidationResult.Valid
                }
            }
        }
        accountEndpointsV1(accountService)
    }
}
