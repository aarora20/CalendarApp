package util

import models.*
import java.time.*

fun transformScheduleDataToOptimized(courseSchedules: List<CourseSchedule>): OptimizedSchedule {
    val timeslots = mutableListOf<TimeSlot>()
    val courseSections = mutableListOf<CourseSection>()

    for (courseSchedule in courseSchedules) {
        val componentMap: MutableMap<String, MutableList<TimeSlot>> = mutableMapOf()
        for (schedule in courseSchedule.schedule) {
            if (schedule.scheduleData.isNullOrEmpty()) {
                continue
            } else {
                val courseTimeslots = mutableListOf<TimeSlot>()
                val scheduleDetail = schedule.scheduleData[0]
                val preStartTime = scheduleDetail.classMeetingStartTime
                val preEndTime = scheduleDetail.classMeetingEndTime
                if (preStartTime.isEmpty() || preEndTime.isEmpty()) {
                    continue
                }
                val component = schedule.courseComponent
                val startTime = LocalDateTime.parse(preStartTime).toLocalTime()
                val endTime = LocalDateTime.parse(preEndTime).toLocalTime()
                if (!Duration.between(startTime, endTime).isZero) {
                    if (scheduleDetail.classMeetingDayPatternCode.contains("M")) {
                        courseTimeslots.add(
                            TimeSlot(
                                DayOfWeek.MONDAY,
                                startTime.toString(),
                                endTime.toString(),
                                schedule.classNumber.toString(),
                                schedule.classSection.toString()
                            )
                        )
                    }
                    if (scheduleDetail.classMeetingDayPatternCode.contains("T")) {
                        courseTimeslots.add(
                            TimeSlot(
                                DayOfWeek.TUESDAY,
                                startTime.toString(),
                                endTime.toString(),
                                schedule.classNumber.toString(),
                                schedule.classSection.toString()
                            )
                        )
                    }
                    if (scheduleDetail.classMeetingDayPatternCode.contains("W")) {
                        courseTimeslots.add(
                            TimeSlot(
                                DayOfWeek.WEDNESDAY,
                                startTime.toString(),
                                endTime.toString(),
                                schedule.classNumber.toString(),
                                schedule.classSection.toString()
                            )
                        )
                    }
                    if (scheduleDetail.classMeetingDayPatternCode.contains("R")) {
                        courseTimeslots.add(
                            TimeSlot(
                                DayOfWeek.THURSDAY,
                                startTime.toString(),
                                endTime.toString(),
                                schedule.classNumber.toString(),
                                schedule.classSection.toString()
                            )
                        )
                    }
                    if (scheduleDetail.classMeetingDayPatternCode.contains("F")) {
                        courseTimeslots.add(
                            TimeSlot(
                                DayOfWeek.FRIDAY,
                                startTime.toString(),
                                endTime.toString(),
                                schedule.classNumber.toString(),
                                schedule.classSection.toString()
                            )
                        )
                    }
                }
                if (courseTimeslots.isNotEmpty()) {
                    for ((index, timeslot) in courseTimeslots.withIndex()) {
                        if (!componentMap.containsKey(component + index)) {
                            componentMap[component + index.toString()] = mutableListOf()
                            componentMap[component + index.toString()]?.add(timeslot)
                        } else {
                            componentMap[component + index.toString()]?.add(timeslot)
                        }
                    }
                    timeslots.addAll(courseTimeslots)
                }
            }
        }
        for (key in componentMap.keys) {
            if (key.contains("LEC")) {
                componentMap[key]?.let {
                    CourseSection(
                        it,
                        "${courseSchedule.course.subjectCode} ${courseSchedule.course.catalogNumber} LEC",
                        courseSchedule.course.courseId,
                        courseSchedule.course.title,
                    )
                }?.let { courseSections.add(it) }
            } else if (key.contains("TUT")) {
                componentMap[key]?.let {
                    CourseSection(
                        it,
                        "${courseSchedule.course.subjectCode} ${courseSchedule.course.catalogNumber} TUT",
                        courseSchedule.course.courseId,
                        courseSchedule.course.title,
                    )
                }?.let { courseSections.add(it) }
            } else if (key.contains("TST") && courseSchedule.includeTST) {
                componentMap[key]?.let {
                    CourseSection(
                        it,
                        "${courseSchedule.course.subjectCode} ${courseSchedule.course.catalogNumber} TST",
                        courseSchedule.course.courseId,
                        courseSchedule.course.title,
                    )
                }?.let { courseSections.add(it) }
            } else if (key.contains("LAB")) {
                componentMap[key]?.let {
                    CourseSection(
                        it,
                        "${courseSchedule.course.subjectCode} ${courseSchedule.course.catalogNumber} LAB",
                        courseSchedule.course.courseId,
                        courseSchedule.course.title,
                    )
                }?.let { courseSections.add(it) }
            }
        }
    }
    return OptimizedSchedule(
        timeslots = timeslots,
        courseSections = courseSections
    )
}

fun transformCourseSectionsToCalendar(courseSections: List<CourseSection>): List<UserCalendarCourse> {
    val sectionMap: MutableMap<String, MutableList<TimeSlot>> = mutableMapOf()
    val calendarCourses = mutableListOf<UserCalendarCourse>()
    for (courseSection in courseSections) {
        val detail = "${courseSection.getCourseSectionName()},${courseSection.getCourseId()},${courseSection.getCourseTitle()}"
        if (!sectionMap.containsKey(detail)) {
            sectionMap[detail] = mutableListOf()
            courseSection.getSelectedTimeslot()?.let { sectionMap[detail]?.add(it) }
        } else {
            courseSection.getSelectedTimeslot()?.let { sectionMap[detail]?.add(it) }
        }
    }
    for (key in sectionMap.keys) {
        val times = sectionMap[key]
        if (times != null) {
            var pattern = ""
            for (time in times) {
                if (time.getDayOfWeek() == DayOfWeek.MONDAY) {
                    pattern = pattern.plus("M")
                }
                if (time.getDayOfWeek() == DayOfWeek.TUESDAY) {
                    pattern = pattern.plus("T")
                }
                if (time.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                    pattern = pattern.plus("W")
                }
                if (time.getDayOfWeek() == DayOfWeek.THURSDAY) {
                    pattern = pattern.plus("R")
                }
                if (time.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    pattern = pattern.plus("F")
                }
            }
            val detailArray = key.split(",")
            val pad = padding(times[0].getClassSection())
            val courseTypeAndNum = detailArray[0].split(" ")
            calendarCourses.add(
                UserCalendarCourse(
                    courseId = detailArray[1],
                    courseNum = courseTypeAndNum[0] + " " + courseTypeAndNum[1],
                    courseTitle = detailArray[2],
                    component = courseTypeAndNum[2] + " $pad" + times[0].getClassSection(),
                    startTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(times[0].getStartTime())).toString(),
                    endTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(times[0].getEndTime())).toString(),
                    weekPattern = pattern
                )
            )
        }
    }
    return calendarCourses
}

fun padding(
    section: String
): String {
    val pad1 = "00"
    val pad2 = "0"
    val digits = section.count()
    return when (digits) {
        1 -> {
            pad1
        }
        2 -> {
            pad2
        }
        else -> ""
    }
}