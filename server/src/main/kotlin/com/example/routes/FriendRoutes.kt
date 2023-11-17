package com.example.routes

import com.example.dao.dao
import com.example.models.FriendParams
import com.example.service.friendService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.friendsRouting() {
    route("/users/{userId}/friends") {
        post("/send") {
            val params = call.receive<FriendParams>();
            val request = friendService.sendFriendRequest(params.userId, params.friendId);
            if (request != null) {
                call.respond(request)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Fail to send request")
            }
        }

        post("/unfriend") {
            val params = call.receive<FriendParams>();
            val request = dao.unfriend(params.userId, params.friendId);
            if (!request) {
                call.respond(HttpStatusCode.BadRequest, "Fail to unfriend")
            } else {
                call.respond(request)
            }
        }

        get {
            val id = call.parameters.getOrFail<String>("userId")
            call.respond(dao.findFriends(id))
        }

        get("/requests/incoming") {
            val id = call.parameters.getOrFail<String>("userId")
            call.respond(dao.findAllPending(id))
        }

        get("/requests/sent") {
            val id = call.parameters.getOrFail<String>("userId")
            call.respond(dao.findAllSent(id))
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
            val request = dao.deleteFriendRequest(params.userId, params.friendId);
            if (!request) {
                call.respond(HttpStatusCode.BadRequest, "Fail to reject request")
            } else {
                call.respond(request)
            }
        }
    }

}