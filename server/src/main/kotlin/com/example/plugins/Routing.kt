package com.example.plugins

import com.example.routes.userRouting
import com.example.routes.courseSchedulesRouting
import com.example.routes.friendsRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.routes.userCoursesRouting

fun Application.configureRouting() {
    routing {
        userRouting()
        userCoursesRouting()
        courseSchedulesRouting()
        friendsRouting()
    }
}
