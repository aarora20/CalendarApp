package com.example.plannerdemo.domain

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore
import org.optaplanner.core.api.solver.SolverStatus

@PlanningSolution
class TimeTable {

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    lateinit var timeslotList: List<TimeSlot>

    @PlanningEntityCollectionProperty
    lateinit var courseSectionList: List<CourseSection>

    @PlanningScore
    var score: HardMediumSoftScore? = null

    // Ignored by OptaPlanner, used by the UI to display solve or stop solving button
    var solverStatus: SolverStatus? = null

    // No-arg constructor required for OptaPlanner
    constructor() {}

    constructor(timeslotList: List<TimeSlot>, courseSectionList: List<CourseSection>) {
        this.timeslotList = timeslotList
        this.courseSectionList = courseSectionList
    }

}