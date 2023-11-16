package components.courseInfo

import APIclient.CourseSchedulesClient
import APIclient.CourseSchedulesClient.addToWishlist
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import components.courseSearch.DropSearch
import components.store
import io.ktor.client.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import models.CourseDetails
import models.ScheduleData
import models.UserCourse
import models.WishCourse
import java.time.LocalDateTime

@Composable
fun coursePage(
    courseNames: List<String>,
    addedCourses: Set<String>,
    onBackClick: () -> Unit,
    course: CourseDetails,
    onChangeCourse: (course: String) -> Unit
) {
    var schedules by remember { mutableStateOf(emptyList<ScheduleData>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(course) {
        scope.launch{
            schedules = try {
                CourseSchedulesClient.getCourseSchedule(course.courseId)
                    .sortedWith(compareBy(
                        { it.courseComponent != "LEC" }, // First, order by whether termcode is not "LEC" (false first)
                        { it.courseComponent != "TUT" }, // Second, order by whether termcode is not "TUT" (false first)
                        { it.courseComponent != "TST" }, // Third, order by whether termcode is not "TST" (false first)
                        { it.courseComponent }
                    )).sortedBy { it.classSection }
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
                emptyList()
            } catch (e: Exception) {
                println(e.message)
                emptyList()
            }
        }
    }
    // sets the page as a column
    Box (modifier = Modifier.fillMaxSize()) {
        Box (
            modifier = Modifier.zIndex(1f)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            ) {
                Row (modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                    Button(onClick = onBackClick) {
                        Text("Back")
                    }
                }

                Box (modifier = Modifier.weight(5f)){
                    DropSearch(courseNames) { onChangeCourse(it) }
                }
            }
        }
        Box (modifier = Modifier.padding(top = 80.dp).padding(horizontal = 16.dp).fillMaxSize()) {
            Column(
                modifier = Modifier.padding(bottom = 10.dp).fillMaxSize().fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
            ) {
                // set the header of the page letting the user know this will provide course info
                Text(
                    text = "Course Information",
                    color = Color.Black,
                    fontSize = 30.sp,
                    maxLines = 1
                )

                // subsequent row provides the course code and its name
                // also provides the user an option to add the course to their wish list
                Row (
                    Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // provides the course code and name ie CS346: Application Development
                    Text(
                        text = course.subjectCode + course.catalogNumber + ": " + course.title,
                        style = MaterialTheme.typography.h6
                    )

                    // wish list option
                    var wishList by remember { mutableStateOf("+ Wish List") }
                    Button(
                        onClick = {
                            wishList = "Added to Wish List!"
                            scope.launch {
                                val toAdd = WishCourse(course.subjectCode,course.catalogNumber,course.title)
                                val success = addToWishlist(store.getState().userId, toAdd)
                                if (!success) {
                                    println("Error adding course to wishlist.")
                                    wishList = "+ Wish List"  // Revert button text on failure
                                }
                            }
                        }
                    ) {
                        Text(wishList)
                    }
                }

                // provides the description of the course
                Row (
                    Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = course.description,
                        fontSize = 15.sp,
                    )
                }

                // provides the prereqs of the course
                Row (
                    Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    course.requirementsDescription?.let {
                        Text(
                            text = it,
                            fontSize = 15.sp,
                        )
                    }
                }

                // lets the user know that below this row is the schedule for the selected couse
                Row (
                    Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schedule for " + course.termName + ":",
                        fontSize = 15.sp,
                    )
                }
                tableScreen(course, schedules, scope, addedCourses)
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    header: Int,
) {
    if (header == 1) {
        Text(
            text = text,
            Modifier
                .weight(weight, fill = true)
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    } else {
        Text(
            text = text,
            Modifier
                .weight(weight, fill = true)
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}

fun detectTimeConflict(userCourses: List<UserCourse>, addStartTime: String, addEndTime: String): Boolean {

    val startTime = LocalDateTime.parse(addStartTime)
    val endTime = LocalDateTime.parse(addEndTime)

    var courseStartTime: LocalDateTime
    var courseEndTime: LocalDateTime

    for (course in userCourses) {
        courseStartTime = LocalDateTime.parse(course.startTime)
        courseEndTime = LocalDateTime.parse(course.endTime)

        val overlapStart = maxOf(startTime, courseStartTime)
        val overlapEnd = minOf(endTime, courseEndTime)

        if (overlapStart.isBefore(overlapEnd)) {
            return true
        }
    }
    return false
}



@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    scope: CoroutineScope,
    course: CourseDetails,
    schedule: ScheduleData,
) {
    var addCourseStr by remember { mutableStateOf(text) }
    TextButton(
        modifier = Modifier
            .weight(weight, fill = true)
            .padding(8.dp),
        onClick = {
            if (text == "+ Course Schedule") {
                addCourseStr = "Added to Course Schedule!"
                scope.launch {
                    try {
                        val toAdd = UserCourse(course.courseId,
                            course.subjectCode + " " + course.catalogNumber,
                            course.title, schedule.courseComponent + " " + schedule.classSection,
                            schedule.scheduleData?.get(0)?.classMeetingStartTime.orEmpty(),
                            schedule.scheduleData?.get(0)?.classMeetingEndTime.orEmpty(),
                            schedule.scheduleData?.get(0)?.classMeetingDayPatternCode.orEmpty())
                        val userCourses = CourseSchedulesClient.getUserCourses(store.getState().userId)
                        val isTimeConflict = detectTimeConflict(userCourses, schedule.scheduleData?.get(0)?.classMeetingStartTime.orEmpty(), schedule.scheduleData?.get(0)?.classMeetingEndTime.orEmpty())
                        println(isTimeConflict)
                        CourseSchedulesClient.addUserCourse(toAdd, store.getState().userId)
                    } catch (e: ClientRequestException) {
                        println("Error fetching data: ${e.message}")
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
            }
        }
    ) {
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(addCourseStr, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun tableScreen(
    course: CourseDetails,
    schedules: List<ScheduleData>,
    scope: CoroutineScope,
    addedCourses: Set<String>
) {
    // Each cell of a column must have the same weight.
    val sectionWeight = .15f // 15%
    val classWeight = .10f // 10%
    val timeWeight = .3f // 30%
    val dateWeight = 0.25f // 25%
    val buttonWeight = .2f // 20%
    // The LazyColumn will be our table. Notice the use of the weights below
    LazyColumn(Modifier.fillMaxSize().padding(0.dp)) {
        // Here is the header
        item {
            Row(
                Modifier.background(Color.Gray),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell(text = "Class", weight = classWeight, header = 1)
                TableCell(text = "Section", weight = sectionWeight, header = 1)
                TableCell(text = "Time", weight = timeWeight, header = 1)
                TableCell(text = "Days", weight = dateWeight, header = 1)
                TableCell(text = "Add to Course Schedule", weight = buttonWeight, header = 1)
            }
        }
        // Here are all the lines of your table.
        items(schedules) {
            val classNum  = it.classNumber
            val courseComp = it.courseComponent
            val sectionNum = it.classSection
            val start = LocalDateTime.parse(it.scheduleData?.get(0)?.classMeetingStartTime.orEmpty())
                .format(components.calendar.TimeFormatter).replace(".", "").uppercase()
            val end = LocalDateTime.parse(it.scheduleData?.get(0)?.classMeetingEndTime.orEmpty())
                .format(components.calendar.TimeFormatter).replace(".", "").uppercase()
            val date = it.scheduleData?.get(0)?.classMeetingDayPatternCode.orEmpty()
            Row(
                Modifier.fillMaxWidth().border(0.dp, Color.Black),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell(text = classNum.toString(), weight = classWeight, header = 0)
                TableCell(text = "$courseComp $sectionNum", weight = sectionWeight, header = 0)
                TableCell(text = "$start - $end", weight = timeWeight, header = 0)
                TableCell(text = date, weight = dateWeight, header = 0)
                TableCell(text = if (addedCourses.contains(
                        "${course.subjectCode} ${course.catalogNumber}$courseComp $sectionNum")) {
                    "Added to Course Schedule!" }
                        else { "+ Course Schedule"},
                    weight = buttonWeight, scope, course, it)
            }
        }
    }
}