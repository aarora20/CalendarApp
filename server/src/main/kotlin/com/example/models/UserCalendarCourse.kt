package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

@Serializable
data class UserCalendarCourse(val courseId: String, val courseNum: String, val courseTitle: String, val component: String,
                      val startTime: String, val endTime: String, val weekPattern: String)

@Serializable
data class UserCalendarCourseList(val courses: List<UserCalendarCourse>)

// Entry will be delete whenever parent row gets deleted (Custom Calendar)
object UserCalendarCourses : UUIDTable() {
    val userId = reference("user_id", Users)
    val calendarId = reference("calendar_id", CustomCalendars, onDelete = ReferenceOption.CASCADE)
    val courseId = varchar("course_id", 50)
    val courseNum = varchar("course_number", 50)
    val courseTitle = varchar("course_title", 50)
    val component = varchar("component", 50)
    val startTime = varchar("start_time", 50)
    val endTime = varchar("end_time", 50)
    val weekPattern = varchar("week_pattern", 50)
}