package com.example.routes

import com.example.dao.dao
import com.example.models.FriendParams
import com.example.models.UsernameParams
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.jetbrains.exposed.sql.exposedLogger

fun Route.friendsRouting() {
    route("/friends") {
        post("/send") {
            val params = call.receive<FriendParams>();
            val request = dao.addFriend(params.userId, params.friendId);
            if (request != null) {
                call.respond(request)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Fail to send request")
            }
        }

        get("/{id}") {
            val id = call.parameters.getOrFail<String>("id")
            call.respond(dao.findFriends(id))
        }

        get("/requests/pending/{id}") {
            val id = call.parameters.getOrFail<String>("id")
            call.respond(dao.findAllRequests(id))
        }

        post("/requests/accept") {
            val params = call.receive<FriendParams>();
            val request = dao.acceptFriendRequest(params.userId, params.friendId);
            if (request != null) {
                call.respond(request)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Fail to accept request")
            }
        }

        post("/requests/delete") {
            val params = call.receive<FriendParams>();
            val request = dao.rejectFriendRequest(params.userId, params.friendId);
            if (!request) {
                call.respond(HttpStatusCode.BadRequest, "Fail to reject request")
            } else {
                call.respond(request)
            }
        }
    }

}