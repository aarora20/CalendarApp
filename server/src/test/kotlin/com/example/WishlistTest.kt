package com.example

import com.example.models.AuthRes
import com.example.models.UserParams
import com.example.models.WishlistCourse
import com.example.util.ResponseData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class WishlistTest {
    private val customClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    @Test
    fun testWishlistFunctionality(): Unit = testApplication {
        // Register a user
        val response = customClient.post("http://0.0.0.0:8080/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                UserParams("wishlistUser", "password")
            )
        }
        val userId = response.body<ResponseData<AuthRes>>().data?.userId

        suspend fun addCourseToWishlist(userId: String?, course: WishlistCourse) {
            val addResponse = customClient.post("http://0.0.0.0:8080/users/${userId}/wishlist") {
                contentType(ContentType.Application.Json)
                setBody(course)
            }
            assertEquals(HttpStatusCode.OK, addResponse.status)
            assertNotNull(addResponse.body<WishlistCourse>())
            assertEquals(course, addResponse.body())
        }

        // Add a course to the wishlist
        val wishlistCourse = WishlistCourse("CS", "246", "OOP", "2A")
        addCourseToWishlist(userId, wishlistCourse)

        // Retrieve the wishlist
        val getResponse = customClient.get("http://0.0.0.0:8080/users/${userId}/wishlist")
        assertEquals(HttpStatusCode.OK, getResponse.status)
        assertNotNull(getResponse.body<List<WishlistCourse>>())
        assertEquals(listOf(wishlistCourse), getResponse.body())

        // Remove the course from the wishlist
        val deleteResponse = customClient.delete("http://0.0.0.0:8080/users/${userId}/wishlist/${wishlistCourse.subjectCode}/${wishlistCourse.catalogNumber}/${wishlistCourse.termYear}")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        // Verify the wishlist is now empty
        val getEmptyResponse = customClient.get("http://0.0.0.0:8080/users/${userId}/wishlist")
        assertEquals(HttpStatusCode.OK, getEmptyResponse.status)
        assertTrue(getEmptyResponse.body<List<WishlistCourse>>().isEmpty())

    }

}
