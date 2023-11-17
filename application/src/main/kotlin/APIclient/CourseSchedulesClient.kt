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
import io.ktor.util.*
import models.Courses
import models.ScheduleData
import models.UserCourse
import models.WishCourse

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

    suspend fun getUserCourses(userId: String): List<UserCourse> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/$userId/courses")
        return response.body<List<UserCourse>>()
    }

    suspend fun addUserCourse(course: UserCourse, userId: String): UserCourse? {

        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/courses") {
            contentType(ContentType.Application.Json)
            setBody(course)
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body()
        }
    }

    suspend fun updateSchedule(courses: List<UserCourse>, userId: String): Boolean {

        val response: HttpResponse = client.put("http://0.0.0.0:8080/users/$userId/courses/all") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("courses" to courses))
        }

        return response.body()
    }

    suspend fun getCourseSchedule(courseId: String): List<ScheduleData> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/classSchedules/1239/${courseId}")
        return response.body<List<ScheduleData>>()
    }

    @OptIn(InternalAPI::class)
    suspend fun addToWishlist(userId: String, course: WishCourse): Boolean {
        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/wishlist") {
            contentType(ContentType.Application.Json)
            setBody(course)
        }

        return response.status == HttpStatusCode.OK
    }

    suspend fun getWishlist(userId: String): List<WishCourse> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/$userId/wishlist")
        return response.body<List<WishCourse>>()
    }

    suspend fun removeFromWishlist(userId: String, subjectCode: String, catalogNumber: String): Boolean {
        return try {
            val response: HttpResponse = client.delete("http://0.0.0.0:8080/users/$userId/wishlist/$subjectCode/$catalogNumber")
            response.status == HttpStatusCode.NoContent
        } catch (e: ClientRequestException) {
            println("Error in request: ${e.message}")
            false
        }
    }
}