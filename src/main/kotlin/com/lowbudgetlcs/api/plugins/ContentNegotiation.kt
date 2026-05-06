package com.lowbudgetlcs.api.plugins

import com.lowbudgetlcs.serializers.InstantSerializer
import com.lowbudgetlcs.serializers.UUIDSerializer
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.Instant
import java.util.UUID


fun Application.setupContentNegotiation() {
    install(ContentNegotiation) {
        json(
            Json {
                serializersModule = SerializersModule {
                    contextual(Instant::class, InstantSerializer)
                    contextual(UUID::class, UUIDSerializer)
                }
                encodeDefaults = true
            },
        )
    }
}
