package com.lowbudgetlcs

import com.lowbudgetlcs.routes.api.v1.jsontest.TestJson
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
        client.get("/api/v1/json-test").apply {
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
        client.post("/api/v1/json-test") {
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
        client.post("/api/v1/json-test") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(body)
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
    }
}
