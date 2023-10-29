package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.id.UUIDTable

@Serializable
data class User(@Transient val id: String = "default", val username: String, val password: String)

@Serializable
data class UserParams(val username: String, val password: String)

object Users : UUIDTable() {
    val username = varchar("username", 128)
    val password = varchar("password", 100)
}