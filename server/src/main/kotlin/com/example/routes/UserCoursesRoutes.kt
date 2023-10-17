package com.example.routes

import com.example.dao.dao
import com.example.models.Calendar
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.userCoursesRouting() {
    route("/user") {
        post("/{id}/courses") {
            val calendar = call.receive<Calendar>()
            val id = call.parameters.getOrFail<String>("id")
            call.respond(dao.updateUserCourses(id, calendar.courses))
        }

        get("/{id}/courses") {
            val id = call.parameters.getOrFail<String>("id")
            call.respond(dao.getAllUserCourses(id))
        }
    }
}
