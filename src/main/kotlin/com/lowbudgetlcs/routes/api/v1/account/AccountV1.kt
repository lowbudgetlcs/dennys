package com.lowbudgetlcs.routes.api.v1.account

import com.lowbudgetlcs.domain.services.AccountService
import com.lowbudgetlcs.routes.dto.accounts.NewAccountDto
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.routing.*


fun Route.accountRoutesV1(
    accountService: AccountService
) {
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