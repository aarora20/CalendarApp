package components.calendar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.UserCourse
import java.security.KeyStore.TrustedCertificateEntry
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt
import java.text.SimpleDateFormat
import java.util.*
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import compose.icons.TablerIcons
import compose.icons.tablericons.CaretRight
import compose.icons.tablericons.CaretLeft

data class UniClass(

    // STAT333
    val name :String,

    // LEC, TUT, TST
    val type :String,

    // color
    val color :Color,

    val days: String,

    // start time
    // "1:30:00"
    val start :LocalDateTime,

    // finish time
    val finish :LocalDateTime
)

enum class Theme {
    THEME1, THEME2
}

val TimeFormatter = DateTimeFormatter.ofPattern("h:mma")
val HourFormatter = DateTimeFormatter.ofPattern("h a")
val DateFormatter = DateTimeFormatter.ofPattern("MMM dd")

// allows us to attach data to a composable with a modifier
// read data from a measurable within a layout
// we need to do this because we need to know the underlying
private class ClassDataModifier(
    val uniclass: UniClass,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = uniclass
}

// add customer modifier to attach data as parentData to composable
private fun Modifier.classData(uniclass: UniClass) = this.then(ClassDataModifier(uniclass))

// Global variables for the size of the calendar
val startTime = LocalTime.parse("08:00:00")
val endTime = LocalTime.parse("22:00:00")
val hours = ChronoUnit.HOURS.between(startTime, endTime).toInt()



@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun oneClass (
    uniclass: UniClass,
    textSize: Int,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 1.dp, end = 1.dp)
            .background(uniclass.color, shape = RoundedCornerShape(4.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        val density = LocalWindowInfo.current.containerSize.width / 3 / 7

        Text(uniclass.name + " " + uniclass.type,

            fontSize = textSize.sp)
        Text(uniclass.start.format(TimeFormatter).replace(".", "").uppercase() + " - " + uniclass.finish.format(TimeFormatter).replace(".", "").uppercase(),
            fontSize = textSize.sp)
    }
}

@Composable
fun BasicSidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time.format(HourFormatter).replace(".", "").uppercase(),
        fontSize = 12.sp,
        modifier = modifier
            .fillMaxHeight()
            .padding(start = 2.dp)
    )
}

@Composable
fun ScheduleSidebar(
    hoursHeigh: Dp,
    modifier: Modifier = Modifier,
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
) {
    Column(modifier = modifier) {
        repeat(hours) { i ->
            Box(modifier = Modifier.height(hoursHeigh)) {
                label(startTime.plusHours(i.toLong()))
            }
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Schedule(
    hourHeight: Dp,
    classes: List<UniClass>,
    textSize: Int,
    modifier: Modifier = Modifier,
    uniclassContent: @Composable (uniclass: UniClass) -> Unit = { oneClass(uniclass = it, textSize) },
) {

    Layout(
        content = {
            classes.sortedBy(UniClass::start).forEach { uniclass ->
                // attach the class data to the composable
                Box(modifier = Modifier.classData(uniclass)) {
                    uniclassContent(uniclass)
                }
            }
        },
        modifier = modifier
            //.verticalScroll(rememberScrollState()),

    ) { classMeasureables, constraints ->

        val height = hourHeight.roundToPx() * hours
        val placeablesWithClasses = classMeasureables.map { measurable ->
            val uniclass = measurable.parentData as UniClass
            val classDurationMinutes = ChronoUnit.MINUTES.between(uniclass.start, uniclass.finish)
            val classHeight = ((classDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(constraints.copy(minHeight = classHeight, maxHeight = classHeight))
            Pair(placeable, uniclass)
        }

        layout(constraints.maxWidth, height) {
            placeablesWithClasses.forEach { (placeable, uniclass) ->
                val eventOffsetMinutes = ChronoUnit.MINUTES.between(LocalTime.parse("08:00:00"), uniclass.start.toLocalTime())
                val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
                placeable.place(0, eventY)
            }
        }
    }
}

fun generateColors(courseList: List<UserCourse>, theme: Theme): Map<String, Color> {
    val distinctCourseNames = courseList.map { it.courseNum }.distinct()
    val colorMap = mutableMapOf<String, Color>()

    // Define color sets for each theme
    val theme1Colors = listOf(
        Color(174, 214, 241),
        Color(133, 193, 233),
        Color(93, 173, 226),
        Color(52, 152, 219),
        Color(46, 134, 193),
        Color(127, 179, 213),
        Color(84, 153, 199),
        Color(41, 128, 185),
        Color(174, 214, 241),
        Color(133, 193, 233),
        Color(93, 173, 226),
        Color(52, 152, 219),
        Color(46, 134, 193),
        Color(127, 179, 213),
        Color(84, 153, 199),
        Color(41, 128, 185),
    )

    val theme2Colors = listOf(
        Color(255, 207, 210),
        Color(241, 192, 232),
        Color(207, 186, 240),
        Color(163, 196, 243),
        Color(144, 219, 244),
        Color(142, 236, 245),
        Color(152, 245, 225),
        Color(185, 251, 192),
        Color(251, 248, 204),
        Color(253, 228, 207),
        Color(255, 207, 210),
        Color(241, 192, 232),
        Color(207, 186, 240),
        Color(163, 196, 243),
        Color(144, 219, 244),
        Color(142, 236, 245),
        Color(152, 245, 225),
        Color(185, 251, 192),
        Color(251, 248, 204),
        Color(253, 228, 207),

    )

    val selectedThemeColors = when (theme) {
        Theme.THEME1 -> theme1Colors
        Theme.THEME2 -> theme2Colors
    }

    for ((index, courseName) in distinctCourseNames.withIndex()) {
        // Use colors from the selected theme in order
        val colorIndex = index % selectedThemeColors.size
        colorMap[courseName] = selectedThemeColors[colorIndex]
    }

    return colorMap
}



@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CalendarRender(courseList: List<UserCourse>, selectedTheme: Theme) {

    val colorMap = generateColors(courseList, selectedTheme)

    val selectedCourses =  courseList.map { UniClass(it.courseNum,
        it.component, colorMap[it.courseNum]!!, it.weekPattern, LocalDateTime.parse(it.startTime),
        LocalDateTime.parse(it.endTime))
    }

    val term_start = LocalDateTime.parse("2023-09-10T00:00:00")
    val term_end = LocalDateTime.parse("2023-12-06T00:00:00")
    val today = remember { mutableStateOf(LocalDateTime.now()) }
    val dayOfWeek = today.value.dayOfWeek

    val weekDayNumberMap: MutableMap<String, Int> = mutableMapOf()
    weekDayNumberMap["MONDAY"] = 1
    weekDayNumberMap["TUESDAY"] = 2
    weekDayNumberMap["WEDNESDAY"] = 3
    weekDayNumberMap["THURSDAY"] = 4
    weekDayNumberMap["FRIDAY"] = 5
    weekDayNumberMap["SATURDAY"] = 6
    weekDayNumberMap["SUNDAY"] = 7

    val dayOfWeekNumber = weekDayNumberMap[dayOfWeek.toString()]
    val dayOfWeekNumberNonNull = dayOfWeekNumber ?: 0
    val monDate = today.value.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    val tuesDate = if (2 > dayOfWeekNumberNonNull) {
        today.value.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY))
    } else {
        today.value.with(TemporalAdjusters.previousOrSame(DayOfWeek.TUESDAY))
    }

    val wedDate = if (3 > dayOfWeekNumberNonNull) {
        today.value.with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY))
    } else {
        today.value.with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY))
    }

    val thursDate = if (4 > dayOfWeekNumberNonNull) {
        today.value.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY))
    } else {
        today.value.with(TemporalAdjusters.previousOrSame(DayOfWeek.THURSDAY))
    }

    val friDate = if (5 > dayOfWeekNumberNonNull) {
        today.value.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))
    } else {
        today.value.with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY))
    }

    val satDate = if (6 > dayOfWeekNumberNonNull) {
        today.value.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
    } else {
        today.value.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY))
    }

    val sunDate = if (7 > dayOfWeekNumberNonNull) {
        today.value.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    } else {
        today.value.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    }

    val mondayClasses = if (term_start <= monDate && monDate <= term_end) {
        selectedCourses.filter {(it.days.contains("M") && it.type.substring(0 ,3) != "TST")
                || (it.days.contains("M") && it.type.substring(0, 3) == "TST" && it.start <= monDate && it.finish <= sunDate)
        }
    } else {
        emptyList()
    }

    val tuesdayClasses = if (term_start <= tuesDate && tuesDate <= term_end) {
        selectedCourses.filter {(it.days.contains("T") && it.type.substring(0 ,3) != "TST")
                || (it.days.contains("T") && it.type.substring(0, 3) == "TST" && it.start <= monDate && it.finish <= sunDate)
        }
    } else {
        emptyList()
    }

    val wednesdayClasses = if (term_start <= wedDate && wedDate <= term_end) {
        selectedCourses.filter {(it.days.contains("W") && it.type.substring(0 ,3) != "TST")
                || (it.days.contains("W") && it.type.substring(0, 3) == "TST" && it.start <= monDate && it.finish <= sunDate)
        }
    } else {
        emptyList()
    }

    val thursdayClasses = if (term_start <= thursDate && thursDate <= term_end) {
        selectedCourses.filter {(it.days.contains("R") && it.type.substring(0 ,3) != "TST")
                || (it.days.contains("R") && it.type.substring(0, 3) == "TST" && it.start <= monDate && it.finish <= sunDate)
        }
    } else {
        emptyList()
    }

    val fridayClasses = if (term_start <= friDate && friDate <= term_end) {
        selectedCourses.filter {(it.days.contains("F") && it.type.substring(0 ,3) != "TST")
                || (it.days.contains("F") && it.type.substring(0, 3) == "TST" && it.start <= monDate && it.finish <= sunDate)
        }
    } else {
        emptyList()
    }

    val saturdayClasses = if (term_start <= satDate && satDate <= term_end) {
        selectedCourses.filter {(it.days.contains("Sa") && it.type.substring(0 ,3) != "TST")
                || (it.days.contains("Sa") && it.type.substring(0, 3) == "TST" && it.start <= monDate && it.finish <= sunDate)
        }
    } else {
        emptyList()
    }

    val sundayClasses = if (term_start <= sunDate && sunDate <= term_end) {
        selectedCourses.filter {(it.days.contains("Su") && it.type.substring(0 ,3) != "TST")
                || (it.days.contains("Su") && it.type.substring(0, 3) == "TST" && it.start <= monDate && it.finish <= sunDate)
        }
    } else {
        emptyList()
    }

    val classes = listOf(mondayClasses, tuesdayClasses, wednesdayClasses,
        thursdayClasses, fridayClasses, saturdayClasses, sundayClasses)

    val monDateString = monDate.format(DateFormatter)
    val tuesDateString = tuesDate.format(DateFormatter)
    val wedDateString = wedDate.format(DateFormatter)
    val thursDateString = thursDate.format(DateFormatter)
    val friDateString = friDate.format(DateFormatter)
    val satDateString = satDate.format(DateFormatter)
    val sunDateString = sunDate.format(DateFormatter)

    val days = listOf(("M | " + monDateString),
        ("Tu | " + tuesDateString), ("W | " + wedDateString), ("Th | " + thursDateString),
        ("F | " + friDateString), ("Sa | " + satDateString), ("Su | " + sunDateString))

    var screenHeight = LocalWindowInfo.current.containerSize.height
    var hourHeight = (screenHeight / hours).dp
    if (hourHeight < 40.dp) {
        hourHeight = 40.dp
    }

    //print("hourHeight modified \n")
    //print("hourheight: " + hourHeight + "\n")

    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {

        // TITLES
        Row (
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // TIMES space

            Row (
                modifier = Modifier
                    .width(70.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box (
                    //modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp)
                ) {
                    PlainTooltipBox(
                        tooltip = {Text("Next Week", color = Color.White)}
                    ) {
                        CompositionLocalProvider(
                            LocalMinimumInteractiveComponentEnforcement provides false
                        ) {
                            IconButton(
                                onClick = {
                                    today.value = today.value.minusDays(7)

                                },
                                modifier = Modifier
                                    .then(Modifier.size(20.dp))
                                    .statusBarsPadding()
                                    .background(
                                        color = Color.LightGray,
                                        shape = CircleShape
                                    ).tooltipAnchor(),
                                ) {
                                Icon(
                                    imageVector = (TablerIcons.CaretLeft),
                                    contentDescription = "Right",
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }

                Box (
                    //modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp)
                ) {
                    PlainTooltipBox(
                        tooltip = {Text("Previous Week", color = Color.White)}
                    ) {
                        CompositionLocalProvider(
                            LocalMinimumInteractiveComponentEnforcement provides false
                        ) {
                            IconButton(
                                onClick = {
                                    today.value = today.value.plusDays(7)

                                },
                                modifier = Modifier
                                    .then(Modifier.size(20.dp))
                                    .statusBarsPadding()
                                    .background(
                                        color = Color.LightGray,
                                        shape = CircleShape
                                    ).tooltipAnchor(),

                                ) {
                                Icon(
                                    imageVector = (TablerIcons.CaretRight),
                                    contentDescription = "Right",
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // DAYS OF THE WEEK
            for (text in days) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = text)
                        //style = TextStyle(color = Color.Black, fontSize = 12.sp)
                }
            }
        }

        // ACTUAL CALENDAR

        Row (
            modifier = Modifier
                .weight(0.84f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .drawBehind {
                    //print("DRAWBEHIND ENTERED \n")
                    //print("hourHeight in draw: " + hourHeight + "\n")
                    repeat(hours * 2) {
                        drawLine(
                            start = Offset(x = 0f, y = it * (hourHeight / 2).toPx().toFloat()),
                            end = Offset(x = size.width, y = it * (hourHeight / 2).toPx().toFloat()),
                            strokeWidth = 0.4.dp.toPx(),
                            color = Color.LightGray
                        )
                    }
                },

        ) {

            // make hourHeight adapt to changes in screenSize
            // make hourHeight adapt to changes in screenSize

            Column (
                modifier = Modifier
                    .width(70.dp)
            ) {
                //print("ScheduleSidebar run \n")
                //print("hourHeight into schedule" + hourHeight + "\n")
                ScheduleSidebar(hourHeight)
            }

            for (dayClass in classes) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val textSize = 10
                    Schedule(hourHeight = hourHeight, classes = dayClass, textSize)
                }
            }
        }
    }
}


