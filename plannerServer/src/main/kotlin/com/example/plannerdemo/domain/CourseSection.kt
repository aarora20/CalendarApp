package com.example.plannerdemo.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.domain.variable.PlanningVariable
import java.io.Serializable
import java.util.*


@PlanningEntity
class CourseSection: Serializable {
    lateinit var availableTimeslots: List<TimeSlot>

    private lateinit var courseSectionName: String
    private lateinit var courseId: String
    private lateinit var courseTitle: String



    @PlanningId // needed for forEachUniquePair
    @JsonIgnore
    private val courseSectionIdentifier = UUID.randomUUID().toString();

    @PlanningVariable(valueRangeProviderRefs = ["timeslotRange"])
    private var selectedTimeslot: TimeSlot? = null

    @get:ValueRangeProvider(id = "timeslotRange")
    @get:JsonIgnore
    val timeslotRangeForCourse: List<TimeSlot>
        get() = availableTimeslots

    constructor()

    constructor(timeslots: List<TimeSlot>) {
        this.availableTimeslots = timeslots
    }

    constructor(timeslots: List<TimeSlot>, courseSectionName: String,
        courseId: String, courseTitle: String) {
        this.availableTimeslots = timeslots
        this.courseSectionName = courseSectionName
        this.courseId = courseId
        this.courseTitle = courseTitle
    }

    // getters and setters ...
    fun getSelectedTimeslot(): TimeSlot? {
        return this.selectedTimeslot
    }

    fun getCourseSectionName(): String {
        return this.courseSectionName
    }

    fun getCourseId(): String {
        return this.courseId
    }

    fun getCourseTitle(): String {
        return this.courseTitle
    }

    override fun toString(): String {
        return "$courseId $availableTimeslots selected:$selectedTimeslot"
    }

}