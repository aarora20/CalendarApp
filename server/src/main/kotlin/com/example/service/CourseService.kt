package com.example.service

import com.example.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun httpClient(apiToken: String) = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    defaultRequest {
        header("x-api-key", apiToken)
    }
}

// Class Schedules
suspend fun fetchClassSchedule(client: HttpClient, termCode: String): ClassScheduleStrings {
    val response: HttpResponse = client.get("https://openapi.data.uwaterloo.ca/v3/ClassSchedules/$termCode")
    return response.body<ClassScheduleStrings>()
}

suspend fun fetchClassScheduleByCourseId(client: HttpClient, termCode: String, courseId: String): ClassSchedules {
    val response: HttpResponse = client.get("https://openapi.data.uwaterloo.ca/v3/ClassSchedules/$termCode/$courseId")
    return response.body<ClassSchedules>()
}

suspend fun fetchClassScheduleBySubjectAndCatalogNumber(client: HttpClient, termCode: String, subject: String, catalogNumber: String): ClassSchedules {
    val response: HttpResponse = client.get("https://openapi.data.uwaterloo.ca/v3/ClassSchedules/$termCode/$subject/$catalogNumber")
    return response.body<ClassSchedules>()
}


// Courses
suspend fun fetchCourses(client: HttpClient): Courses {
    return client.get("https://openapi.data.uwaterloo.ca/v3/Courses/1239").body()
}
// Subjects
suspend fun fetchSubjects(client: HttpClient): Subjects {
    val response: HttpResponse = client.get("https://openapi.data.uwaterloo.ca/v3/Subjects")
    return response.body<Subjects>()
}

suspend fun fetchSubjectByCode(client: HttpClient, code: String): Subject {
    val response: HttpResponse = client.get("https://openapi.data.uwaterloo.ca/v3/Subjects/$code")
    return response.body<Subject>()
}

suspend fun fetchSubjectsByOrganization(client: HttpClient, organizationCode: String): Subjects {
    val response: HttpResponse = client.get("https://openapi.data.uwaterloo.ca/v3/Subjects/associatedto/$organizationCode")
    return response.body<Subjects>()
}

// terms
suspend fun fetchTerms(client: HttpClient): Terms {
    val response: HttpResponse = client.get("https://openapi.data.uwaterloo.ca/v3/Terms")
    return response.body<Terms>()
}

suspend fun fetchCurrentTerm(client: HttpClient): Term {
    val response: HttpResponse = client.get("https://openapi.data.uwaterloo.ca/v3/Terms/current")
    return response.body<Term>()
}

suspend fun fetchTermByCode(client: HttpClient, code: String): Term {
    val response: HttpResponse = client.get("https://openapi.data.uwaterloo.ca/v3/Terms/$code")
    return response.body<Term>()
}

suspend fun fetchTermsForAcademicYear(client: HttpClient, year: Int): Terms {
    val response: HttpResponse = client.get("https://openapi.data.uwaterloo.ca/v3/Terms/foracademicyear/$year")
    return response.body<Terms>()
}