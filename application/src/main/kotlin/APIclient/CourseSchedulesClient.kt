package APIclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
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
        val response: HttpResponse = client.get("http://0.0.0.0:8080/user/48d99b1a-d963-4686-870c-52d6bac6dd9f/courses")
        return response.body<List<UserCourse>>()
    }

    @OptIn(InternalAPI::class)
    suspend fun addUserCourse(course: UserCourse): UserCourse? {

        val response: HttpResponse = client.post("http://0.0.0.0:8080/user/48d99b1a-d963-4686-870c-52d6bac6dd9f/course") {
            contentType(ContentType.Application.Json)
            setBody(course)
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body()
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun updateSchedule(courses: List<UserCourse>): Boolean {

        val response: HttpResponse = client.post("http://0.0.0.0:8080/user/48d99b1a-d963-4686-870c-52d6bac6dd9f/courses") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("courses" to courses))
        }

        return response.body()
    }

    suspend fun getCourseSchedule(courseId: String): List<ScheduleData> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/classSchedules/1239/${courseId}")
        return response.body<List<ScheduleData>>()
    }
}