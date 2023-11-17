package APIclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import models.*
import util.UserResponseData

object FriendsClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun searchUsers(userId: String, username: String): List<User> {

        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/$userId/find") {
            contentType(ContentType.Application.Json)
            setBody(UsernameParams(username))
        }

        return response.body()
    }

    suspend fun sendFriendRequest(userId: String, friendId: String): Friend? {
        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/friends/send") {
            contentType(ContentType.Application.Json)
            setBody(FriendParams(userId, friendId))
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body()
        }
    }

    suspend fun getFriendList(userId: String): List<User> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/$userId/friends") {
        }
        return response.body()
    }

    suspend fun getPendingList(userId: String): List<User> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/$userId/friends/requests/pending") {
        }
        return response.body()
    }

    suspend fun acceptFriendRequest(userId: String, friendId: String): Friend? {
        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/friends/requests/accept") {
            contentType(ContentType.Application.Json)
            setBody(FriendParams(userId, friendId))
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body()
        }
    }

    suspend fun rejectFriendRequest(userId: String, friendId: String): Boolean {
        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/friends/requests/reject") {
            contentType(ContentType.Application.Json)
            setBody(FriendParams(userId, friendId))
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            false
        } else {
            response.body()
        }
    }
}