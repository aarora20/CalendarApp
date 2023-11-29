package com.example

import com.example.models.AuthRes
import com.example.models.UserParams
import com.example.plugins.*
import com.example.util.ResponseData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.junit.runner.OrderWith
import kotlin.test.*

class AuthTest {
    val clientServer = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun testAuthLogin(): Unit = testApplication {
        // register a new user
        val response = clientServer.post("http://0.0.0.0:8080/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                UserParams("user1", "password")
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(response.body());
        val id = response.body<ResponseData<AuthRes>>().data?.userId
        assertNotEquals("", id)
        assertEquals(ResponseData(data = AuthRes("", id!!), message = "success"), response.body())

        // login with the new user
        val loginResponse = clientServer.post("http://0.0.0.0:8080/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                UserParams("user1", "password")
            )
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        assertNotNull(loginResponse.body());
        val id2 = loginResponse.body<ResponseData<AuthRes>>().data?.userId
        assertNotEquals("", id2)
        assertEquals(ResponseData(data = AuthRes("", id2!!), message = "success"), loginResponse.body())

        // register with existing user
        val sameUserResponse = clientServer.post("http://0.0.0.0:8080/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                UserParams("user1", "password")
            )
        }
        assertEquals(HttpStatusCode.BadRequest, sameUserResponse.status)
        assertEquals(ResponseData(data = null, message = "username already exists"), sameUserResponse.body())

        // login with a non existent user
        val wrongUserResponse = clientServer.post("http://0.0.0.0:8080/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                UserParams("user4444", "12434363")
            )
        }
        assertEquals(HttpStatusCode.BadRequest, wrongUserResponse.status)
        assertEquals(ResponseData(data = null, message = "user does not exist"), wrongUserResponse.body())

        // login with the wrong password
        val wrongPasswordResponse = clientServer.post("http://0.0.0.0:8080/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                UserParams("user1", "wrong")
            )
        }
        assertEquals(HttpStatusCode.BadRequest, wrongPasswordResponse.status)
        assertEquals(ResponseData(data = null, message = "wrong password"), wrongPasswordResponse.body())
    }

}


