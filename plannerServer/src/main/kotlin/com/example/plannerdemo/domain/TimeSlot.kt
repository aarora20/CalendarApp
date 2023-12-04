package com.example.plannerdemo.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.optaplanner.core.api.domain.lookup.PlanningId
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.*

class TimeSlot: Serializable {
    // ************************************************************************
    // Getters and setters
    // ************************************************************************
    @PlanningId
    @JsonIgnore
    var id = UUID.randomUUID().toString()
        private set

    private lateinit var dayOfWeek: DayOfWeek

    @JsonProperty("startTime")
    private lateinit var startTime: LocalTime

    @JsonProperty("endTime")
    private lateinit var endTime: LocalTime


    @JsonProperty("classStartTime")
    fun getReadableStartTime(): String {
        return startTime.toString()
    }

    @JsonProperty("classEndTime")
    fun getReadableEndTime(): String {
        return endTime.toString()
    }


    private lateinit var componentId: String
    private lateinit var classSection: String

    // No-arg constructor required for Hibernate
    constructor()

    @JsonCreator
    constructor(dayOfWeek: DayOfWeek, @JsonProperty("classStartTime") classStartTime: String,
                @JsonProperty("classEndTime") classEndTime: String, componentId: String,
                classSection: String) {
        this.dayOfWeek = dayOfWeek
        this.startTime = LocalTime.parse(classStartTime)
        this.endTime = LocalTime.parse(classEndTime)
        this.componentId = componentId
        this.classSection = classSection
    }

    constructor(id: String, dayOfWeek: DayOfWeek, classStartTime: String, classEndTime: String,
                componentId: String, classSection: String)
            : this(dayOfWeek, classStartTime, classEndTime, componentId, classSection) {
        this.id = id
    }

    override fun toString(): String {
        return "$dayOfWeek $startTime"
    }

    // getters and setters
    fun getDayOfWeek(): DayOfWeek {
        return this.dayOfWeek
    }

    fun getStartTime(): LocalTime {
        return this.startTime
    }

    fun getEndTime(): LocalTime {
        return this.endTime
    }
    fun getComponentId(): String {
        return this.componentId
    }

    fun getClassSection(): String {
        return this.classSection
    }
}