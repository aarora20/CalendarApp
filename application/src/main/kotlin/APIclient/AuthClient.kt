package APIclient

import components.store
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
import util.RouteResponseData

object AuthClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    suspend fun loginUser(user: UserParams): RouteResponseData<AuthRes>? {

        val response: HttpResponse = client.post("https://calendar-app-server-ycd64g7ulq-pd.a.run.app/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(user)
            bearerAuth(store.getState().token)
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body()
        }
    }

    suspend fun registerUser(user: UserParams): RouteResponseData<AuthRes>? {

        val response: HttpResponse = client.post("https://calendar-app-server-ycd64g7ulq-pd.a.run.app/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
            bearerAuth(store.getState().token)
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body()
        }
    }
}