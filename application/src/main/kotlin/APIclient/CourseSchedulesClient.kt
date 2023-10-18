package APIclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import models.Courses
import models.ScheduleData
import models.UserCourse

object CourseSchedulesClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getCourses(): Courses {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/courses/1239")
        return response.body<Courses>()
    }

    suspend fun getUserCourses(): List<UserCourse> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/user/6634487e-8aa6-4ca5-8f89-9f9bdc6ffd83/courses")
        return response.body<List<UserCourse>>()
    }

    suspend fun getCourseSchedule(courseId: String): List<ScheduleData> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/classSchedules/1239/${courseId}")
        return response.body<List<ScheduleData>>()
    }
}