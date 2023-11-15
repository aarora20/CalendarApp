package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable

@Serializable
data class CustomCalendar(val id: String, val name: String)

@Serializable
data class CustomCalendarParams(val name: String)

object CustomCalendars : UUIDTable() {
    val userId = reference("user_id", Users)
    val name = varchar("name", 50)
}