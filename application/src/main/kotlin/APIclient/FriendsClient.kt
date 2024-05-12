package APIclient

import components.store
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import models.*
import util.RouteResponseData

object FriendsClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun searchUsers(userId: String, username: String): List<User> {

        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/find") {
            contentType(ContentType.Application.Json)
            setBody(UsernameParams(username))
            bearerAuth(store.getState().ktorToken)
        }

        return response.body()
    }

    suspend fun sendFriendRequest(userId: String, friendId: String): Friend? {
        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/friends/send") {
            contentType(ContentType.Application.Json)
            setBody(FriendParams(userId, friendId))
            bearerAuth(store.getState().ktorToken)
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body<RouteResponseData<Friend>>().data
        }
    }

    suspend fun getFriendList(userId: String): List<User> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/$userId/friends") {
            bearerAuth(store.getState().ktorToken)
        }
        return response.body()
    }

    suspend fun getIncomingList(userId: String): List<User> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/$userId/friends/requests/incoming") {
            bearerAuth(store.getState().ktorToken)
        }
        return response.body()
    }

    suspend fun getSentList(userId: String): List<User> {
        val response: HttpResponse = client.get("http://0.0.0.0:8080/users/$userId/friends/requests/sent") {
            bearerAuth(store.getState().ktorToken)
        }
        return response.body()
    }


    suspend fun acceptFriendRequest(userId: String, friendId: String): Friend? {
        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/friends/requests/accept") {
            contentType(ContentType.Application.Json)
            setBody(FriendParams(userId, friendId))
            bearerAuth(store.getState().ktorToken)
        }

        return if (response.status == HttpStatusCode.BadRequest) {
            null
        } else {
            response.body<RouteResponseData<Friend>>().data
        }
    }

    suspend fun rejectFriendRequest(userId: String, friendId: String): Boolean {
        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/friends/requests/delete") {
            contentType(ContentType.Application.Json)
            setBody(FriendParams(userId, friendId))
            bearerAuth(store.getState().ktorToken)
        }

        return response.status == HttpStatusCode.OK

    }

    suspend fun unfriend(userId: String, friendId: String): Boolean {
        val response: HttpResponse = client.post("http://0.0.0.0:8080/users/$userId/friends/unfriend") {
            contentType(ContentType.Application.Json)
            setBody(FriendParams(userId, friendId))
            bearerAuth(store.getState().ktorToken)
        }

        return response.status == HttpStatusCode.OK
    }
}