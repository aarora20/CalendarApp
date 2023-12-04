package models

import kotlinx.serialization.Serializable

@Serializable
data class UserCourse(val courseId: String, val courseNum: String, val courseTitle: String, val component: String,
                      val startTime: String, val endTime: String, val startDate: String, val endDate: String,
                      val weekPattern: String)