package com.lowbudgetlcs.api

import com.lowbudgetlcs.config.LoggingConfig
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.calllogging.CallLogging
import org.koin.ktor.ext.inject
import java.util.UUID

fun Application.setupLogging() {
    val loggingConfig by inject<LoggingConfig>()
    install(CallId) {
        header("X-Request-Id")
        generate { UUID.randomUUID().toString() }
    }
    install(CallLogging) {
        callIdMdc(loggingConfig.cid)
    }
}
