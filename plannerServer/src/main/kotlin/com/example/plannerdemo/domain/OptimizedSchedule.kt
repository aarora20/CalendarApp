package com.example.plannerdemo.domain

import java.io.Serializable

class OptimizedSchedule: Serializable {

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