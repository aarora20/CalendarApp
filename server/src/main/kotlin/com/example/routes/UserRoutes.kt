package com.example.routes

import com.example.dao.dao
import com.example.models.User
import com.example.models.UserParams
import com.example.service.userService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.userRouting() {
    route("/auth") {
        post("/register") {
            val params = call.receive<UserParams>()
            val res = userService.registerUser(params.username, params.password)
            call.respond(res.statusCode, res.data)
        }

        post("/login") {
            val params = call.receive<UserParams>()
            val res = userService.loginUser(params.username, params.password)
            call.respond(res.statusCode, res.data)
        }
    }

    route("/user") {
        get("/{id}") {
            val id = call.parameters.getOrFail<String>("id")
            val user = dao.user(id)
            if (user != null) {
                call.respond(user)
            } else {
                call.respond("No user found!")
            }

        }
    }

}