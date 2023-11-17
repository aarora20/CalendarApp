package components.selectedCourses

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
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
import components.common.CustomIconButton
import components.courseSearch.DropSearch
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.Plus
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.CourseDetails
import models.ScheduleData
import models.UserCourse
import java.time.LocalDateTime

@Immutable
sealed class SelectionScreen {
    object CourseSelection : SelectionScreen()
    object Calendar : SelectionScreen()
}

@Composable
fun selectionScreen(
    courses:  List<CourseDetails>
) {
    var currentScreen by remember { mutableStateOf<SelectionScreen>(SelectionScreen.CourseSelection) }
    val selectedCourses = remember { mutableStateListOf<UserCourse>() }
    val userCourseScope = rememberCoroutineScope()
    var isSheetOpen by remember { mutableStateOf(false) }
    val courseNames = courses.map { "${it.subjectCode}${it.catalogNumber}" }
    val courseMap = courses.associateBy { it.subjectCode + it.catalogNumber }
    var schedules by remember {  mutableStateOf(emptyList<ScheduleData>()) }

    LaunchedEffect(true) {
        userCourseScope.launch{
            try {
                val userCourses = CourseSchedulesClient.getUserCourses(store.getState().userId)
                selectedCourses.addAll(userCourses)
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e : Exception) {
                println("Error parsing data: ${e.message}")
            }
        }
    }
    Row (modifier = Modifier.fillMaxSize()) {
        Column (modifier = Modifier.fillMaxHeight().fillMaxWidth(if (isSheetOpen) { 0.7f} else {1f})) {
            when (currentScreen) {
                is SelectionScreen.CourseSelection -> {
                    courseSelection(selectedCourses, { currentScreen = SelectionScreen.Calendar}, { course ->
                        selectedCourses.remove(course)
                    }, {
                        isSheetOpen = !isSheetOpen
                    })
                }
                is SelectionScreen.Calendar -> {
                    CalendarContainer(selectedCourses, { currentScreen = SelectionScreen.CourseSelection }) {
                        isSheetOpen = !isSheetOpen
                    }
                }
            }
        }
        if (isSheetOpen) {
            AddCourseSideSheet(courseNames, courseMap, schedules, { schedules = it }) {
                selectedCourses.add(it)
            }
        }
    }
}

@Composable
fun AddCourseSideSheet(courseNames: List<String>,
                       courseMap:  Map<String, CourseDetails>,
                       schedules: List<ScheduleData>,
                       setSchedules: (scheduleData: List<ScheduleData>) -> Unit,
                       addToSchedule: (UserCourse) -> Unit
                       ) {

    val addScope = rememberCoroutineScope()
    var selectedCourse by remember { mutableStateOf("") }
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
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp).fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    DropSearch(courseNames, onClickCourse = {
                        addScope.launch {
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
                                    selectedCourse = it
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    })
                }
            }
            Box(modifier = Modifier.padding(top = 80.dp, bottom = 4.dp).padding(horizontal = 8.dp).fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(schedules) {
                        AddableScheduleItem(it) {
                            addScope.launch {
                                try {
                                    val toAdd = courseMap[selectedCourse]
                                    if (toAdd != null) {
                                        val response = CourseSchedulesClient.addUserCourse(UserCourse(toAdd.courseId,
                                            toAdd.subjectCode + " " + toAdd.catalogNumber,
                                            toAdd.title, it.courseComponent + " " + it.classSection,
                                            it.scheduleData?.get(0)?.classMeetingStartTime.orEmpty(),
                                            it.scheduleData?.get(0)?.classMeetingEndTime.orEmpty(),
                                            it.scheduleData?.get(0)?.classMeetingDayPatternCode.orEmpty()),
                                            store.getState().userId,
                                        )
                                        if (response != null) {
                                            addToSchedule(response)
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
                }
            }
        }
    }
}

@Composable
fun AddableScheduleItem(schedule: ScheduleData, addCourse: (schedule: ScheduleData) -> Unit) {
    Row(
        Modifier.background(Color.LightGray).border(0.dp, Color.Black).heightIn(max=80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScheduleCell(text = schedule.courseComponent + schedule.classSection, weight = 0.2f)
        ScheduleCell(text = "${
            LocalDateTime.parse(schedule.scheduleData?.get(0)?.classMeetingStartTime.orEmpty())
            .format(components.calendar.TimeFormatter).replace(".", "").uppercase()} - " +
                LocalDateTime.parse(schedule.scheduleData?.get(0)?.classMeetingEndTime.orEmpty())
                    .format(components.calendar.TimeFormatter).replace(".", "").uppercase()
                    .format(components.calendar.TimeFormatter).replace(".", "").uppercase()
            , weight = 0.4f)
        ScheduleCell(text = schedule.scheduleData?.get(0)?.classMeetingDayPatternCode.orEmpty()
            .format(components.calendar.TimeFormatter).replace(".", "").uppercase()
            , weight = 0.2f)
        Row (
            modifier = Modifier.fillMaxSize().weight(0.2f, fill = true),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomIconButton(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    addCourse(schedule)
                },
                tooltipText = "Add To Schedule",
                buttonRadius = 20.dp,
                buttonSize = 10.dp,
                backgroundColor = Color.LightGray,
                icon = TablerIcons.Plus
            )
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
            .padding(8.dp),
        textAlign = TextAlign.Center,
        fontSize = 8.sp
    )
}