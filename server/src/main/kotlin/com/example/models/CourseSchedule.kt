package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ScheduleData(
    val courseId: String,
    val courseOfferNumber: Int,
    val sessionCode: String,
    val classSection: Int,
    val termCode: String,
    val classNumber: Int,
    val courseComponent: String,
    val associatedClassCode: Int,
    val maxEnrollmentCapacity: Int,
    val enrolledStudents: Int,
    val enrollConsentCode: String,
    val enrollConsentDescription: String,
    val dropConsentCode: String,
    val dropConsentDescription: String,
    val scheduleData: List<ScheduleDetail>?,
    val instructorData: List<InstructorDetail>?
)

@Serializable
data class ScheduleDetail(
    val courseId: String,
    val courseOfferNumber: Int,
    val sessionCode: String,
    val classSection: Int,
    val termCode: String,
    val classMeetingNumber: Int,
    val scheduleStartDate: String,
    val scheduleEndDate: String,
    val classMeetingStartTime: String,
    val classMeetingEndTime: String,
    val classMeetingDayPatternCode: String,
    val classMeetingWeekPatternCode: String,
    val locationName: String?
)

@Serializable
data class InstructorDetail(
    val courseId: String?,
    val courseOfferNumber: Int?,
    val sessionCode: String?,
    val classSection: Int?,
    val termCode: String?,
    val instructorRoleCode: String?,
    val instructorFirstName: String?,
    val instructorLastName: String?,
    val instructorUniqueIdentifier: String?,
    val classMeetingNumber: Int?
)

typealias ClassSchedules = List<ScheduleData>
typealias ClassScheduleStrings = List<String>

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