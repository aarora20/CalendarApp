package com.example.models

import kotlinx.serialization.Serializable

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