package models

import kotlinx.serialization.Serializable

@Serializable
data class WishCourse(
    val subjectCode: String,
    val catalogNumber: String,
    val courseTitle: String,
    val termYear: String
)