package com.lowbudgetlcs

import com.lowbudgetlcs.routes.jsontest.TestJson
import com.lowbudgetlcs.routes.riot.RiotCallback
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
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
    }

    @Test
    fun jsonTestGet() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.get("/json-test").apply {
            val results = body<List<TestJson>>()
            assertEquals(HttpStatusCode.OK, status)

            val expected = listOf(TestJson("Title", 1))
            assertContentEquals(expected, results)
        }
    }

    @Test
    fun jsonTestPost() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val body = listOf(TestJson("Title", 1))
        client.post("/json-test") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(body)
        }.apply {
            val results = body<List<TestJson>>()
            assertEquals(HttpStatusCode.OK, status)
            assertContentEquals(body, results)
        }
    }

    @Test
    fun jsonTestCountGreater0() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val body = listOf(TestJson("Title", 0))
        client.post("/json-test") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(body)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }

    @Test
    fun riotCallbackPost() = testApplication {
        application {
            module()
        }
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val body = RiotCallback(startTime = 1000, shortCode = "ABCD", metaData = "", gameId = 1001)
        client.post("/riot/callback") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(body)
        }.apply{
            val response = body<RiotCallback>()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(body, response)
        }
    }
}
