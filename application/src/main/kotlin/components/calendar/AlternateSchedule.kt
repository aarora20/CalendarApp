package components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.UserCalendarCourse
import models.UserCourse
import java.time.LocalDateTime
import kotlin.math.roundToInt


fun generateColorsAlternate(courseList: List<UserCalendarCourse>, theme: Theme): Map<String, Color> {
    val distinctCourseNames = courseList.map { it.courseNum }.distinct()
    val colorMap = mutableMapOf<String, Color>()

    // Define color sets for each theme

    val alpha = 0.6f
    val theme1Colors = listOf(
        Color(174, 214, 241).copy(alpha = alpha),
        Color(133, 193, 233).copy(alpha = alpha),
        Color(93, 173, 226).copy(alpha = alpha),
        Color(52, 152, 219).copy(alpha = alpha),
        Color(46, 134, 193).copy(alpha = alpha),
        Color(127, 179, 213).copy(alpha = alpha),
        Color(84, 153, 199).copy(alpha = alpha),
        Color(41, 128, 185).copy(alpha = alpha),
        Color(174, 214, 241).copy(alpha = alpha),
        Color(133, 193, 233).copy(alpha = alpha),
        Color(93, 173, 226).copy(alpha = alpha),
        Color(52, 152, 219).copy(alpha = alpha),
        Color(46, 134, 193).copy(alpha = alpha),
        Color(127, 179, 213).copy(alpha = alpha),
        Color(84, 153, 199).copy(alpha = alpha),
        Color(41, 128, 185).copy(alpha = alpha),
        Color(174, 214, 241).copy(alpha = alpha),
        Color(133, 193, 233).copy(alpha = alpha),
        Color(93, 173, 226).copy(alpha = alpha),
        Color(52, 152, 219).copy(alpha = alpha),
        Color(46, 134, 193).copy(alpha = alpha),
        Color(127, 179, 213).copy(alpha = alpha),
        Color(84, 153, 199).copy(alpha = alpha),
        Color(41, 128, 185).copy(alpha = alpha),
        Color(174, 214, 241).copy(alpha = alpha),
        Color(133, 193, 233).copy(alpha = alpha),
        Color(93, 173, 226).copy(alpha = alpha),
        Color(52, 152, 219).copy(alpha = alpha),
        Color(46, 134, 193).copy(alpha = alpha),
        Color(127, 179, 213).copy(alpha = alpha),
        Color(84, 153, 199).copy(alpha = alpha),
        Color(41, 128, 185).copy(alpha = alpha),
    )

    val theme2Colors = listOf(
        Color(255, 207, 210).copy(alpha = alpha),
        Color(241, 192, 232).copy(alpha = alpha),
        Color(207, 186, 240).copy(alpha = alpha),
        Color(163, 196, 243).copy(alpha = alpha),
        Color(144, 219, 244).copy(alpha = alpha),
        Color(142, 236, 245).copy(alpha = alpha),
        Color(152, 245, 225).copy(alpha = alpha),
        Color(185, 251, 192).copy(alpha = alpha),
        Color(251, 248, 204).copy(alpha = alpha),
        Color(253, 228, 207).copy(alpha = alpha),
        Color(255, 207, 210).copy(alpha = alpha),
        Color(241, 192, 232).copy(alpha = alpha),
        Color(207, 186, 240).copy(alpha = alpha),
        Color(163, 196, 243).copy(alpha = alpha),
        Color(144, 219, 244).copy(alpha = alpha),
        Color(142, 236, 245).copy(alpha = alpha),
        Color(152, 245, 225).copy(alpha = alpha),
        Color(185, 251, 192).copy(alpha = alpha),
        Color(251, 248, 204).copy(alpha = alpha),
        Color(253, 228, 207).copy(alpha = alpha),
        Color(152, 245, 225).copy(alpha = alpha),
        Color(185, 251, 192).copy(alpha = alpha),
        Color(251, 248, 204).copy(alpha = alpha),
        Color(253, 228, 207).copy(alpha = alpha),
        Color(255, 207, 210).copy(alpha = alpha),
        Color(241, 192, 232).copy(alpha = alpha),
        Color(207, 186, 240).copy(alpha = alpha),
        Color(163, 196, 243).copy(alpha = alpha),
        Color(144, 219, 244).copy(alpha = alpha),
        Color(142, 236, 245).copy(alpha = alpha),
        Color(152, 245, 225).copy(alpha = alpha),
        Color(185, 251, 192).copy(alpha = alpha),
        Color(251, 248, 204).copy(alpha = alpha),
        Color(253, 228, 207).copy(alpha = alpha),

        )

    val theme3Colors = listOf(
        Color(255,248,182).copy(alpha = alpha),
        Color(255,189,145).copy(alpha = alpha),
        Color(255,141,113).copy(alpha = alpha),
        Color(255,112,126).copy(alpha = alpha),
        Color(255,213,179).copy(alpha = alpha),
        Color(255,182,158).copy(alpha = alpha),
        Color(255,167,145).copy(alpha = alpha),
        Color(255,248,182).copy(alpha = alpha),
        Color(255,189,145).copy(alpha = alpha),
        Color(255,141,113).copy(alpha = alpha),
        Color(255,112,126).copy(alpha = alpha),
        Color(255,213,179).copy(alpha = alpha),
        Color(255,182,158).copy(alpha = alpha),
        Color(255,167,145).copy(alpha = alpha),
        Color(255,248,182).copy(alpha = alpha),
        Color(255,189,145).copy(alpha = alpha),
        Color(255,141,113).copy(alpha = alpha),
        Color(255,112,126).copy(alpha = alpha),
        Color(255,213,179).copy(alpha = alpha),
        Color(255,182,158).copy(alpha = alpha),
        Color(255,167,145).copy(alpha = alpha),
        Color(255,248,182).copy(alpha = alpha),
        Color(255,189,145).copy(alpha = alpha),
        Color(255,141,113).copy(alpha = alpha),
        Color(255,112,126).copy(alpha = alpha),
        Color(255,213,179).copy(alpha = alpha),
        Color(255,182,158).copy(alpha = alpha),
        Color(255,167,145).copy(alpha = alpha),
    )

    val theme4Colors = listOf(
        Color(170,214,136).copy(alpha = alpha),
        Color(152,195,119).copy(alpha = alpha),
        Color(139,189,120).copy(alpha = alpha),
        Color(94,167,88).copy(alpha = alpha),
        Color(71,137,75).copy(alpha = alpha),
        Color(200,225,204).copy(alpha = alpha),
        Color(184,216,190).copy(alpha = alpha),
        Color(224,240,227).copy(alpha = alpha),
        Color(170,214,136).copy(alpha = alpha),
        Color(152,195,119).copy(alpha = alpha),
        Color(139,189,120).copy(alpha = alpha),
        Color(94,167,88).copy(alpha = alpha),
        Color(71,137,75).copy(alpha = alpha),
        Color(200,225,204).copy(alpha = alpha),
        Color(184,216,190).copy(alpha = alpha),
        Color(224,240,227).copy(alpha = alpha),
        Color(170,214,136).copy(alpha = alpha),
        Color(152,195,119).copy(alpha = alpha),
        Color(139,189,120).copy(alpha = alpha),
        Color(94,167,88).copy(alpha = alpha),
        Color(71,137,75).copy(alpha = alpha),
        Color(200,225,204).copy(alpha = alpha),
        Color(184,216,190).copy(alpha = alpha),
        Color(224,240,227).copy(alpha = alpha),
        Color(170,214,136).copy(alpha = alpha),
        Color(152,195,119).copy(alpha = alpha),
        Color(139,189,120).copy(alpha = alpha),
        Color(94,167,88).copy(alpha = alpha),
        Color(71,137,75).copy(alpha = alpha),
        Color(200,225,204).copy(alpha = alpha),
        Color(184,216,190).copy(alpha = alpha),
        Color(224,240,227).copy(alpha = alpha),
    )

    val selectedThemeColors = when (theme) {
        Theme.OCEAN -> theme1Colors
        Theme.PASTEL -> theme2Colors
        Theme.SUNSET -> theme3Colors
        Theme.EARTH -> theme4Colors
    }

    for ((index, courseName) in distinctCourseNames.withIndex()) {
        // Use colors from the selected theme in order
        val colorIndex = index % selectedThemeColors.size
        colorMap[courseName] = selectedThemeColors[colorIndex]
    }

    return colorMap
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AlternateSchedule(courseList: List<UserCalendarCourse>, selectedTheme: Theme) {

    val colorMap = generateColorsAlternate(courseList, selectedTheme)

    val selectedCourses =  courseList.map { UniClass(it.courseNum,
        it.component, colorMap[it.courseNum]!!, it.weekPattern, LocalDateTime.parse(it.startTime),
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

    var screenHeight = LocalWindowInfo.current.containerSize.height
    var hourHeight = (screenHeight / hours).dp
    if (hourHeight < 40.dp) {
        hourHeight = 40.dp
    }

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

        Row (
            modifier = Modifier
                .weight(0.84f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())


                .drawBehind {
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
                    val textSize = 10
                    Schedule(hourHeight = hourHeight, classes = dayClass, textSize)
                }
            }
        }
    }
}