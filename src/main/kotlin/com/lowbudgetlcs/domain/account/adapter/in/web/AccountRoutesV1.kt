package com.lowbudgetlcs.domain.account.adapter.`in`.web

import com.lowbudgetlcs.domain.account.core.port.IAccountService
import com.lowbudgetlcs.domain.account.core.model.types.toAccountId
import com.lowbudgetlcs.domain.account.adapter.`in`.web.dto.NewAccountDto
import com.lowbudgetlcs.domain.account.adapter.`in`.web.dto.toDto
import com.lowbudgetlcs.domain.account.adapter.`in`.web.dto.toNewAccount
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory


fun Route.accountRoutesV1() {
    val accountService by inject<IAccountService>()
    val logger: Logger = LoggerFactory.getLogger(Application::class.java)

    route("/account") {
        post<AccountResourcesV1> {
            val dto = call.receive<NewAccountDto>()
            logger.debug(dto.toString())
            val created = accountService.createAccount(dto.toNewAccount())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
        get<AccountResourcesV1> {
            val accounts = accountService.getAllAccounts()
            call.respond(HttpStatusCode.OK, accounts.map { it.toDto() })
        }
        get<AccountResourcesV1.ById> { route ->
            val account = accountService.getAccount(route.accountId.toAccountId()) // throws if not found
            call.respond(account.toDto())
        }
    }
}
