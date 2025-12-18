package com.lowbudgetlcs.api

import io.ktor.server.application.*
import io.ktor.util.*
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.MDC
import java.util.*

val CorrelationIdPlugin =
    createApplicationPlugin(name = "CorrelationIdPlugin") {
        onCall { call ->
            val cid = call.request.headers["X-Request-Id"] ?: UUID.randomUUID().toString().take(8)
            call.attributes.put(CorrelationIdKey, cid)
        }
    }

val CorrelationIdKey = AttributeKey<String>("CID")

suspend fun ApplicationCall.setCidContext(block: suspend ApplicationCall.() -> Unit) {
    val cid = attributes[CorrelationIdKey]
    MDC.put("c-id", cid)
    try {
        withContext(MDCContext()) {
            block()
        }
    } finally {
        MDC.remove("c-id")
    }
}
