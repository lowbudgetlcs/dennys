package com.lowbudgetlcs.api.routes.v1.account

import com.lowbudgetlcs.api.dto.accounts.NewAccountDto
import com.lowbudgetlcs.api.dto.accounts.toDto
import com.lowbudgetlcs.api.dto.accounts.toNewAccount
import com.lowbudgetlcs.domain.account.IAccountService
import com.lowbudgetlcs.domain.account.models.toAccountId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.accountRoutesV1(accountService: IAccountService) {
    route("/account") {
        post<AccountResources> {
            val dto = call.receive<NewAccountDto>()
            logger.debug(dto.toString())
            val created = accountService.createAccount(dto.toNewAccount())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
        get<AccountResources> {
            val accounts = accountService.getAllAccounts()
            call.respond(HttpStatusCode.OK, accounts.map { it.toDto() })
        }
        get<AccountResources.ById> { route ->
            val account = accountService.getAccount(route.accountId.toAccountId()) // throws if not found
            call.respond(account.toDto())
        }
    }
}
