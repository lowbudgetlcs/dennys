package com.lowbudgetlcs.util

import io.ktor.client.statement.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore

class RateLimiter {
    private val rateLimits = ConcurrentHashMap<String, Semaphore>()
    private val mutex = Mutex()

    suspend fun acquire(endpoint: String) {
        mutex.withLock {
            rateLimits.computeIfAbsent(endpoint) { Semaphore(1) }.acquire()
        }
    }

    suspend fun updateLimits(response: HttpResponse, endpoint: String) {
        mutex.withLock {
            val methodLimits = response.headers["X-Method-Rate-Limit"]
            val appLimits = response.headers["X-App-Rate-Limit"]

            val limit = parseRateLimit(methodLimits) ?: parseRateLimit(appLimits) ?: return
            rateLimits[endpoint] = Semaphore(limit)
        }
    }

    private fun parseRateLimit(headerValue: String?): Int? {
        return headerValue?.split(",")
            ?.mapNotNull { it.split(":").firstOrNull()?.toIntOrNull() }
            ?.minOrNull() // Use the most restrictive limit
    }
}