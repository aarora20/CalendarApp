package com.example.service

import com.example.dao.dao
import com.example.models.User
import com.example.util.UserResponse
import com.example.util.UserResponseData
import io.ktor.http.*
import org.mindrot.jbcrypt.BCrypt

class UserService {
    suspend fun registerUser(username: String, password: String): UserResponse<User> {
        return if (userExists(username)) {
            UserResponse(HttpStatusCode.BadRequest, data = UserResponseData(message = "username already exists"))

        } else {
            val user = dao.addNewUser(username, password)
            if (user != null) {
                UserResponse(data = UserResponseData(data = user, message = "success"))
            } else {
                UserResponse(HttpStatusCode.BadRequest, data = UserResponseData(message = "fail to register"))
            }
        }
    }


    suspend fun loginUser(username: String, password: String): UserResponse<User> {
        val user = dao.findUser(username)
        if (user != null) {
            if (BCrypt.checkpw(password, user.password)) {
                return UserResponse(data = UserResponseData(message = "success"))
            } else {
                return UserResponse(HttpStatusCode.BadRequest, data = UserResponseData(message = "wrong password"))
            }
        } else {
            return UserResponse(HttpStatusCode.BadRequest, data = UserResponseData(message = "user does not exists"))
        }
    }

    suspend fun userExists(username: String):  Boolean {
        return dao.findUser(username) != null;
    }
}

val userService: UserService = UserService()