package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable

@Serializable
data class WishlistCourse(val subjectCode: String, val catalogNumber: String, val courseTitle: String)

object Wishlists : UUIDTable() {
    val userId = reference("user_id", Users)
    val subjectCode = varchar("subject_code", 10)
    val catalogNumber = varchar("catalog_number", 10)
    val courseTitle = varchar("course_title", 50)

    init {
        uniqueIndex(userId, subjectCode, catalogNumber)
    }
}
