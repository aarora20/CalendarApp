package models

import kotlinx.serialization.Serializable

@Serializable
data class UserCalendarCourse(val courseId: String, val courseNum: String, val courseTitle: String, val component: String,
                              val startTime: String, val endTime: String, val weekPattern: String)

@Serializable
data class UserCalendarCourseList(val courses: List<UserCalendarCourse>)