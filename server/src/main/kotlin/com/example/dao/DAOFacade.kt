package com.example.dao

import com.example.models.*

interface DAOFacade {

    // Users/Auth
    // Maybe don't need
    suspend fun allUsers(): List<User>
    suspend fun user(id: String): User?
    suspend fun addNewUser(username: String, password: String): User?

    suspend fun findUser(username: String): User?

    suspend fun findSimilarUsers(username: String): List<User>

    suspend fun deleteUser(id: String): Boolean

    // Friends
    suspend fun addFriend(userId: String, friendId: String): Friend?

    suspend fun acceptFriendRequest(userId: String, friendId: String): Friend?

    suspend fun findFriendRequest(userId: String, friendId: String): Boolean

    suspend fun rejectFriendRequest(userId: String, friendId: String): Boolean

    // User Courses
    suspend fun addUserCourse(userIdArg: String, course: UserCourse): UserCourse?
    suspend fun updateUserCourses(userIdArg: String, courses: List<UserCourse>): Boolean
    suspend fun getAllUserCourses(id: String): List<UserCourse>


}