package com.example.routes

import com.example.service.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val apiToken = "C6990FF709E54275BEC5687553B94B51"  // Centralized API token
fun Route.courseSchedulesRouting() {

    val client = httpClient(apiToken)  // Reuse HttpClient instance
    // Class Schedules
    route("/classSchedules/{termCode}") {
        handle {
            val termCode = call.parameters["termCode"] ?: throw IllegalArgumentException("Missing or malformed termCode")
            val data = fetchClassSchedule(client, termCode)
            call.respond(data)
        }

        route("/{courseId}") {
            handle {
                val termCode = call.parameters["termCode"] ?: throw IllegalArgumentException("Missing or malformed termCode")
                val courseId = call.parameters["courseId"] ?: throw IllegalArgumentException("Missing or malformed courseId")

                val data = fetchClassScheduleByCourseId(client, termCode, courseId)
                call.respond(data)
            }
        }

        route("/{subject}/{catalogNumber}") {
            handle {
                val termCode = call.parameters["termCode"] ?: throw IllegalArgumentException("Missing or malformed termCode")
                val subject = call.parameters["subject"] ?: throw IllegalArgumentException("Missing or malformed subject")
                val catalogNumber = call.parameters["catalogNumber"] ?: throw IllegalArgumentException("Missing or malformed catalogNumber")

                val data = fetchClassScheduleBySubjectAndCatalogNumber(client, termCode, subject, catalogNumber)
                call.respond(data)
            }
        }
    }

    // Courses
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