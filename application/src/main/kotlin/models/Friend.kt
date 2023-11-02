package models

import kotlinx.serialization.Serializable

@Serializable
data class Friend(val userId: String, val friendId: String, val status: String)

@Serializable
data class FriendParams(val userId: String, val friendId: String)