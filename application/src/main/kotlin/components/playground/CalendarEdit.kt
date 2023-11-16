package components.playground

import APIclient.CourseSchedulesClient
import APIclient.CustomCalendarClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import components.calendar.AlternateSchedule
import components.courseSearch.DropSearch
import components.store
import kotlinx.coroutines.launch
import models.CourseDetails
import models.CustomCalendar
import models.ScheduleData
import models.UserCalendarCourse

@Composable
fun CalendarEditView(courseList: List<UserCalendarCourse>, allCourses: List<CourseDetails>,
                     selectedCalendar: CustomCalendar) {
    var isSheetOpen by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf("") }
    val courseNames = allCourses.map { "${it.subjectCode}${it.catalogNumber}" }
    val courseMap = allCourses.associateBy { it.subjectCode + it.catalogNumber }
    var schedules by remember {  mutableStateOf(emptyList<ScheduleData>()) }

    Draggable(modifier = Modifier.fillMaxSize()) {
        Row (modifier = Modifier.fillMaxSize()) {
            ScheduleTarget(isSheetOpen, courseList, selectedCalendar, courseMap, selectedCourse) { isSheetOpen = !isSheetOpen }
            key(schedules) {
                if (isSheetOpen) {
                    ScheduleSideSheet(courseNames, courseMap, schedules, { schedules = it }) { selectedCourse = it }
                }
            }
        }
    }

}

@Composable
fun ScheduleSideSheet(courseNames: List<String>,
                      courseMap:  Map<String, CourseDetails>,
                      schedules: List<ScheduleData>,
                      setSchedules: (scheduleData: List<ScheduleData>) -> Unit,
                      setSelectedCourse: (courseName: String) -> Unit ) {
    val getScheduleScope = rememberCoroutineScope()
    Column (
        modifier = Modifier.fillMaxSize().drawBehind {
            val strokeWidth = 2f
            val y = size.height - strokeWidth

            drawLine(
                color = Color.Black,
                start = Offset(0f, 0f), //(0,0) at top-left point of the box
                end = Offset(0f, y),//bottom-left point of the box
                strokeWidth = strokeWidth
            )
        }
    ) {
        Box (modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.zIndex(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp).fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    DropSearch(courseNames, onClickCourse = {
                        getScheduleScope.launch {
                            try {
                                val course = courseMap[it]
                                if (course != null) {
                                    setSchedules( CourseSchedulesClient.getCourseSchedule(course.courseId)
                                        .sortedWith(compareBy(
                                            { it.courseComponent != "LEC" }, // First, order by whether termcode is not "LEC" (false first)
                                            { it.courseComponent != "TUT" }, // Second, order by whether termcode is not "TUT" (false first)
                                            { it.courseComponent != "TST" }, // Third, order by whether termcode is not "TST" (false first)
                                            { it.courseComponent }
                                        )).sortedBy { it.classSection })
                                    setSelectedCourse(it)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    })

                }
            }
            Box(modifier = Modifier.padding(top = 80.dp).padding(horizontal = 16.dp).fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(schedules) {
                        DraggableSchedule(it) }
                    }
                }
            }

        }
}

@Composable
fun DraggableSchedule(courseSection: ScheduleData) {
    DragTarget(
        modifier = Modifier,
        dataToDrop = courseSection
    ) {
        Column(
            modifier = Modifier.border(0.dp, Color.Black).widthIn(max = 300.dp)
        ) {
            Row(
                Modifier.background(Color.Gray),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScheduleCell(text = courseSection.courseComponent + courseSection.classSection, weight = 0.2f)
                ScheduleCell(text = "${courseSection.scheduleData?.get(0)?.classMeetingStartTime.orEmpty()} - " +
                        courseSection.scheduleData?.get(0)?.classMeetingEndTime.orEmpty()
                    , weight = 0.5f)
                ScheduleCell(text = courseSection.scheduleData?.get(0)?.classMeetingDayPatternCode.orEmpty(), weight = 0.2f)
            }
        }
    }
}

@Composable
fun RowScope.ScheduleCell(
    text: String,
    weight: Float,
) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight, fill = true)
            .padding(6.dp),
        textAlign = TextAlign.Center,
        fontSize = 8.sp
    )
}

@Composable
fun ScheduleTarget(isSheetOpen: Boolean, courseList: List<UserCalendarCourse>,
                   selectedCalendar: CustomCalendar, courseMap: Map<String, CourseDetails>,
                   selectedCourse: String,
                   toggleSideSheet: () -> Unit) {
    val listOfClasses = remember { mutableStateListOf<UserCalendarCourse>() }
    val addCourseScope = rememberCoroutineScope()
    LaunchedEffect(true) {
        listOfClasses.addAll(courseList)
    }

    DropTarget<ScheduleData>(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(if (isSheetOpen) { 0.7f} else {1f})
    ) {
        isInBound, schedule ->
        schedule?.let {
            print("incoming")
            println(schedule)
            if (isInBound) {
                addCourseScope.launch {
                    try {
                        val toAdd = courseMap[selectedCourse]
                        if (toAdd != null) {
                            val response = CustomCalendarClient.addCalendarCourse(store.getState().userId,
                                selectedCalendar.id, UserCalendarCourse(toAdd.courseId,
                                    toAdd.subjectCode + " " + toAdd.catalogNumber,
                                    toAdd.title, schedule.courseComponent + " " + schedule.classSection,
                                    schedule.scheduleData?.get(0)?.classMeetingStartTime.orEmpty(),
                                    schedule.scheduleData?.get(0)?.classMeetingEndTime.orEmpty(),
                                    schedule.scheduleData?.get(0)?.classMeetingDayPatternCode.orEmpty())
                                )
                            if (response != null) {
                                listOfClasses.add(response)
                            } else {
                                // error do nothing
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

        }
        Button(
            onClick = toggleSideSheet
        ) {
            Text("Open")
        }

        AlternateSchedule(listOfClasses)
    }
}




