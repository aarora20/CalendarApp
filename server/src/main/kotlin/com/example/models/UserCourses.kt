package com.example.models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*

data class UserCourse(val userId: String, val courseId: String, val times: String)

object UserCourses : UUIDTable() {
    val userId = reference("user_id", Users)
    val courseId = varchar("course_id", 50)
    val times = varchar("times", 50)
}
