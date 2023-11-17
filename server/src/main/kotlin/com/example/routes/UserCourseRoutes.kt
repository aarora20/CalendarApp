package com.example.routes

import com.example.dao.dao
import com.example.models.Calendar
import com.example.models.UserCourse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.userCoursesRouting() {
    route("/users") {
        put("/{id}/courses/all") {
            val calendar = call.receive<Calendar>()
            val id = call.parameters.getOrFail<String>("id")
            call.respond(dao.updateUserCourses(id, calendar.courses))
        }

        post("/{id}/courses") {
            val course = call.receive<UserCourse>()
            val id = call.parameters.getOrFail<String>("id")
            val addedCourse: UserCourse? = dao.addUserCourse(id, course)
            if (addedCourse != null) {
                call.respond(addedCourse)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Fail to add course")
            }
        }

        get("/{id}/courses") {
            val id = call.parameters.getOrFail<String>("id")
            call.respond(dao.getAllUserCourses(id))
        }
    }
}
