package com.example

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

const val apiToken = "C6990FF709E54275BEC5687553B94B51"  // Centralized API token

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}

fun Application.configureSerialization() {
    install(ServerContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}

fun Application.configureRouting() {
    val client = httpClient(apiToken)  // Reuse HttpClient instance

    install(Routing) {
        route("/classSchedules") {
            handle {
                val data = fetchClassSchedules(client)
                call.respond(data)
            }
        }
        route("/courses") {
            handle {
                val data = fetchCourses(client)
                call.respond(data)
            }
        }
        // Subjects
        route("/subjects") {
            handle {
                val data = fetchSubjects(client)
                call.respond(data)
            }
            route("/{code}") {
                handle {
                    val code = call.parameters["code"] ?: throw IllegalArgumentException("Missing or malformed code")
                    val data = fetchSubjectByCode(client, code)
                    call.respond(data)
                }
            }
            route("/associatedto/{organizationCode}") {
                handle {
                    val organizationCode = call.parameters["organizationCode"] ?: throw IllegalArgumentException("Missing or malformed organizationCode")
                    val data = fetchSubjectsByOrganization(client, organizationCode)
                    call.respond(data)
                }
            }
        }
        // Terms
        route("/terms") {
            handle {
                val data = fetchTerms(client)
                call.respond(data)
            }
            route("/current") {
                handle {
                    val data = fetchCurrentTerm(client)
                    call.respond(data)
                }
            }
            route("/{code}") {
                handle {
                    val code = call.parameters["code"] ?: throw IllegalArgumentException("Missing or malformed code")
                    val data = fetchTermByCode(client, code)
                    call.respond(data)
                }
            }
            route("/foracademicyear/{year}") {
                handle {
                    val year = call.parameters["year"]?.toIntOrNull() ?: throw IllegalArgumentException("Missing or malformed year")
                    val data = fetchTermsForAcademicYear(client, year)
                    call.respond(data)
                }
            }
        }
    }
}

fun httpClient(apiToken: String) = HttpClient(CIO) {
    install(ClientContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    defaultRequest {
        header("x-api-key", apiToken)
    }
}

suspend fun fetchClassSchedules(client: HttpClient): ClassSchedules {
    return client.get("https://openapi.data.uwaterloo.ca/v3/ClassSchedules").body()
}

suspend fun fetchCourses(client: HttpClient): Courses {
    return client.get("https://openapi.data.uwaterloo.ca/v3/Courses").body()
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

@Serializable
data class ClassSchedules(val schedule: String)
@Serializable
data class Courses(val course: String)
@Serializable
data class Subject(
    val code: String,
    val name: String,
    val descriptionAbbreviated: String,
    val description: String,
    val associatedAcademicOrgCode: String
)

typealias Subjects = List<Subject>
@Serializable
data class Term(
    val termCode: String,
    val name: String,
    val nameShort: String,
    val termBeginDate: String,
    val termEndDate: String,
    val sixtyPercentCompleteDate: String,
    val associatedAcademicYear: Int
)

typealias Terms = List<Term>
