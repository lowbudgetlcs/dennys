package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.serializers.InstantSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.dsl.module
import java.io.IOException
import java.time.Instant

const val MAX_RIOT_RETRIES = 3

val httpClientModule =
    module {
        single {
            HttpClient(CIO) {
                install(HttpRequestRetry) {
                    maxRetries = MAX_RIOT_RETRIES
                    retryIf { _, response ->
                        response.status == HttpStatusCode.TooManyRequests || response.status.value >= 500
                    }
                    retryOnExceptionIf { _, cause -> cause is IOException }
                    exponentialDelay()
                }
                install(ContentNegotiation) {
                    json(
                        Json {
                            encodeDefaults = true
                            serializersModule =
                                SerializersModule {
                                    contextual(Instant::class, InstantSerializer)
                                }
                        },
                    )
                }
            }
        }
    }
