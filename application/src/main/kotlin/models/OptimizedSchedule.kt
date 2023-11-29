package models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.DayOfWeek
import java.util.*

@Serializable
class OptimizedSchedule {

    private lateinit var timeslots: List<TimeSlot>
    private lateinit var courseSections: List<CourseSection>

    constructor()

    constructor(timeslots: List<TimeSlot>, courseSections: List<CourseSection>) {
        this.timeslots = timeslots
        this.courseSections = courseSections
    }

    fun getTimeslots(): List<TimeSlot> {
        return this.timeslots
    }

    fun getCourseSections(): List<CourseSection> {
        return this.courseSections
    }


    override fun toString(): String {
        return "timeslots:$timeslots courseSections:$courseSections"
    }
}



@Serializable
class TimeSlot {

    private var id = UUID.randomUUID().toString()
    private lateinit var dayOfWeek: DayOfWeek
    private lateinit var classStartTime: String
    private lateinit var classEndTime: String
    private lateinit var componentId: String
    private lateinit var classSection: String

    constructor(dayOfWeek: DayOfWeek, startTime: String, endTime: String,
                componentId: String,
                classSection: String) {
        this.dayOfWeek = dayOfWeek
        this.classStartTime = startTime
        this.classEndTime = endTime
        this.componentId = componentId
        this.classSection = classSection
    }

    constructor()

    constructor(id: String, dayOfWeek: DayOfWeek, startTime: String, endTime: String, componentId: String,
                classSection: String)
            : this(dayOfWeek, startTime, endTime, componentId, classSection) {
        this.id = id
    }

    override fun toString(): String {
        return "$dayOfWeek $classStartTime $classEndTime"
    }

    // getters and setters
    fun getDayOfWeek(): DayOfWeek {
        return this.dayOfWeek
    }

    fun getStartTime(): String {
        return this.classStartTime
    }

    fun getEndTime(): String {
        return this.classEndTime
    }
    fun getComponentId(): String {
        return this.componentId
    }

    fun getClassSection(): String {
        return this.classSection
    }

}

@Serializable
class CourseSection {
    lateinit var availableTimeslots: List<TimeSlot>

    private lateinit var courseSectionName: String
    private lateinit var courseId: String
    private lateinit var courseTitle: String

    @Transient
    private val courseSectionIdentifier = UUID.randomUUID().toString();

    private var selectedTimeslot: TimeSlot? = null

    constructor()

    constructor(timeslots: List<TimeSlot>) {
        this.availableTimeslots = timeslots
    }

    constructor(timeslots: List<TimeSlot>, courseSectionName: String) {
        this.availableTimeslots = timeslots
        this.courseSectionName = courseSectionName
    }

    constructor(timeslots: List<TimeSlot>,
                courseSectionName: String,
                courseId: String,
                courseTitle: String) {
        this.availableTimeslots = timeslots
        this.courseSectionName = courseSectionName
        this.courseId = courseId
        this.courseTitle = courseTitle
    }

    constructor(timeslots: List<TimeSlot>,
                courseSectionName: String,
                courseId: String,
                courseTitle: String,
                selectedTimeslot: TimeSlot) {
        this.availableTimeslots = timeslots
        this.courseSectionName = courseSectionName
        this.courseId = courseId
        this.courseTitle = courseTitle
        this.selectedTimeslot = selectedTimeslot
    }
    // getters and setters ...
    fun getSelectedTimeslot(): TimeSlot? {
        return this.selectedTimeslot
    }

    fun getCourseSectionName(): String {
        return this.courseSectionName
    }

    fun getCourseTitle(): String {
        return this.courseTitle
    }

    fun getCourseId(): String {
        return this.courseId
    }

    override fun toString(): String {
        return "$courseId $availableTimeslots selected:$selectedTimeslot"
    }

}


class SectionDetail(
    var courseSectionName: String,
    var courseId: String,
    var courseTitle: String
)

