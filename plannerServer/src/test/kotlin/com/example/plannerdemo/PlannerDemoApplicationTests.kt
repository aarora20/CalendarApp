package com.example.plannerdemo

import com.example.plannerdemo.domain.CourseSection
import com.example.plannerdemo.domain.OptimizedSchedule
import com.example.plannerdemo.domain.TimeSlot
import com.example.plannerdemo.util.RouteResponseData
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.DayOfWeek

@SpringBootTest
class PlannerDemoApplicationTests {

    @Test
    fun testOptimization() {
        val restTemplate = TestRestTemplate()

        val lecTime = TimeSlot( DayOfWeek.MONDAY, "08:30", "09:30", "LEC", "001")
        val lecTime2 = TimeSlot( DayOfWeek.TUESDAY, "09:30", "10:30", "LEC", "002")
        val lecTime3 = TimeSlot( DayOfWeek.TUESDAY, "09:30", "10:30", "LEC", "003")
        val courseSection1 = CourseSection(listOf(lecTime, lecTime2, lecTime3), "CS246", "1234", "OOP")

        val lecTime5 = TimeSlot( DayOfWeek.WEDNESDAY, "08:30", "09:30", "TUT", "101")
        val lecTime6 = TimeSlot( DayOfWeek.WEDNESDAY, "09:30", "10:30", "TUT", "102")
        val lecTime7 = TimeSlot( DayOfWeek.THURSDAY, "09:30", "10:30", "TUT", "103")
        val courseSection2 = CourseSection(listOf(lecTime5, lecTime6, lecTime7), "STAT 230", "4747", "Probability")

        val lecTime8 = TimeSlot( DayOfWeek.WEDNESDAY, "08:30","10:30", "LEC", "001")
        val courseSection3 = CourseSection(listOf(lecTime8), "AFM 101", "34535", "Introduction to Accounting")

        val optimizedSchedule = OptimizedSchedule(listOf(lecTime, lecTime2, lecTime3, lecTime5, lecTime6, lecTime7, lecTime8),
            listOf(courseSection1, courseSection2, courseSection3))

        val headers = HttpHeaders()
        val request = HttpEntity(optimizedSchedule, headers)
        val response: ResponseEntity<RouteResponseData<*>> = restTemplate.postForEntity("http://0.0.0.0:8081/timeTable/solve", request,
            RouteResponseData::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body!!.data)
        assertNotEquals(emptyList<CourseSection>(), response.body!!.data)
    }

}
