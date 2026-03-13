package com.lowbudgetlcs.config.modules

import com.lowbudgetlcs.serializers.InstantSerializer
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.dsl.module
import java.time.Instant

val httpClientModule =
    module {
        single {
            HttpClient(CIO) {
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
