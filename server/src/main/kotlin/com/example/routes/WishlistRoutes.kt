package com.example.routes

import com.example.models.WishlistCourse
import com.example.service.wishlistService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.wishlistRouting() {
    route("/user") {
        post("/{id}/wishlist") {
            val course = call.receive<WishlistCourse>()
            val id = call.parameters.getOrFail<String>("id")
            val addedCourse: WishlistCourse? = wishlistService.addToWishlist(id, course)
            if (addedCourse != null) {
                call.respond(addedCourse)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Failed to add course to wishlist")
            }
        }

        delete("/{id}/wishlist/{courseId}") {
            val id = call.parameters.getOrFail<String>("id")
            val courseId = call.parameters.getOrFail<String>("courseId")
            if (wishlistService.removeFromWishlist(id, courseId)) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Failed to remove course from wishlist")
            }
        }

        get("/{id}/wishlist") {
            val id = call.parameters.getOrFail<String>("id")
            call.respond(wishlistService.getUserWishlist(id))
        }
    }
}
