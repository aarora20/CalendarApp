package components.courseInfo

import APIclient.CourseSchedulesClient
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
import io.ktor.client.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import models.CourseDetails
import models.ScheduleData
import models.UserCourse

@Composable
fun coursePage(
    addedCourses: Set<String>,
    onBackClick: () -> Unit,
    course: CourseDetails,
) {
    var schedules by remember { mutableStateOf(emptyList<ScheduleData>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch{
            try {
                schedules = CourseSchedulesClient.getCourseSchedule(course.courseId)
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
    // sets the page as a column
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Button(onClick = onBackClick) {
            Text("Back")
        }
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
                //modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    wishList = "Added to Wish List!"
                },
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

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    button: Int,
    header: Int,
) {
    if (button == 1) {
        var addCourseStr by remember { mutableStateOf("        + Course Schedule          ") }
        TextButton(
            onClick = {
                Modifier
                    //.fillMaxWidth()
                    .weight(weight)
                    .border(0.dp, Color.Black)
                    .padding(0.dp)
                addCourseStr = "   Added to Course Schedule!   "
            }
        ) {
            Text(addCourseStr)
        }
    } else if (header == 1) {
        Text(
            text = text,
            Modifier
                //.border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    } else {
        Text(
            text = text,
            Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    button: Int,
    header: Int,
    scope: CoroutineScope,
    course: CourseDetails,
    schedule: ScheduleData,
) {
    if (button == 1) {
        var addCourseStr by remember { mutableStateOf(text) }
        TextButton(
            onClick = {
                if (text == "        + Course Schedule          ") {
                    Modifier
                        //.fillMaxWidth()
                        .weight(weight)
                        .border(0.dp, Color.Black)
                        .padding(0.dp)
                    addCourseStr = "   Added to Course Schedule!   "
                    scope.launch {
                        try {
                            val toAdd = UserCourse(course.courseId,
                                course.subjectCode + " " + course.catalogNumber,
                                course.title, schedule.courseComponent + " " + schedule.classSection,
                                schedule.scheduleData?.get(0)?.classMeetingStartTime.orEmpty(),
                                schedule.scheduleData?.get(0)?.classMeetingEndTime.orEmpty(),
                                schedule.scheduleData?.get(0)?.classMeetingDayPatternCode.orEmpty())
                            CourseSchedulesClient.addUserCourse(toAdd)
                        } catch (e: ClientRequestException) {
                            println("Error fetching data: ${e.message}")
                        } catch (e: Exception) {
                            println(e.message)
                        }
                    }
                }
            }
        ) {
            Text(addCourseStr)
        }
    } else if (header == 1) {
        Text(
            text = text,
            Modifier
                //.border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
    } else {
        Text(
            text = text,
            Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp),
            textAlign = TextAlign.Center
        )
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
                TableCell(text = "Class", weight = classWeight, button = 0, header = 1)
                TableCell(text = "Section", weight = sectionWeight, button = 0, header = 1)
                TableCell(text = "Time", weight = timeWeight, button = 0, header = 1)
                TableCell(text = "Days", weight = dateWeight, button = 0, header = 1)
                TableCell(text = "Add to Course Schedule", weight = buttonWeight, button = 0, header = 1)
            }
        }
        // Here are all the lines of your table.
        items(schedules) {
            val classNum  = it.classNumber
            val courseComp = it.courseComponent
            val sectionNum = it.classSection
            val start = it.scheduleData?.get(0)?.classMeetingStartTime.orEmpty()
            val end = it.scheduleData?.get(0)?.classMeetingEndTime.orEmpty()
            val date = it.scheduleData?.get(0)?.classMeetingDayPatternCode.orEmpty()
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell(text = classNum.toString(), weight = classWeight, button = 0, header = 0)
                TableCell(text = "$courseComp $sectionNum", weight = sectionWeight, button = 0, header = 0)
                TableCell(text = "$start - $end", weight = timeWeight, button = 0, header = 0)
                TableCell(text = date, weight = dateWeight, button = 0, header = 0)
                TableCell(text = if (addedCourses.contains(
                        "${course.subjectCode} ${course.catalogNumber}$courseComp $sectionNum")) {
                    "   Added to Course Schedule!   " }
                        else { "        + Course Schedule          "},
                    weight = buttonWeight, button = 1, header = 0, scope, course, it)
            }
        }
    }
}

fun List<String>.concat() = this.joinToString("/") { it }.takeWhile { it.isDefined() }
/*
fun schedule(
    classes: UniCourse,
    sections: List<courseSection>
) {
    tableScreen(classes, sections)
    /*
    Row (
        Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Section",
            fontSize = 15.sp,
            //style = MaterialTheme.typography.h6
        )
        Text(
            text = "Class",
            fontSize = 15.sp,
            //style = MaterialTheme.typography.h6
        )
        Text(
            text = "Time",
            fontSize = 15.sp,
            //style = MaterialTheme.typography.h6
        )
        Text(
            text = "Date",
            fontSize = 15.sp,
            //style = MaterialTheme.typography.h6
        )
        Text(
            text = "",
            fontSize = 15.sp,
            //style = MaterialTheme.typography.h6
        )
    }
    var added = 0
    for (section in sections) {
        Row (
            Modifier.fillMaxWidth().padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.courseComponent + " " + section.classSection,
                fontSize = 15.sp,
                //style = MaterialTheme.typography.h6
            )
            Text(
                text = section.classNumber,
                fontSize = 15.sp,
                //style = MaterialTheme.typography.h6
            )
            Text(
                text = section.classMeetingStartTime + " - " + section.classMeetingEndTime,
                fontSize = 15.sp,
                //style = MaterialTheme.typography.h6
            )
            Text(
                text = section.classNumber,
                fontSize = 15.sp,
                //style = MaterialTheme.typography.h6
            )
            var addCoursestr by remember { mutableStateOf("+ Course Schedule") }
            var courseAdded = "Course Added"
            Button(
                //modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    addCoursestr = "Added to Course Schedule!"
                    added = 1
                }
            ) {
                if (added == 0) {
                    Text(addCoursestr)
                } else {
                    Text(courseAdded)
                }
            }
        }
    }
    */
}
 */