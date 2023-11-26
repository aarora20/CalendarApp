package models

import kotlinx.serialization.Serializable

@Serializable
data class CustomCalendar(val id: String, val name: String)

@Serializable
data class CustomCalendarParams(val name: String)