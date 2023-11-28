package APIclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import models.AuthRes
import models.UserParams
import util.UserResponseData

object AuthClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    suspend fun loginUser(user: UserParams): UserResponseData<AuthRes>? {

        val response: HttpResponse = client.post("http://0.0.0.0:8080/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body()
        }
    }

    suspend fun registerUser(user: UserParams): UserResponseData<AuthRes>? {

        val response: HttpResponse = client.post("http://0.0.0.0:8080/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
            bearerAuth("")
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body()
        }
    }
}