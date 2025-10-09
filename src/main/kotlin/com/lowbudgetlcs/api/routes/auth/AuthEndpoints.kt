package com.lowbudgetlcs.api.routes.auth

import com.lowbudgetlcs.api.dto.Error
import com.lowbudgetlcs.api.dto.auth.CreateUserDto
import com.lowbudgetlcs.api.dto.auth.toDto
import com.lowbudgetlcs.api.dto.auth.toNewUser
import com.lowbudgetlcs.api.log
import com.lowbudgetlcs.api.setCidContext
import com.lowbudgetlcs.config.SecurityConfig
import com.lowbudgetlcs.config.appConfig
import com.lowbudgetlcs.domain.services.user.IUserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger(Application::class.java)

fun Route.authEndpoints(
    securityConfig: SecurityConfig = appConfig.security,
    userService: IUserService,
) {
    post<AuthResources.User> {
        call.setCidContext {
            call.log()
            val newUserDto = call.receive<CreateUserDto>()
            val newUser = newUserDto.toNewUser()
            logger.debug(newUser.toString())
            if (newUserDto.secretKey == securityConfig.createusersecret.value) {
                logger.debug("Secret key is correct.")
                val user = userService.createUser(newUser)
                call.respond(HttpStatusCode.Created, user.toDto())
            } else {
                logger.debug("Secret key is incorrect.")
                call.respond(
                    HttpStatusCode.Forbidden,
                    Error(HttpStatusCode.Forbidden.value, "Incorrect secret key provided."),
                )
            }
        }
    }
}
