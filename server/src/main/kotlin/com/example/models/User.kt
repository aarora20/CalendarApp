package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*

@Serializable
data class User(@Transient val id: String = "default", val username: String)

object Users : UUIDTable() {
    val username = varchar("username", 128)
}