package APIclient

import components.store
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

        val response: HttpResponse = client.post("https://calendar-app-planner-server-ycd64g7ulq-pd.a.run.app/timeTable/solve") {
            contentType(ContentType.Application.Json)
            setBody(schedule)
            bearerAuth(store.getState().springToken)
            timeout {
                requestTimeoutMillis = 30000
            }
        }

        val body = response.body<RouteResponseData<List<CourseSection>>>()

        return body.data
    }

}