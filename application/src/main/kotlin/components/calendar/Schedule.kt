package components.calendar

import APIclient.CourseSchedulesClient
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
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
        Text(uniclass.name)
        Text(uniclass.type)
        Text(uniclass.start.format(TimeFormatter) + " - " + uniclass.finish.format(TimeFormatter))
    }
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

        val height = hourHeight.roundToPx() * 22
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
fun render() {
    var selectedCourses by remember { mutableStateOf(emptyList<UniClass>()) }

    val userCourseScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        userCourseScope.launch{
            try {
                selectedCourses = CourseSchedulesClient.getUserCourses().map { UniClass(it.courseName,
                    it.component, Color(0xffffeb46), it.weekPattern, LocalDateTime.parse(it.startTime),
                    LocalDateTime.parse(it.endTime))
                }

            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e : Exception) {
                println("Error parsing data: ${e.message}")
            }
        }
    }

    val mondayClasses = selectedCourses.filter { it.days.contains("M") }
    val tuesdayClasses = selectedCourses.filter { it.days.contains("T") }
    val wednesdayClasses = selectedCourses.filter { it.days.contains("W") }
    val thursdayClasses = selectedCourses.filter { it.days.contains("R") }
    val fridayClasses = selectedCourses.filter { it.days.contains("F") }
    val saturdayClasses = selectedCourses.filter { it.days.contains("Sa") }
    val sundayClasses = selectedCourses.filter { it.days.contains("Su") }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFFE8DAEF))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("SUNDAY")
            Schedule(sundayClasses)
        }

        Column(
            modifier = Modifier
                .background(Color(0xFFD4E6F1))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("MONDAY")
            Schedule(mondayClasses)
        }

        Column(
            modifier = Modifier
                .background(Color(0xFFD6EAF8))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("TUESDAY")
            Schedule(tuesdayClasses)
        }

        Column(
            modifier = Modifier
                .background(Color(0xFFD1F2EB))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("WEDNESDAY")
            Schedule(wednesdayClasses)
        }

        Column(
            modifier = Modifier
                .background(Color(0xFFD0ECE7))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("THURSDAY")
            Schedule(thursdayClasses)
        }

        Column(
            modifier = Modifier
                .background(Color(0xFFD4EFDF))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("FRIDAY")
            Schedule(fridayClasses)
        }

        Column(
            modifier = Modifier
                .background(Color(0xFFD5F5E3))
                .weight(1f)
                .padding(top = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Text("SATURDAY")
            Schedule(saturdayClasses)
        }
    }
}

