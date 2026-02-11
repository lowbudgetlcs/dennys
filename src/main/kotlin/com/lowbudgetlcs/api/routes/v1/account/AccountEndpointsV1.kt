package com.lowbudgetlcs.api.routes.v1.account

import com.lowbudgetlcs.api.dto.accounts.NewAccountDto
import com.lowbudgetlcs.api.dto.accounts.toDto
import com.lowbudgetlcs.api.dto.accounts.toNewRiotAccount
import com.lowbudgetlcs.api.logCall
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.domain.models.player.account.toRiotAccountId
import com.lowbudgetlcs.domain.services.account.IAccountService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.accountEndpointsV1(accountService: IAccountService) {
    post<AccountResourcesV1> {
        call.setCidContext {
            logCall(call)
            val dto = call.receive<NewAccountDto>()
            logger.debug(dto.toString())
            val created = accountService.createAccount(dto.toNewRiotAccount())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }

    get<AccountResourcesV1> {
        call.setCidContext {
            logCall(call)
            val accounts = accountService.getAllAccounts()
            call.respond(HttpStatusCode.OK, accounts.map { it.toDto() })
        }
    }

    get<AccountResourcesV1.ById> { route ->
        call.setCidContext {
            logCall(call)
            val account = accountService.getAccount(route.accountId.toRiotAccountId()) // throws if not found
            call.respond(account.toDto())
        }
    }
}
