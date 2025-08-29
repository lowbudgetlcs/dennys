package com.lowbudgetlcs.api.routes.v1.account

import com.lowbudgetlcs.domain.models.riot.account.toRiotAccountId
import com.lowbudgetlcs.domain.services.account.AccountService
import com.lowbudgetlcs.api.dto.accounts.NewAccountDto
import com.lowbudgetlcs.api.dto.accounts.toDto
import com.lowbudgetlcs.api.dto.accounts.toNewRiotAccount
import com.lowbudgetlcs.api.setCidContext
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.accountEndpointsV1(
    accountService: AccountService
) {
    post<AccountResourcesV1> {
        call.setCidContext {
            logger.info("📩 Received POST /v1/account")
            val dto = call.receive<NewAccountDto>()
            logger.debug(dto.toString())
            val created = accountService.createAccount(dto.toNewRiotAccount())
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }

    get<AccountResourcesV1> {
        call.setCidContext {
            logger.info("📩 Received GET /v1/account")
            val accounts = accountService.getAllAccounts()
            call.respond(HttpStatusCode.OK, accounts.map { it.toDto() })
        }
    }

    get<AccountResourcesV1.ById> { route ->
        call.setCidContext {
            logger.info("📩 Received GET /v1/account/${route.accountId}")
            val account = accountService.getAccount(route.accountId.toRiotAccountId()) // throws if not found
            call.respond(account.toDto())
        }
    }
}