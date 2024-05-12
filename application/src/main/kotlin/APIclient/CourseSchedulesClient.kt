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
import models.Courses
import models.ScheduleData
import models.UserCourse
import models.WishCourse
import util.RouteResponseData

object CourseSchedulesClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    suspend fun getCourses(): Courses {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/courses/1241") {
            bearerAuth(store.getState().ktorToken)
        }
        return response.body<Courses>()
    }

    suspend fun getUserCourses(userId: String): List<UserCourse> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/$userId/courses") {
            bearerAuth(store.getState().ktorToken)
        }
        return response.body<List<UserCourse>>()
    }

    suspend fun addUserCourse(course: UserCourse, userId: String): UserCourse? {

        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/courses") {
            contentType(ContentType.Application.Json)
            setBody(course)
            bearerAuth(store.getState().ktorToken)
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body<RouteResponseData<UserCourse>>().data
        }
    }

    suspend fun updateSchedule(courses: List<UserCourse>, userId: String): Boolean {
        val response: HttpResponse = client.put("http://0.0.0.0:8080/users/$userId/courses/all") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("courses" to courses))
            bearerAuth(store.getState().ktorToken)
        }

        return response.status == HttpStatusCode.OK
    }

    suspend fun getCourseSchedule(courseId: String): List<ScheduleData> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/classSchedules/1241/${courseId}") {
            bearerAuth(store.getState().ktorToken)
        }
        return response.body<List<ScheduleData>>()
    }

    suspend fun addToWishlist(userId: String, course: WishCourse): Boolean {
        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/wishlist") {
            contentType(ContentType.Application.Json)
            setBody(course)
            bearerAuth(store.getState().ktorToken)
        }

        return response.status == HttpStatusCode.OK
    }

    suspend fun getWishlist(userId: String): List<WishCourse> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/$userId/wishlist") {
            bearerAuth(store.getState().ktorToken)
        }
        return response.body<List<WishCourse>>()
    }

    suspend fun removeFromWishlist(userId: String, subjectCode: String, catalogNumber: String, termYear: String): Boolean {
        return try {
            val response: HttpResponse = client.delete("http://0.0.0.0:8080/users/$userId/wishlist/$subjectCode/$catalogNumber/$termYear") {
                bearerAuth(store.getState().ktorToken)
            }
            response.status == HttpStatusCode.NoContent
        } catch (e: ClientRequestException) {
            println("Error in request: ${e.message}")
            false
        }
    }
}