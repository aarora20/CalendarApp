package components.calendar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

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

val TimeFormatter = DateTimeFormatter.ofPattern("h:mma")
private val HourFormatter = DateTimeFormatter.ofPattern("h a")


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
var hourHeight = 60.dp
val startTime = LocalTime.parse("08:00:00")
val endTime = LocalTime.parse("22:00:00")
val hours = ChronoUnit.HOURS.between(startTime, endTime).toInt()

@Composable
@Preview

fun oneClass (
    uniclass: UniClass,
    modifier: Modifier = Modifier,
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 1.dp, end = 1.dp)
            .background(uniclass.color, shape = RoundedCornerShape(4.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(uniclass.name + " " + uniclass.type,
            fontSize = 10.sp)
        Text(uniclass.start.format(TimeFormatter).replace(".", "").uppercase() + " - " + uniclass.finish.format(TimeFormatter).replace(".", "").uppercase(),
            fontSize = 10.sp)
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
    classes: List<UniClass>,
    modifier: Modifier = Modifier,
    uniclassContent: @Composable (uniclass: UniClass) -> Unit = { oneClass(uniclass = it) },
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun render(courseList: List<UserCourse>, onBackClick: () -> Unit) {

    val selectedCourses =  courseList.map { UniClass(it.courseNum,
        it.component, Color(0xffffeb46), it.weekPattern, LocalDateTime.parse(it.startTime),
        LocalDateTime.parse(it.endTime))
    }

    val mondayClasses = selectedCourses.filter { it.days.contains("M") }
    val tuesdayClasses = selectedCourses.filter { it.days.contains("T") }
    val wednesdayClasses = selectedCourses.filter { it.days.contains("W") }
    val thursdayClasses = selectedCourses.filter { it.days.contains("R") }
    val fridayClasses = selectedCourses.filter { it.days.contains("F") }
    val saturdayClasses = selectedCourses.filter { it.days.contains("Sa") }
    val sundayClasses = selectedCourses.filter { it.days.contains("Su") }

    val classes = listOf(mondayClasses, tuesdayClasses, wednesdayClasses,
        thursdayClasses, fridayClasses, saturdayClasses, sundayClasses)

    val days = listOf("MONDAY", "TUESDAY", "WEDNESDAY",
        "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")

    // val screenSize = java.awt.Toolkit.getDefaultToolkit().screenSize
    // print(screenSize.getWidth())

    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {

        // BACK BUTTON
        Button(onClick = onBackClick,
            modifier = Modifier
                .padding(0.dp)
                .height(45.dp)) {
            Text("Back")
        }

        // TITLES
        Row (
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // TIMES space

            Column (
                modifier = Modifier
                    .width(50.dp)
                    .fillMaxSize()
            ) {
                Text("")
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
                    Text(
                        text = text,
                        style = TextStyle(color = Color.Black, fontSize = 14.sp)
                    )
                }
            }
        }

        // ACTUAL CALENDAR
        var screenHeight = LocalWindowInfo.current.containerSize.height
        var contentHeight = 800

        Row (
            modifier = Modifier
                .weight(0.84f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .onSizeChanged { constraints ->
                    contentHeight = constraints.height
                    print("contentHeight: ")
                    print(contentHeight)
                    print(" ")
                    print("screenHeight: ")
                    print(screenHeight)
                    print(" ")
                }

                .drawBehind {
                    val hourHeightHalf = hourHeight / 2
                    val hourHeightHalfPx = hourHeightHalf.toPx().roundToInt().toFloat()

                    repeat(hours * 2) {
                        drawLine(
                            start = Offset(x = 100f, y = it * hourHeightHalfPx),
                            end = Offset(x = size.width, y = it * hourHeightHalfPx),
                            strokeWidth = 0.4.dp.toPx(),
                            color = Color.LightGray
                        )
                    }
                },

        ) {

            // make hourHeight adapt to changes in screenSize
            hourHeight = (screenHeight / hours).dp
            if (hourHeight < 40.dp) {
                hourHeight = 40.dp
            }

            Column (
                modifier = Modifier
                    .width(50.dp)
            ) {
                //Text("", textAlign = TextAlign.Center)
                ScheduleSidebar(hourHeight)
            }

            for (dayClass in classes) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Schedule(dayClass)
                }
            }
        }
    }
}

