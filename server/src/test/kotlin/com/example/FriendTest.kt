package com.example

import com.example.models.*
import com.example.plugins.*
import com.example.util.ResponseData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.*

class FriendTest {
    val clientServer = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun testFriendRequest(): Unit = testApplication {
        // register two new users
        val friend1Response = clientServer.post("http://0.0.0.0:8080/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                UserParams("friend1", "1234")
            )
        }

        val friendId1 = friend1Response.body<ResponseData<AuthRes>>().data?.userId.orEmpty()

        val friend2Response = clientServer.post("http://0.0.0.0:8080/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                UserParams("friend2", "1234")
            )
        }

        val friendId2 = friend2Response.body<ResponseData<AuthRes>>().data?.userId.orEmpty()


        // Friend 1 sends a request to Friend 2
        val requestParams = FriendParams(
            userId = friendId1,
            friendId = friendId2
        )

        val sendRequestResponse = clientServer.post("http://0.0.0.0:8080/users/${friendId1}/friends/send") {
            contentType(ContentType.Application.Json)
            setBody(
                requestParams
            )
        }

        val request = Friend(
            userId = friendId1,
            friendId = friendId2,
            status = "pending"
        )

        assertEquals(HttpStatusCode.OK, sendRequestResponse.status)
        assertNotNull(sendRequestResponse.body());
        assertEquals(ResponseData(data = request, message="Success sending request"), sendRequestResponse.body())


        // get request that Friend 1 sent
        val sentRequestsResponse = clientServer.get("http://0.0.0.0:8080/users/${friendId1}/friends/requests/sent")

        assertEquals(HttpStatusCode.OK, sentRequestsResponse.status)
        assertNotNull(sentRequestsResponse.body());
        val sentList = sentRequestsResponse.body<List<User>>()
        assertEquals(1, sentList.size)
        assertEquals("friend2", sentList[0].username)



        // get Friend 2's incoming requests

        val incomingRequestsResponse = clientServer.get("http://0.0.0.0:8080/users/${friendId2}/friends/requests/incoming")

        assertEquals(HttpStatusCode.OK, incomingRequestsResponse.status)
        assertNotNull(incomingRequestsResponse.body());
        val incomingList = incomingRequestsResponse.body<List<User>>()
        assertEquals(1, incomingList.size)
        assertEquals("friend1", incomingList[0].username)

        // reject Friend 1's request (it's the same endpoint for withdrawing requests, but it's from Friend 1)
        val rejectRequestResponse = clientServer.post("http://0.0.0.0:8080/users/${friendId2}/friends/requests/delete") {
            contentType(ContentType.Application.Json)
            setBody(
                FriendParams(
                    userId = friendId2,
                    friendId = friendId1
                )
            )
        }

        assertEquals(HttpStatusCode.OK, rejectRequestResponse.status)
        assertNotNull(rejectRequestResponse.body());
        assertEquals(ResponseData(data = true, message = "Success deleting request"), rejectRequestResponse.body())

        // get request that Friend 1 sent
        val sentAgainRes = clientServer.get("http://0.0.0.0:8080/users/${friendId1}/friends/requests/sent")

        assertEquals(HttpStatusCode.OK, sentAgainRes.status)
        assertNotNull(sentAgainRes.body());
        val sentList2 = sentAgainRes.body<List<User>>()
        assertEquals(0, sentList2.size)


        // get Friend 2's incoming requests

        val incomingAgainRes = clientServer.get("http://0.0.0.0:8080/users/${friendId2}/friends/requests/incoming")

        assertEquals(HttpStatusCode.OK, incomingAgainRes.status)
        assertNotNull(incomingAgainRes.body());
        val incomingList2 = incomingAgainRes.body<List<User>>()
        assertEquals(0, incomingList2.size)

        // resend request
        clientServer.post("http://0.0.0.0:8080/users/${friendId1}/friends/send") {
            contentType(ContentType.Application.Json)
            setBody(
                requestParams
            )
        }

        // Accept Friend 1's request
        val acceptResponse = clientServer.post("http://0.0.0.0:8080/users/${friendId2}/friends/requests/accept") {
            contentType(ContentType.Application.Json)
            setBody(
                FriendParams(
                    userId = friendId2,
                    friendId = friendId1
                )
            )
        }
        val acceptedRequest = Friend(
            userId = friendId2,
            friendId = friendId1,
            status = "accepted")

        assertEquals(HttpStatusCode.OK, acceptResponse.status)
        assertNotNull(acceptResponse.body());
        assertEquals(ResponseData(data = acceptedRequest, message = "Success accepting request"), acceptResponse.body())

        // Get Friend 1's friend list
        val friendsResponse1 = clientServer.get("http://0.0.0.0:8080/users/${friendId1}/friends")
        assertEquals(HttpStatusCode.OK, friendsResponse1.status)
        assertNotNull(friendsResponse1.body());
        val friendList1 = friendsResponse1.body<List<User>>()
        assertEquals(1, friendList1.size)
        assertEquals("friend2", friendList1[0].username)


        // Get Friend 2's friend list
        val friendsResponse2 = clientServer.get("http://0.0.0.0:8080/users/${friendId2}/friends")
        assertEquals(HttpStatusCode.OK, friendsResponse2.status)
        assertNotNull(friendsResponse2.body());
        val friendList2 = friendsResponse2.body<List<User>>()
        assertEquals(1, friendList2.size)
        assertEquals("friend1", friendList2[0].username)


        // Unfriend
        val unfriendResponse = clientServer.post("http://0.0.0.0:8080/users/${friendId2}/friends/unfriend") {
            contentType(ContentType.Application.Json)
            setBody(
                FriendParams(
                    userId = friendId2,
                    friendId = friendId1
                )
            )
        }
        assertEquals(HttpStatusCode.OK, unfriendResponse.status)
        assertNotNull(unfriendResponse.body());
        assertEquals(ResponseData(data = true, message = "Success unfriending"),unfriendResponse.body())

        // Get Friend 1's friend list
        val noFriendResponse1 = clientServer.get("http://0.0.0.0:8080/users/${friendId1}/friends")
        assertEquals(HttpStatusCode.OK, noFriendResponse1.status)
        assertNotNull(noFriendResponse1.body());
        val noFriendList1 = noFriendResponse1.body<List<User>>()
        assertEquals(0, noFriendList1.size)

        // Get Friend 2's friend list
        val noFriendResponse2 = clientServer.get("http://0.0.0.0:8080/users/${friendId2}/friends")
        assertEquals(HttpStatusCode.OK, noFriendResponse2.status)
        assertNotNull(noFriendResponse2.body());
        val noFriendList2 = noFriendResponse2.body<List<User>>()
        assertEquals(0, noFriendList2.size)
    }

}
