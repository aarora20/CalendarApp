package com.example.routes

import com.example.dao.dao
import com.example.models.CustomCalendarParams
import com.example.util.ResponseData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.customCalendarRouting() {
    route("/users/{id}/calendars") {
        post {
            val calendarParams = call.receive<CustomCalendarParams>()
            val id = call.parameters.getOrFail<String>("id")
            val calendar = dao.addCustomCalendar(id, calendarParams)
            if (calendar != null) {
                call.respond(ResponseData(
                    data = calendar,
                    message = "Create calendar success"
                ))
            } else {
                call.respond(HttpStatusCode.BadRequest, ResponseData(data = null, message = "Fail to create calendar"))
            }
        }

        delete("/{calendarId}") {
            val id = call.parameters.getOrFail<String>("id")
            val calendarId = call.parameters.getOrFail<String>("calendarId")
            val isDeleted = dao.deleteCustomCalendar(id, calendarId)
            if (isDeleted) {
                call.respond(ResponseData(data = true, message = "Success in deleting calendar"))
            } else {
                call.respond(HttpStatusCode.BadRequest, ResponseData(data = true, message = "Fail to delete calendar"))
            }
        }

        get() {
            val id = call.parameters.getOrFail<String>("id")
            call.respond(dao.getCustomCalendars(id))
        }
    }
}