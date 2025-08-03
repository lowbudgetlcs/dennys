package com.lowbudgetlcs.routes.api.v1

import com.lowbudgetlcs.domain.models.toRiotAccountId
import com.lowbudgetlcs.domain.services.AccountService
import com.lowbudgetlcs.routes.dto.accounts.NewRiotAccountDto
import com.lowbudgetlcs.routes.dto.accounts.RiotAccountDto
import com.lowbudgetlcs.routes.dto.accounts.toDto
import com.lowbudgetlcs.routes.dto.accounts.toNewRiotAccount
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.accountRoutesV1(
    accountService: AccountService
) {
    route("/account") {

        post {
            logger.info("📩 Received post on /v1/account")

            val dto = try {
                call.receive<NewRiotAccountDto>()
            } catch (e: Exception) {
                logger.warn("❌ Malformed request body", e)
                return@post call.respond(HttpStatusCode.BadRequest, "Invalid request format")
            }

            val newAccount = try {
                dto.toNewRiotAccount()
            } catch (e: IllegalArgumentException) {
                return@post call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid input")
            }

            try {
                val created = accountService.createAccount(newAccount)
                    ?: return@post call.respond(HttpStatusCode.InternalServerError, "Failed to insert account")

                call.respond(HttpStatusCode.Created, created.toDto())

            } catch (e: IllegalStateException) {
                logger.warn("⚠️ Conflict: ${e.message}")
                call.respond(HttpStatusCode.Conflict, e.message ?: "Riot account already exists")

            } catch (e: NoSuchElementException) {
                logger.warn("⚠️ Riot account not found from Riot API")
                call.respond(HttpStatusCode.NotFound, e.message ?: "Riot account not found")

            } catch (e: Exception) {
                logger.error("❌ Unexpected error in POST /account", e)
                call.respond(HttpStatusCode.InternalServerError, "Unexpected error")
            }
        }

        get {
            logger.info("📩 Received get on /v1/account")
            val accounts = accountService.getAllAccounts()
            val accountsDto : List<RiotAccountDto> = accounts.map { it.toDto() }
            call.respond(accountsDto)
        }

        get("{accountId}") {
            logger.info("📩 Received get on /v1/account/{accountId}")
            val accountIdField = call.parameters["accountId"]?.toIntOrNull()
                ?: return@get call.respondText(
                    text = "Invalid account ID",
                    status = HttpStatusCode.BadRequest
                )

            val accountId = accountIdField.toRiotAccountId()
            val account = accountService.getAccount(accountId)

            if (account != null) {
                call.respond(account.toDto())
            } else {
                call.respondText(
                    text = "Account not found",
                    status = HttpStatusCode.NotFound
                )
            }
        }
    }
}