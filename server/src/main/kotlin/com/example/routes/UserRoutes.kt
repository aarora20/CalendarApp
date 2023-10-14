package com.example.routes

import com.example.dao.dao
import com.example.models.User
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.userRouting() {
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

        post {
            val user = call.receive<User>()
            val createdUser = dao.addNewUser(user.username)
            if (createdUser != null) {
                call.respond(mapOf("id" to createdUser.id.toString()))
            } else {
                call.respond("Creation Failed")
            }
        }
    }

}