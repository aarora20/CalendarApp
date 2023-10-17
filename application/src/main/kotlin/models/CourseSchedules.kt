package models

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

// For now, removing some of the details. We do not need them all for now
@Serializable
data class CourseDetails(
    val courseId: String,
    val courseOfferNumber: Int,
    val termCode: String,
    val termName: String,
//    val associatedAcademicCareer: String,
//    val associatedAcademicGroupCode: String,
//    val associatedAcademicOrgCode: String,
    val subjectCode: String,
    val catalogNumber: String,
    val title: String,
    val descriptionAbbreviated: String,
    val description: String,
//    val gradingBasis: String,
//    val courseComponentCode: String,
//    val enrollConsentCode: String,
//    val enrollConsentDescription: String,
//    val dropConsentCode: String,
//    val dropConsentDescription: String,
    val requirementsDescription: String?
)

typealias Courses = List<CourseDetails>
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