package com.example

import com.example.models.AuthRes
import com.example.models.CustomCalendar
import com.example.models.CustomCalendarParams
import com.example.models.UserParams
import com.example.util.ResponseData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CustomCalendarTest {
    val clientServer = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun testCustomCalendar(): Unit = testApplication {
        // create a user
        val response = clientServer.post("http://0.0.0.0:8080/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                UserParams("CSstudent", "password123")
            )
        }
        val id = response.body<ResponseData<AuthRes>>().data?.userId

        // add a new custom calendar to the user
        val calendar = CustomCalendarParams(
            name = "Winter 2024"
        )

        val addCalendarResponse = clientServer.post("http://0.0.0.0:8080/users/${id}/calendars") {
            contentType(ContentType.Application.Json)
            setBody(calendar)
        }

        assertEquals(HttpStatusCode.OK, addCalendarResponse.status)
        assertNotNull(addCalendarResponse.body());
        val calendarId = addCalendarResponse.body<ResponseData<CustomCalendar>>().data?.id.orEmpty()
        val expectedCalendar = CustomCalendar(
            id = calendarId,
            name = "Winter 2024"
        )
        assertEquals(ResponseData(data = expectedCalendar, message = "Create calendar success"), addCalendarResponse.body())

        // inserts duplicate calendar
        val addDuplicateResponse = clientServer.post("http://0.0.0.0:8080/users/${id}/calendars") {
            contentType(ContentType.Application.Json)
            setBody(calendar)
        }

        assertEquals(HttpStatusCode.BadRequest, addDuplicateResponse.status)
        assertNotNull(addDuplicateResponse.body());
        assertEquals(ResponseData(data = null, message = "Fail to create calendar"), addDuplicateResponse.body())

        // Add another calendar
        val calendar2 =  CustomCalendarParams(
            name = "Alternate Calendar"
        )

        val addCalendar2Response = clientServer.post("http://0.0.0.0:8080/users/${id}/calendars") {
            contentType(ContentType.Application.Json)
            setBody(calendar2)
        }
        assertEquals(HttpStatusCode.OK, addCalendar2Response.status)
        assertNotNull(addCalendar2Response.body());
        val calendarId2 = addCalendar2Response.body<ResponseData<CustomCalendar>>().data?.id.orEmpty()
        val expectedCalendar2 = CustomCalendar(
            id = calendarId2,
            name = "Alternate Calendar"
        )
        assertEquals(ResponseData(data = expectedCalendar2, message = "Create calendar success"), addCalendar2Response.body())

        // Fetch all calendars
        val fetchCalendarsResponse = clientServer.get("http://0.0.0.0:8080/users/${id}/calendars")
        assertEquals(HttpStatusCode.OK, fetchCalendarsResponse.status)
        assertNotNull(fetchCalendarsResponse.body());
        assertEquals(listOf(expectedCalendar, expectedCalendar2),fetchCalendarsResponse.body())


        // Delete a calendar
        val deleteCalendarResponse = clientServer.delete("http://0.0.0.0:8080/users/${id}/calendars/${calendarId2}")
        assertEquals(HttpStatusCode.OK, deleteCalendarResponse.status)
        assertNotNull(deleteCalendarResponse.body());
        assertEquals(ResponseData(data = true, message = "Success in deleting calendar"),deleteCalendarResponse.body())

        // Fetch again to verify
        val fetchDeletedResponse = clientServer.get("http://0.0.0.0:8080/users/${id}/calendars")
        assertEquals(HttpStatusCode.OK, fetchDeletedResponse.status)
        assertNotNull(fetchDeletedResponse.body());
        assertEquals(listOf(expectedCalendar),fetchDeletedResponse.body())
    }
}