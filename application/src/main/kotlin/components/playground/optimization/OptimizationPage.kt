package components.playground.optimization

import APIclient.CourseSchedulesClient
import APIclient.CustomCalendarClient
import APIclient.OptimizationClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import components.common.CustomIconButton
import components.courseSearch.DropSearch
import components.selectedCourses.AddableScheduleItem
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Trash
import compose.icons.tablericons.X
import io.ktor.client.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import models.*
import java.time.*

fun transformScheduleDataToOptimized(courseSchedules: List<CourseSchedule>): OptimizedSchedule {
    val timeslots = mutableListOf<TimeSlot>()
    val courseSections = mutableListOf<CourseSection>()

    for (courseSchedule in courseSchedules) {
        val componentMap: MutableMap<String, MutableList<TimeSlot>> = mutableMapOf()
        for (schedule in courseSchedule.schedule) {
            if (schedule.scheduleData.isNullOrEmpty()) {
                continue
            } else {
                val courseTimeslots = mutableListOf<TimeSlot>()
                val scheduleDetail = schedule.scheduleData[0]
                val component = schedule.courseComponent
                val startTime = LocalDateTime.parse(scheduleDetail.classMeetingStartTime).toLocalTime()
                val endTime = LocalDateTime.parse(scheduleDetail.classMeetingEndTime).toLocalTime()
                if (!Duration.between(startTime, endTime).isZero) {
                    if (scheduleDetail.classMeetingDayPatternCode.contains("M")) {
                        courseTimeslots.add(
                            TimeSlot(
                                DayOfWeek.MONDAY,
                                startTime.toString(),
                                endTime.toString(),
                                schedule.classNumber.toString(),
                                schedule.classSection.toString()
                        )
                        )
                    }
                    if (scheduleDetail.classMeetingDayPatternCode.contains("T")) {
                        courseTimeslots.add(
                            TimeSlot(
                                DayOfWeek.TUESDAY,
                                startTime.toString(),
                                endTime.toString(),
                                schedule.classNumber.toString(),
                                schedule.classSection.toString()
                            )
                        )
                    }
                    if (scheduleDetail.classMeetingDayPatternCode.contains("W")) {
                        courseTimeslots.add(
                            TimeSlot(
                                DayOfWeek.WEDNESDAY,
                                startTime.toString(),
                                endTime.toString(),
                                schedule.classNumber.toString(),
                                schedule.classSection.toString()
                            )
                        )
                    }
                    if (scheduleDetail.classMeetingDayPatternCode.contains("R")) {
                        courseTimeslots.add(
                            TimeSlot(
                                DayOfWeek.THURSDAY,
                                startTime.toString(),
                                endTime.toString(),
                                schedule.classNumber.toString(),
                                schedule.classSection.toString()
                            )
                        )
                    }
                    if (scheduleDetail.classMeetingDayPatternCode.contains("F")) {
                        courseTimeslots.add(
                            TimeSlot(
                                DayOfWeek.FRIDAY,
                                startTime.toString(),
                                endTime.toString(),
                                schedule.classNumber.toString(),
                                schedule.classSection.toString()
                            )
                        )
                    }
                }
                if (courseTimeslots.isNotEmpty()) {
                    for ((index, timeslot) in courseTimeslots.withIndex()) {
                        if (!componentMap.containsKey(component + index)) {
                            componentMap[component + index.toString()] = mutableListOf()
                            componentMap[component + index.toString()]?.add(timeslot)
                        } else {
                            componentMap[component + index.toString()]?.add(timeslot)
                        }
                    }
                    timeslots.addAll(courseTimeslots)
                }
            }
        }
        println(componentMap.keys)
        for (key in componentMap.keys) {
            if (key.contains("LEC")) {
                componentMap[key]?.let {
                    CourseSection(
                        it,
                        "${courseSchedule.course.subjectCode}${courseSchedule.course.catalogNumber} LEC",
                        courseSchedule.course.courseId,
                        courseSchedule.course.title,
                    )
                }?.let { courseSections.add(it) }
            } else if (key.contains("TUT")) {
                componentMap[key]?.let {
                    CourseSection(
                        it,
                        "${courseSchedule.course.subjectCode}${courseSchedule.course.catalogNumber} TUT",
                        courseSchedule.course.courseId,
                        courseSchedule.course.title,
                    )
                }?.let { courseSections.add(it) }
//            } else if (key.contains("TST")) {
//                componentMap[key]?.let {
//                    CourseSection(
//                        it,
//                        "${courseSchedule.course.subjectCode}${courseSchedule.course.catalogNumber} TST",
//                        courseSchedule.course.courseId,
//                        courseSchedule.course.title,
//                    )
//                }?.let { courseSections.add(it) }
            } else if (key.contains("LAB")) {
                componentMap[key]?.let {
                    CourseSection(
                        it,
                        "${courseSchedule.course.subjectCode}${courseSchedule.course.catalogNumber} LAB",
                        courseSchedule.course.courseId,
                        courseSchedule.course.title,
                    )
                }?.let { courseSections.add(it) }
            }
        }
    }
    return OptimizedSchedule(
        timeslots = timeslots,
        courseSections = courseSections
    )
}

fun transformCourseSectionsToCalendar(courseSections: List<CourseSection>): List<UserCalendarCourse> {
    val sectionMap: MutableMap<SectionDetail, MutableList<TimeSlot>> = mutableMapOf()
    val calendarCourses = mutableListOf<UserCalendarCourse>()
    for (courseSection in courseSections) {
        val detail = SectionDetail(
            courseSection.getCourseSectionName(),
            courseSection.getCourseId(),
            courseSection.getCourseTitle()
        )
        if (!sectionMap.containsKey(detail)) {
            sectionMap[detail] = mutableListOf()
            courseSection.getSelectedTimeslot()?.let { sectionMap[detail]?.add(it) }
        } else {
            courseSection.getSelectedTimeslot()?.let { sectionMap[detail]?.add(it) }
        }
    }
    for (key in sectionMap.keys) {
        val times = sectionMap[key]
        if (times != null) {
            var pattern = ""
            for (time in times) {
                if (time.getDayOfWeek() == DayOfWeek.MONDAY) {
                    pattern = pattern.plus("M")
                }
                if (time.getDayOfWeek() == DayOfWeek.TUESDAY) {
                    pattern = pattern.plus("T")
                }
                if (time.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                    pattern = pattern.plus("W")
                }
                if (time.getDayOfWeek() == DayOfWeek.THURSDAY) {
                    pattern = pattern.plus("R")
                }
                if (time.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    pattern = pattern.plus("F")
                }
            }
            calendarCourses.add(
                UserCalendarCourse(
                    courseId = key.courseId,
                    courseNum = key.courseSectionName.substringBefore(" "),
                    courseTitle = key.courseTitle,
                    component = key.courseSectionName.substringAfter(" ") + times[0].getClassSection(),
                    startTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(times[0].getStartTime())).toString(),
                    endTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(times[0].getEndTime())).toString(),
                    weekPattern = pattern
                )
            )
        }
    }
    return calendarCourses
}

@Composable
fun OptimizationPage(
    calendarId: String,
    courseNames: List<String>,
    courseMap:  Map<String, CourseDetails>,
    selectedCourses: SnapshotStateList<CourseSchedule>,
    updateCalendar: (id: String) -> Unit,
    goToCalendar: () -> Unit,
) {
    var isSheetOpen by remember { mutableStateOf(false) }
    Row (modifier = Modifier.fillMaxSize()) {
        Column (modifier = Modifier.fillMaxHeight().fillMaxWidth(if (isSheetOpen) { 0.7f} else {1f})) {
            OptimizationSelection(calendarId, selectedCourses, { selectedCourses.remove(it) }, goToCalendar, updateCalendar ) {
                isSheetOpen = !isSheetOpen
            }
        }
        if (isSheetOpen) {
            OptimizeCourseSideSheet(courseNames, courseMap, { isSheetOpen = false }) {
                if (selectedCourses.size < 8) {
                    if (!selectedCourses.contains(it)) {
                        selectedCourses.add(it)
                    }
                }
            }
        }
    }
}

@Composable
fun OptimizationSelection(
    calendarId: String,
    selectedCourses: List<CourseSchedule>,
    removeItem: (courseSchedule: CourseSchedule) -> Unit,
    goToCalendar: () -> Unit,
    updateCalendar: (id: String) -> Unit,
    openSideSheet: () -> Unit
) {
    val optimizeScope = rememberCoroutineScope()
    var isDialogOpen by remember {  mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Optimize Your Schedule",
                modifier = Modifier
                    .padding(8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Button(
                onClick = goToCalendar,
                modifier = Modifier.width(180.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Calendar", color = Color.White)
                }
            }

        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {
            items(selectedCourses) { it ->
                OptimizeItem(
                    it
                ) {c ->
                    removeItem(c)
                }
            }
            if (selectedCourses.size < 8) {
                item {
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .height(70.dp)
                            .clip(CardDefaults.shape)
                            .clickable {
                                openSideSheet()
                            }
                        ,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.LightGray
                        )
                    )
                    {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row (
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = TablerIcons.Plus,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    isDialogOpen = true
                },
                modifier = Modifier.width(180.dp)
            ) {
                Text("Optimize Schedule", color =Color.White)
            }
        }
    }
    if (isDialogOpen) {
        OptimizationDialog(onDismissRequest = {
            isDialogOpen = false
        }, scope = optimizeScope,
            selectedCourses = selectedCourses,
            calendarId = calendarId,
            updateCalendar = updateCalendar)
    }
}

@Composable
fun OptimizeCourseSideSheet(courseNames: List<String>,
       courseMap:  Map<String, CourseDetails>,
       closeSidesheet: () -> Unit,
       addToOptimization: (courseSchedule: CourseSchedule) -> Unit
) {
    var schedules by remember {  mutableStateOf(emptyList<ScheduleData>()) }
    val scheduleScope = rememberCoroutineScope()
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            CustomIconButton(
                modifier = Modifier.padding(8.dp),
                onClick = closeSidesheet,
                tooltipText = "Close Side Sheet",
                buttonRadius = 20.dp,
                buttonSize = 10.dp,
                backgroundColor = Color.LightGray,
                icon = TablerIcons.X
            )
        }
        Box (modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.zIndex(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp).fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    DropSearch(courseNames, onClickCourse = {
                        scheduleScope.launch {
                            try {
                                val course = courseMap[it]
                                if (course != null) {
                                    schedules = CourseSchedulesClient.getCourseSchedule(course.courseId)
                                        .sortedWith(compareBy(
                                            { it.courseComponent != "LEC" }, // First, order by whether termcode is not "LEC" (false first)
                                            { it.courseComponent != "TUT" }, // Second, order by whether termcode is not "TUT" (false first)
                                            { it.courseComponent != "TST" }, // Third, order by whether termcode is not "TST" (false first)
                                            { it.courseComponent }
                                        )).sortedBy { it.classSection }
                                    selectedCourse = it + " " + course.title
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    })
                }
            }
            Box(modifier = Modifier.padding(top = 80.dp, bottom = 4.dp).padding(horizontal = 8.dp).fillMaxSize()) {
                Column (
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (selectedCourse.isNotEmpty()) {
                        Row (
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).border(0.dp, Color.Black)
                                .background(Color.Gray).padding(horizontal = 6.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = selectedCourse, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                                textAlign = TextAlign.Center)
                        }
                        Row (
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).border(0.dp, Color.Black)
                                .background(Color.Gray).clickable {
                                    courseMap[selectedCourse.substringBefore(" ")]?.let {
                                        CourseSchedule(
                                            course = it,
                                            schedule = schedules
                                        )
                                    }?.let {
                                        addToOptimization(
                                            it
                                        )
                                    }
                            }.padding(horizontal = 4.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = TablerIcons.Plus,
                                contentDescription = null
                            )
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(schedules) {
                            AddableScheduleItem(
                                it,
                                false
                            ) {

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptimizeItem(
    courseSchedule: CourseSchedule,
    removeItem: (courseSchedule: CourseSchedule) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .height(70.dp)
            .clip(CardDefaults.shape)
        ,
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row (
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = courseSchedule.course.subjectCode + courseSchedule.course.catalogNumber +
                            " " + courseSchedule.course.title,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 8.dp).fillMaxWidth(0.7f),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(
                        0.3f).fillMaxHeight(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomIconButton(
                        onClick= {
                            removeItem(courseSchedule)
                        },
                        modifier= Modifier.padding(vertical = 5.dp).widthIn(min = 30.dp),
                        tooltipText= "Remove Calendar",
                        buttonRadius= 36.dp,
                        buttonSize= 15.dp,
                        backgroundColor = Color.LightGray,
                        icon = TablerIcons.Trash
                    )
                }
            }

        }
    }
}

@Composable
fun OptimizationDialog(
    onDismissRequest: () -> Unit,
    scope: CoroutineScope,
    selectedCourses: List<CourseSchedule>,
    calendarId: String,
    updateCalendar: (id: String) -> Unit
) {
    var isLoading by remember {  mutableStateOf(false) }
    Dialog(
        onDismissRequest = { }
    ) {
        if (!isLoading) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(275.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomIconButton(
                            onClick = onDismissRequest,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                            tooltipText = "Close Modal",
                            buttonRadius = 45.dp,
                            buttonSize = 36.dp,
                            backgroundColor = Color.Transparent,
                            icon = TablerIcons.X
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        SelectionContainer {
                            Text(
                                "Are you sure you want to optimize the current selection of courses? " +
                                        "This action will overwrite your existing courses in your schedule."
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    isLoading = true
                                    try {
                                        val op = transformScheduleDataToOptimized(selectedCourses)
                                        val ans = OptimizationClient.optimizeSchedule(op)
                                        if (!ans.isNullOrEmpty()) {
                                            val newCourses = transformCourseSectionsToCalendar(ans)
                                            CustomCalendarClient.updateCalendar(store.getState().userId,
                                                calendarId,
                                                newCourses)
                                            updateCalendar(calendarId)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    isLoading = false
                                    onDismissRequest()
                                }
                            },
                            modifier = Modifier.padding(20.dp),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colorScheme.inverseOnSurface
                            )
                        ) {
                            Text(text = "Confirm", modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(275.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Row (
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }
    }
}