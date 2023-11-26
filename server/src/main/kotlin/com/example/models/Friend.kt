package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable

//enum class FriendStatus {
//    PENDING, ACCEPTED, REJECTED
//}

@Serializable
data class Friend(val userId: String, val friendId: String, val status: String)

@Serializable
data class FriendParams(val userId: String, val friendId: String)


object Friends : UUIDTable() {
    val userId = reference("user_id", Users)
    val friendId = reference("friend_id", Users)
    val status = varchar("status", 50)
}