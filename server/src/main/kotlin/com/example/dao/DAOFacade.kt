package com.example.dao

import com.example.models.*

interface DAOFacade {
    suspend fun allUsers(): List<User>
    suspend fun user(id: String): User?
    suspend fun addNewUser(username: String): User?

    suspend fun deleteUser(id: String): Boolean

    suspend fun updateUserCourses(userIdArg: String, courses: List<Course>): Boolean
    suspend fun getAllUserCourses(id: String): List<Course>


}