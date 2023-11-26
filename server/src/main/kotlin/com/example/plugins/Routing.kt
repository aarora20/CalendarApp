package com.example.plugins

import com.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        userRouting()
        userCoursesRouting()
        courseSchedulesRouting()
        wishlistRouting()
        friendsRouting()
        customCalendarRouting()
        userCalendarCourseRouting()
    }
}
