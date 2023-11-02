package models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(val id: String = "default", val username: String, val password: String)

@Serializable
data class UserParams(val username: String, val password: String)

@Serializable
data class AuthRes(val token: String, val userId: String)

@Serializable
data class UsernameParams(val username: String)
