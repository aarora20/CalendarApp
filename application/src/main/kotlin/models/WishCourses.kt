package models

import kotlinx.serialization.Serializable

@Serializable
data class WishCourses(
    val subjectCode: String,
    val catalogNumber: String,
    val courseTitle: String
)