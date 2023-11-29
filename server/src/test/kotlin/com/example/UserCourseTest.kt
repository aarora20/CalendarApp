package com.example

import com.example.models.AuthRes
import com.example.models.UserCourse
import com.example.models.UserParams
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
import kotlin.test.*

class UserCourseTest {
    val clientServer = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }


    @BeforeTest
    fun createUser(): Unit = testApplication {

    }

    @Test
    fun testAddCourse(): Unit = testApplication {
        // create a user
        val response = clientServer.post("http://0.0.0.0:8080/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                UserParams("waterlooStudent", "password")
            )
        }
        val id = response.body<ResponseData<AuthRes>>().data?.userId

        // add a course to a user for their selected courses

        val course =  UserCourse(
            courseId = "12355",
            courseNum = "CS 346",
            courseTitle = "Application Development",
            component = "LEC 001",
            startTime = "2023-10-16T10:00:00",
            endTime = "2023-10-16T11:20:00",
            weekPattern = "MW"
        )

        val addCourseResponse = clientServer.post("http://0.0.0.0:8080/users/${id}/courses") {
            contentType(ContentType.Application.Json)
            setBody(course)
        }

        assertEquals(HttpStatusCode.OK, addCourseResponse.status)
        assertNotNull(addCourseResponse.body());
        assertEquals(ResponseData(data = course, message = "Add course success"), addCourseResponse.body())

        // get back the course that was just added

        val getCourseResponse = clientServer.get("http://0.0.0.0:8080/users/${id}/courses")

        assertEquals(HttpStatusCode.OK, getCourseResponse.status)
        assertNotNull(getCourseResponse.body());
        assertEquals(listOf(course), getCourseResponse.body())


        // add the same course again

        val course2 =  UserCourse(
            courseId = "12355",
            courseNum = "CS 346",
            courseTitle = "Application Development",
            component = "LEC 001",
            startTime = "2023-10-16T10:00:00",
            endTime = "2023-10-16T11:20:00",
            weekPattern = "MW"
        )

        val addCourseAgainResponse = clientServer.post("http://0.0.0.0:8080/users/${id}/courses") {
            contentType(ContentType.Application.Json)
            setBody(course2)
        }

        assertEquals(HttpStatusCode.BadRequest, addCourseAgainResponse.status)
        assertNotNull(addCourseAgainResponse.body());
        assertEquals(ResponseData(data = null, message = "Fail to add course"), addCourseAgainResponse.body())


        // add another distinct course
        val course3 = UserCourse(
            courseId = "453636",
            courseNum = "CS 136",
            courseTitle = "Elementary Design",
            component = "TUT 102",
            startTime = "2023-10-16T18:00:00",
            endTime = "2023-10-16T20:20:00",
            weekPattern = "TR"
        )

        val addNewCourseResponse = clientServer.post("http://0.0.0.0:8080/users/${id}/courses") {
            contentType(ContentType.Application.Json)
            setBody(course3)
        }

        assertEquals(HttpStatusCode.OK, addNewCourseResponse.status)
        assertNotNull(addNewCourseResponse.body());
        assertEquals(ResponseData(data = course3, message = "Add course success"), addNewCourseResponse.body())


        // fetch all courses
        val getCoursesAgainResponse = clientServer.get("http://0.0.0.0:8080/users/${id}/courses")

        assertEquals(HttpStatusCode.OK, getCoursesAgainResponse.status)
        assertNotNull(getCoursesAgainResponse.body());
        assertEquals(listOf( course, course3), getCoursesAgainResponse.body())
    }


    @Test
    fun testUpdateAllCourses(): Unit = testApplication {
        // create a user
        val response = clientServer.post("http://0.0.0.0:8080/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                UserParams("lazyStudent", "password")
            )
        }
        val id = response.body<ResponseData<AuthRes>>().data?.userId

        // add a course to a user for their selected courses

        val course =  UserCourse(
            courseId = "12355",
            courseNum = "CS 346",
            courseTitle = "Application Development",
            component = "LEC 001",
            startTime = "2023-10-16T10:00:00",
            endTime = "2023-10-16T11:20:00",
            weekPattern = "MW"
        )

        val addCourseResponse = clientServer.post("http://0.0.0.0:8080/users/${id}/courses") {
            contentType(ContentType.Application.Json)
            setBody(course)
        }

        assertEquals(HttpStatusCode.OK, addCourseResponse.status)
        assertNotNull(addCourseResponse.body());
        assertEquals(ResponseData(data = course, message = "Add course success"), addCourseResponse.body())

        // get back the course that was just added

        val getCourseResponse = clientServer.get("http://0.0.0.0:8080/users/${id}/courses")

        assertEquals(HttpStatusCode.OK, getCourseResponse.status)
        assertNotNull(getCourseResponse.body());
        assertEquals(listOf(course), getCourseResponse.body())


        // add the same course again

        val course2 =  UserCourse(
            courseId = "12355",
            courseNum = "CS 346",
            courseTitle = "Application Development",
            component = "LEC 001",
            startTime = "2023-10-16T10:00:00",
            endTime = "2023-10-16T11:20:00",
            weekPattern = "MW"
        )

        val addCourseAgainResponse = clientServer.post("http://0.0.0.0:8080/users/${id}/courses") {
            contentType(ContentType.Application.Json)
            setBody(course2)
        }

        assertEquals(HttpStatusCode.BadRequest, addCourseAgainResponse.status)
        assertNotNull(addCourseAgainResponse.body());
        assertEquals(ResponseData(data = null, message = "Fail to add course"), addCourseAgainResponse.body())


        // add another distinct course
        val course3 = UserCourse(
            courseId = "453636",
            courseNum = "CS 136",
            courseTitle = "Elementary Design",
            component = "TUT 102",
            startTime = "2023-10-16T18:00:00",
            endTime = "2023-10-16T20:20:00",
            weekPattern = "TR"
        )

        val addNewCourseResponse = clientServer.post("http://0.0.0.0:8080/users/${id}/courses") {
            contentType(ContentType.Application.Json)
            setBody(course3)
        }

        assertEquals(HttpStatusCode.OK, addNewCourseResponse.status)
        assertNotNull(addNewCourseResponse.body());
        assertEquals(ResponseData(data = course3, message = "Add course success"), addNewCourseResponse.body())


        // fetch all courses
        val getCoursesAgainResponse = clientServer.get("http://0.0.0.0:8080/users/${id}/courses")

        assertEquals(HttpStatusCode.OK, getCoursesAgainResponse.status)
        assertNotNull(getCoursesAgainResponse.body());
        assertEquals(listOf( course, course3), getCoursesAgainResponse.body())
    }

}