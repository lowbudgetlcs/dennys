package com.lowbudgetlcs

import com.lowbudgetlcs.api.plugins.setupContentNegotiation
import com.lowbudgetlcs.api.plugins.setupCors
import com.lowbudgetlcs.api.plugins.setupLogging
import com.lowbudgetlcs.api.plugins.setupSessions
import com.lowbudgetlcs.api.plugins.setupStatusPages
import com.lowbudgetlcs.api.routes
import com.lowbudgetlcs.config.modules.apiRouteModule
import com.lowbudgetlcs.config.modules.configModule
import com.lowbudgetlcs.config.modules.databaseModule
import com.lowbudgetlcs.config.modules.divisionModule
import com.lowbudgetlcs.config.modules.gatewayModule
import com.lowbudgetlcs.config.modules.hashingModule
import com.lowbudgetlcs.config.modules.httpClientModule
import com.lowbudgetlcs.config.modules.repositoryModule
import com.lowbudgetlcs.config.modules.serviceModule
import com.lowbudgetlcs.domain.auth.adapter.`in`.web.setupAuth
import com.lowbudgetlcs.domain.auth.core.port.IAuthService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.resources.Resources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

val <T : Any> T.logger: Logger
    get() = LoggerFactory.getLogger(this.javaClass)

private val SESSION_CLEANUP_DELAY = 360.seconds

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.startSessionCleanup() = CoroutineScope(Dispatchers.Default).launch {
    logger.debug("Starting session cleanup...")
    val authService by inject<IAuthService>()
    repeat(Int.MAX_VALUE) {
        authService.cleanupExpiredSessions()
        delay(SESSION_CLEANUP_DELAY)
    }
}

fun Application.module() {
    logger.info("🔧 Performing opening duties...")

    install(Koin) {
        slf4jLogger()
        modules(
            configModule,
            databaseModule,
            serviceModule,
            repositoryModule,
            gatewayModule,
            httpClientModule,
            hashingModule,
            apiRouteModule,

            // New - Module-per-domain
            divisionModule
        )
    }

    setupContentNegotiation()
    setupLogging()
    setupStatusPages()
    setupAuth()
    setupCors()
    setupSessions()
    install(Resources)
    install(AutoHeadResponse)
    routes()
    startSessionCleanup()
    logger.info("🍽️ Denny's is open! Ready to serve requests. 🚀")
}
