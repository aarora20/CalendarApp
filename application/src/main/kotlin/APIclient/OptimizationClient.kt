package APIclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import models.AuthRes
import models.CourseSection
import models.OptimizedSchedule
import models.UserParams
import util.RouteResponseData

object OptimizationClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                (Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            )
        }
        install(HttpTimeout)
    }
    suspend fun optimizeSchedule(schedule: OptimizedSchedule): List<CourseSection>? {

        val response: HttpResponse = client.post("http://0.0.0.0:8889/timeTable/solve") {
            contentType(ContentType.Application.Json)
            setBody(schedule)
            timeout {
                requestTimeoutMillis = 30000
            }
        }

        println(response)

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body()
        }
    }

}