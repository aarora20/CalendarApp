package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.id.UUIDTable

@Serializable
data class User(val id: String, val username: String, val password: String)

@Serializable
data class UserParams(val username: String, val password: String)

@Serializable
data class UsernameParams(val username: String)

@Serializable
data class AuthRes(val token: String, val userId: String)

object Users : UUIDTable() {
    val username = varchar("username", 128)
    val password = varchar("password", 100)
}