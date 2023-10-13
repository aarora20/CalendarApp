package com.example.models

import org.jetbrains.exposed.dao.id.UUIDTable

data class UserCourse(val userId: String, val courseId: String, val times: String)

object UserCourses : UUIDTable() {
    val userId = reference("user_id", Users)
    val courseId = varchar("course_id", 50)
    val times = varchar("times", 50)
}
