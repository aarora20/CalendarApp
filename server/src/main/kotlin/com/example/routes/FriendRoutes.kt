package com.example.routes

import com.example.dao.dao
import com.example.models.FriendParams
import com.example.models.UsernameParams
import com.example.service.friendService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.friendsRouting() {
    // Find all similar users in the database to the given username
    route("/users/{id}/find") {
        get() {
            val id = call.parameters.getOrFail<String>("id")
            val params = call.receive<UsernameParams>()
            call.respond(friendService.searchUsers(id, params.username))
        }
    }

    route("/users/{userId}/friends") {
        // send a friend request
        post("/send") {
            val params = call.receive<FriendParams>();
            val request = friendService.sendFriendRequest(params.userId, params.friendId);
            if (request != null) {
                call.respond(request)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Fail to send request")
            }
        }

        // unfriend an existing friend
        post("/unfriend") {
            val params = call.receive<FriendParams>();
            val request = dao.unfriend(params.userId, params.friendId);
            if (!request) {
                call.respond(HttpStatusCode.BadRequest, "Fail to unfriend")
            } else {
                call.respond(request)
            }
        }

        // get all friends
        get {
            val id = call.parameters.getOrFail<String>("userId")
            call.respond(dao.findFriends(id))
        }

        // get all incoming friend requests
        get("/requests/incoming") {
            val id = call.parameters.getOrFail<String>("userId")
            call.respond(dao.findAllPending(id))
        }

        // get all the request that user has sent
        get("/requests/sent") {
            val id = call.parameters.getOrFail<String>("userId")
            call.respond(dao.findAllSent(id))
        }

        // accept an incoming friend request
        post("/requests/accept") {
            val params = call.receive<FriendParams>();
            val request = dao.acceptFriendRequest(params.userId, params.friendId);
            if (request != null) {
                call.respond(request)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Fail to accept request")
            }
        }

        // delete a request whether it's incoming or sent
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