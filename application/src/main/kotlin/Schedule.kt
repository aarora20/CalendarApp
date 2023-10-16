import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.desktop.ui.tooling.*
import androidx.compose.material.Button
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.window.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.*
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt
import androidx.compose.ui.unit.sp


data class UniClass(

    // STAT333
    val name :String,

    // LEC, TUT, TST
    val type :String,

    // color
    val color :Color,

    // 001, 002 (from LEC 001)
    val typeNum :String,

    // days (M, T, W, Th, ...)
    val days :List<String>,

    // start time
    // "1:30:00"
    val start :LocalDateTime,

    // finish time
    val finish :LocalDateTime
)

private val fakeClasses = listOf(
    UniClass(
        name = "STAT 333",
        type = "LEC",
        typeNum = "001",
        days = listOf("M", "W", "F"),
        start = LocalDateTime.parse("2002-10-18T13:30:00"),
        finish = LocalDateTime.parse("2002-10-18T14:20:00"),
        color = Color(0xffffeb46)
    ),

    UniClass(
        name = "STAT 333",
        type = "TUT",
        typeNum = "001",
        days = listOf("F"),
        start = LocalDateTime.parse("2002-10-18T15:00:00"),
        finish = LocalDateTime.parse("2002-10-18T15:50:00"),
        color = Color(0xffffeb46)
    ),

    UniClass(
        name = "CS 346",
        type = "LEC",
        typeNum = "001",
        days = listOf("Tu", "Th"),
        start = LocalDateTime.parse("2002-10-18T10:30:00"),
        finish = LocalDateTime.parse("2002-10-18T12:20:00"),
        color = Color(0xFF9CCC65)
    ),

    UniClass(
        name = "AFM 425",
        type = "LEC",
        typeNum = "002",
        days = listOf("M", "W", "F"),
        start = LocalDateTime.parse("2002-10-18T07:00:00"),
        finish = LocalDateTime.parse("2002-10-18T08:20:00"),
        color = Color(0xFFABEBC6)
    )
)

val mondayClasses = fakeClasses.filter { it.days.contains("M") }
val tuesdayClasses = fakeClasses.filter { it.days.contains("Tu") }
val wednesdayClasses = fakeClasses.filter { it.days.contains("W") }
val thursdayClasses = fakeClasses.filter { it.days.contains("Th") }
val fridayClasses = fakeClasses.filter { it.days.contains("F") }
val saturdayClasses = fakeClasses.filter { it.days.contains("Sa") }
val sundayClasses = fakeClasses.filter { it.days.contains("Su") }

val TimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

@Composable
@Preview

fun oneClass (
    uniclass: UniClass,
    modifier: Modifier = Modifier,
) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
            .background(uniclass.color, shape = RoundedCornerShape(4.dp))
            .padding(4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(uniclass.name,
            fontSize = 12.sp)
        Text(uniclass.type + " " + uniclass.typeNum,
            fontSize = 12.sp)
        Text(uniclass.start.format(TimeFormatter) + " - " + uniclass.finish.format(TimeFormatter),
            fontSize = 12.sp)
    }
}

private val HourFormatter = DateTimeFormatter.ofPattern("h a")

@Composable
fun BasicSidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time.format(HourFormatter),
        fontSize = 12.sp,
        modifier = modifier
            .fillMaxHeight()
            .padding(4.dp)
    )
}

@Preview
@Composable
fun BasicSidebarLabelPreview() {
    BasicSidebarLabel(time = LocalTime.parse("07:00:00"), Modifier.sizeIn(maxHeight = 80.dp))
}

@Composable
fun ScheduleSidebar(
    hourHeight: Dp,
    modifier: Modifier = Modifier,
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
) {
    Column(modifier = modifier) {
        val startTime = LocalTime.parse("06:00:00")
        repeat(15) { i ->
            Box(modifier = Modifier.height(hourHeight)) {
                label(startTime.plusHours(i.toLong()))
            }
        }
    }
}

@Preview
@Composable
fun ScheduleSidebarPreview() {
    ScheduleSidebar(hourHeight = 80.dp)
}

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


@Composable
fun Schedule(
    classes: List<UniClass>,
    modifier: Modifier = Modifier,
    uniclassContent: @Composable (uniclass: UniClass) -> Unit = { oneClass(uniclass = it) },
) {
    val hourHeight = 80.dp
    val earliestHour = 7
    val latestHour = 22
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

        val height = hourHeight.roundToPx() * 15
        val placeablesWithClasses = classMeasureables.map { measurable ->
            val uniclass = measurable.parentData as UniClass
            val classDurationMinutes = ChronoUnit.MINUTES.between(uniclass.start, uniclass.finish)
            val classHeight = ((classDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
            val placeable = measurable.measure(constraints.copy(minHeight = classHeight, maxHeight = classHeight))
            Pair(placeable, uniclass)
        }

        layout(constraints.maxWidth, height) {
            placeablesWithClasses.forEach { (placeable, uniclass) ->
                val eventOffsetMinutes = ChronoUnit.MINUTES.between(LocalTime.MIN, uniclass.start.toLocalTime())
                val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()

                placeable.place(0, eventY - 700)
            }
        }
    }
}


@Composable
fun rendor() {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),

    ) {

        Column (
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp)

        ) {
            Text("TIMES",
                textAlign = TextAlign.Center)
            ScheduleSidebarPreview()
        }

        Column(
            modifier = Modifier
                //.background(Color(0xFFD4E6F1))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("MONDAY")
            Schedule(mondayClasses)
        }

        Column(
            modifier = Modifier
                //.background(Color(0xFFD6EAF8))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("TUESDAY")
            Schedule(tuesdayClasses)
        }

        Column(
            modifier = Modifier
                //.background(Color(0xFFD1F2EB))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("WEDNESDAY")
            Schedule(wednesdayClasses)
        }

        Column(
            modifier = Modifier
                //.background(Color(0xFFD0ECE7))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("THURSDAY")
            Schedule(thursdayClasses)
        }

        Column(
            modifier = Modifier
                //.background(Color(0xFFD4EFDF))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("FRIDAY")
            Schedule(fridayClasses)
        }

        Column(
            modifier = Modifier
                //.background(Color(0xFFD4EFDF))
                .weight(0.9f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("SATURDAY")
            Schedule(saturdayClasses)
        }

        Column(
            modifier = Modifier
                //.background(Color(0xFFD4EFDF))
                .weight(0.9f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("SUNDAY")
            Schedule(sundayClasses)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        // oneTime(LocalTime.parse("07:00:00"))
        rendor()
    }
}

