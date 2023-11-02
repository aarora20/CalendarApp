package com.example.plugins

import com.example.routes.courseSchedulesRouting
import com.example.routes.userCoursesRouting
import com.example.routes.userRouting
import com.example.routes.wishlistRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        userRouting()
        userCoursesRouting()
        courseSchedulesRouting()
        wishlistRouting()
    }
}
