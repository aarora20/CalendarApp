package APIclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import models.Courses

object CourseSchedulesClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getCourses(): Courses {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/courses/1241")
        return response.body<Courses>()
    }
}