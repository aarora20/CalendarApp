package com.example.plannerdemo.rest

import com.example.plannerdemo.domain.CourseSection
import com.example.plannerdemo.domain.OptimizedSchedule
import com.example.plannerdemo.domain.TimeSlot
import com.example.plannerdemo.domain.TimeTable
import com.example.plannerdemo.util.RouteResponseData
import org.optaplanner.core.api.solver.SolverManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.*

@RestController
@RequestMapping("/timeTable")
class TimeTableController {
    @Autowired
    lateinit var solverManager: SolverManager<TimeTable, UUID>

    @PostMapping("/solve")
    fun solve(
        @RequestBody schedule: OptimizedSchedule
    ): ResponseEntity<RouteResponseData<List<CourseSection>>> {
        val table = TimeTable(schedule.getTimeslots(), schedule.getCourseSections())
        val ans = solverManager.solve( UUID.randomUUID(), table)
        val answer = ans.finalBestSolution
        if (answer.score?.isFeasible() == true) {
            val def = ans.finalBestSolution.courseSectionList
            return ResponseEntity.ok().body(RouteResponseData(
                data = def, message = "optimization found"
            ))
        } else {
            return ResponseEntity.ok().body(RouteResponseData(data = emptyList(), message = "No optimization found"))
        }
    }

}