package APIclient

import components.store
import models.UserCalendarCourse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import models.CustomCalendar
import models.CustomCalendarParams
import util.RouteResponseData

object CustomCalendarClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getCalendars(userId: String): List<CustomCalendar> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/${userId}/calendars") {
            bearerAuth(store.getState().ktorToken)
        }
        return response.body()
    }

    suspend fun addCalendar(userId: String, calendarParams: CustomCalendarParams): CustomCalendar? {
        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/calendars") {
            contentType(ContentType.Application.Json)
            setBody(calendarParams)
            bearerAuth(store.getState().ktorToken)
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body<RouteResponseData<CustomCalendar>>().data
        }
    }

    suspend fun deleteCalendar(userId: String, calendarId: String): Boolean {
        val response: HttpResponse = client.delete("http://0.0.0.0:8080/users/$userId/calendars/$calendarId") {
            bearerAuth(store.getState().ktorToken)
        }
        return response.status == HttpStatusCode.OK
    }


    suspend fun getCalendarCourses(userId: String, calendarId: String): List<UserCalendarCourse> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/$userId/calendars/$calendarId/calendarCourses") {
            bearerAuth(store.getState().ktorToken)
        }
        return response.body<List<UserCalendarCourse>>()
    }


    suspend fun addCalendarCourse(userId: String, calendarId: String, course: UserCalendarCourse): UserCalendarCourse? {

        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/calendars/$calendarId/calendarCourses") {
            contentType(ContentType.Application.Json)
            setBody(course)
            bearerAuth(store.getState().ktorToken)
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body<RouteResponseData<UserCalendarCourse>>().data
        }
    }

    suspend fun updateCalendar(userId: String, calendarId: String, courses: List<UserCalendarCourse>): Boolean {

        val response: HttpResponse = client.put("http://0.0.0.0:8080/users/$userId/calendars/$calendarId/" +
                "calendarCourses/all") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("courses" to courses))
            bearerAuth(store.getState().ktorToken)
        }

        return response.status == HttpStatusCode.OK
    }
}