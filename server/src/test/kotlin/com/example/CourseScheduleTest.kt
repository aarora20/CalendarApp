package com.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals


class CourseScheduleTest {
    private val clientServer = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    @Test
    fun testCourseScheduleRoutes(): Unit = testApplication {
        // Mock data
        val testTermCode = "1239"
        val testTermCode1 = "1235"
        val testTermCode2 = "1225"
        val testTermCode3 = "1229"
        val testCourseId = "006872"
        val testCourseId2 = "006915"
        val testSubject = "cs"
        val testSubject2 = "math"
        val testCatalogNumber = "346"
        val testOfferNumber = "1"
        val testCode = "sci"
        val testOrganizationCode = "msci"
        val testYear = 2022

        suspend fun testClassSchedules(termCode: String) {
            val response = clientServer.get("http://0.0.0.0:8080/classSchedules/$termCode")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testClassScheduleByCourseId(termCode: String, courseId: String) {
            val response = clientServer.get("http://0.0.0.0:8080/classSchedules/$termCode/$courseId")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testClassScheduleBySubjectAndCatalogNumber(termCode: String, subject: String, catalogNumber: String) {
            val response = clientServer.get("http://0.0.0.0:8080/classSchedules/$termCode/$subject/$catalogNumber")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testCoursesByTermCode(termCode: String) {
            val response = clientServer.get("http://0.0.0.0:8080/courses/$termCode")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testCourseByTermAndId(termCode: String, courseId: String) {
            val response = clientServer.get("http://0.0.0.0:8080/courses/$termCode/$courseId")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testCourseOfferNumber(termCode: String, courseId: String, offerNumber: String) {
            val response = clientServer.get("http://0.0.0.0:8080/courses/$termCode/$courseId/$offerNumber")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testCoursesBySubject(termCode: String, subject: String) {
            val response = clientServer.get("http://0.0.0.0:8080/courses/$termCode/$subject")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testSubjects() {
            val response = clientServer.get("http://0.0.0.0:8080/subjects")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testSubjectByCode(code: String) {
            val response = clientServer.get("http://0.0.0.0:8080/subjects/$code")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testSubjectsByOrganization(organizationCode: String) {
            val response = clientServer.get("http://0.0.0.0:8080/subjects/associatedto/$organizationCode")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testTerms() {
            val response = clientServer.get("http://0.0.0.0:8080/terms")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testCurrentTerm() {
            val response = clientServer.get("http://0.0.0.0:8080/terms/current")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testTermByCode(code: String) {
            val response = clientServer.get("http://0.0.0.0:8080/terms/$code")
            assertEquals(HttpStatusCode.OK, response.status)
        }

         suspend fun testTermsForAcademicYear(year: Int) {
            val response = clientServer.get("http://0.0.0.0:8080/terms/foracademicyear/$year")
            assertEquals(HttpStatusCode.OK, response.status)
        }

        // Class Schedules
        testClassSchedules(testTermCode)
        testClassScheduleByCourseId(testTermCode, testCourseId)
        testClassScheduleBySubjectAndCatalogNumber(testTermCode, testSubject, testCatalogNumber)

        // Courses
        testCoursesByTermCode(testTermCode2)
        testCourseByTermAndId(testTermCode1, testCourseId2)
        testCourseOfferNumber(testTermCode3, testCourseId2, testOfferNumber)
        testCoursesBySubject(testTermCode3, testSubject2)

        // Subjects
        testSubjects()
        testSubjectByCode(testCode)
        testSubjectsByOrganization(testOrganizationCode)

        // Terms
        testTerms()
        testCurrentTerm()
        testTermByCode(testTermCode)
        testTermsForAcademicYear(testYear)
    }
}
