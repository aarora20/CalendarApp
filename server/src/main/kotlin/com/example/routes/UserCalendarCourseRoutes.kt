package com.example.routes

import com.example.dao.dao
import com.example.models.UserCalendarCourse
import com.example.models.UserCalendarCourseList
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.userCalendarCourseRouting() {
    route("/users/{id}/calendars/{calendarId}") {
        put("/calendarCourses/all") {
            val calendar = call.receive<UserCalendarCourseList>()
            val id = call.parameters.getOrFail<String>("id")
            val calendarId = call.parameters.getOrFail<String>("calendarId")
            call.respond(dao.updateUserCalendarCourses(id, calendarId, calendar.courses))
        }

        post("/calendarCourses") {
            val course = call.receive<UserCalendarCourse>()
            val id = call.parameters.getOrFail<String>("id")
            val calendarId = call.parameters.getOrFail<String>("calendarId")
            val addedCourse: UserCalendarCourse? = dao.addUserCalendarCourse(id, calendarId, course)
            if (addedCourse != null) {
                call.respond(addedCourse)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Fail to add course")
            }
        }

        get("/calendarCourses") {
            val id = call.parameters.getOrFail<String>("id")
            val calendarId = call.parameters.getOrFail<String>("calendarId")
            call.respond(dao.getAllUserCalendarCourses(id, calendarId))
        }
    }
}
