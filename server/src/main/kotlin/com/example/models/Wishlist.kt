package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable

@Serializable
data class WishlistCourse(val courseId: String, val courseTitle: String)

object Wishlists : UUIDTable() {
    val userId = reference("user_id", Users)
    val courseId = varchar("course_id", 50)
    val courseTitle = varchar("course_title", 50)

    init {
        uniqueIndex(userId, courseId)
    }
}
