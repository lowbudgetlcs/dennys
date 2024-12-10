package com.lowbudgetlcs

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        client.get("/healthcheck)").apply {
            val httpResponse: String = body()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Status: OK", httpResponse)
        }
    }

}
