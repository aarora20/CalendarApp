package com.example.service

import com.example.dao.dao
import com.example.models.AuthRes
import com.example.util.Response
import com.example.util.ResponseData
import io.ktor.http.*
import org.mindrot.jbcrypt.BCrypt

class UserService {
    suspend fun registerUser(username: String, password: String): Response<AuthRes> {
        return if (userExists(username)) {
            Response(HttpStatusCode.BadRequest, data = ResponseData(message = "username already exists"))
        } else {
            val user = dao.addNewUser(username, password)
            if (user != null) {
                Response(data = ResponseData(data = AuthRes("", user.id), message = "success"))
            } else {
                Response(HttpStatusCode.BadRequest, data = ResponseData(message = "fail to register"))
            }
        }
    }

    suspend fun loginUser(username: String, password: String): Response<AuthRes> {
        val user = dao.findUser(username)
        if (user != null) {
            if (BCrypt.checkpw(password, user.password)) {
                return Response(data = ResponseData(data = AuthRes("", user.id), message = "success"))
            } else {
                return Response(HttpStatusCode.BadRequest, data = ResponseData(message = "wrong password"))
            }
        } else {
            return Response(HttpStatusCode.BadRequest, data = ResponseData(message = "user does not exist"))
        }
    }

    suspend fun userExists(username: String):  Boolean {
        return dao.findUser(username) != null;
    }
}

val userService: UserService = UserService()