package models

import kotlinx.serialization.Serializable

@Serializable
data class UserCalendarCourse(val courseId: String, val courseNum: String, val courseTitle: String, val component: String,
                              val startTime: String, val endTime: String, val weekPattern: String) {
    override fun toString(): String {
        return "${this.courseId} ${this.courseNum} ${courseTitle} ${component} ${startTime} ${endTime} ${weekPattern}"
    }
}


@Serializable
data class UserCalendarCourseList(val courses: List<UserCalendarCourse>)