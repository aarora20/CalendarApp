package com.example.dao

import com.example.models.*

interface DAOFacade {
    suspend fun allUsers(): List<User>
    suspend fun user(id: String): User?
    suspend fun addNewUser(username: String): User?

    suspend fun deleteUser(id: String): Boolean

    suspend fun addUserCourse(userIdArg: String, course: UserCourse): UserCourse?
    suspend fun updateUserCourses(userIdArg: String, courses: List<UserCourse>): Boolean
    suspend fun getAllUserCourses(id: String): List<UserCourse>


}