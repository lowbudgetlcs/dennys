package com.lowbudgetlcs.routes

import io.ktor.server.application.*
import io.ktor.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.MDC
import java.util.*

val CorrelationIdPlugin = createApplicationPlugin(name = "CorrelationIdPlugin") {
    onCall { call ->
        val cid = UUID.randomUUID().toString().take(8)
        MDC.put("c-id", cid)
        call.attributes.put(CorrelationIdKey, cid)
        call.request.call.application.launch {
            withContext(MDCContext()) {}
        }
    }
    onCallRespond {
        MDC.remove("c-id")
    }
}

val CorrelationIdKey = AttributeKey<String>("CID")