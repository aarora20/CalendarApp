package com.example.plugins

import com.example.dao.dao
import com.example.models.Calendar
import com.example.models.User
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.jetbrains.exposed.sql.exposedLogger

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/user/{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val user = dao.user(id)
            if (user != null) {
                call.respond(user)
            } else {
                call.respond("No user found!")
            }

        }

        post("/user") {
            val user = call.receive<User>()
            val createdUser = dao.addNewUser(user.username)
            if (createdUser != null) {
                call.respond(mapOf("id" to createdUser.id.toString()))
            } else {
                call.respond("Creation Failed")
            }
        }

        post("/user/{id}/courses") {
            val calendar = call.receive<Calendar>()
            val id = call.parameters.getOrFail<String>("id")
            call.respond(dao.updateUserCourses(id, calendar.courses))
        }

        get("/user/{id}/courses") {
            val id = call.parameters.getOrFail<String>("id")
            call.respond(dao.getAllUserCourses(id))
        }
    }
}
