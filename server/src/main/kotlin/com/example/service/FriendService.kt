package com.example.service

import com.example.dao.dao
import com.example.models.User

class FriendService {
    // TODO

    suspend fun searchUsers(userId: String, username: String): List<User> {
        val searchedUsers = dao.findSimilarUsers(username)
        val currentFriends = dao.findAllRequests(userId).map { it.friendId }

        val freshUsers = searchedUsers.filter { user -> user.id !in currentFriends &&
                user.id != userId
        }

        return freshUsers
    }

}

val friendService: FriendService = FriendService()