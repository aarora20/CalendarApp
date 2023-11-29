package components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import components.common.CustomIconButton
import compose.icons.TablerIcons
import compose.icons.tablericons.ChevronLeft
import models.UserCourse
import java.time.LocalDateTime
import kotlin.math.roundToInt


enum class CompareTheme {
    OCEAN, PASTEL, SUNSET, EARTH
}

fun generateCompareColours(theme: CompareTheme): List<Color> {

    // Define color sets for each theme
    val alpha = 0.5f
    val theme1Colors = listOf(
        Color(174, 214, 241).copy(alpha=alpha),
        Color(133, 193, 233).copy(alpha=alpha),
    )

    val theme2Colors = listOf(
        Color(255, 207, 210).copy(alpha=alpha),
        Color(241, 192, 232).copy(alpha=alpha),
    )

    val theme3Colors = listOf(
        Color(255,112,126).copy(alpha=alpha),
        Color(255,213,179).copy(alpha=alpha),
    )

    val theme4Colors = listOf(
        Color(94,167,88).copy(alpha=alpha),
        Color(71,137,75).copy(alpha=alpha),
    )

    if (theme == CompareTheme.OCEAN) {
        return theme1Colors
    } else if (theme == CompareTheme.PASTEL) {
        return theme2Colors
    } else if (theme == CompareTheme.SUNSET) {
        return theme3Colors
    } else {
        return theme4Colors
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CalendarCompareScreen(
    userList: List<UserCourse>,
    friendList: List<UserCourse>,
    onBackClick: () -> Unit,
    theme: CompareTheme) {

    val themeColours = generateCompareColours(theme)

    val userSelectedCourses =  userList.map { UniClass(it.courseNum,
        it.component, themeColours[0], it.weekPattern, LocalDateTime.parse(it.startTime),
        LocalDateTime.parse(it.endTime))
    }

    val friendSelectedCourses = friendList.map { UniClass(it.courseNum,
        it.component, themeColours[1], it.weekPattern, LocalDateTime.parse(it.startTime),
        LocalDateTime.parse(it.endTime))
    }

    val selectedCourses = userSelectedCourses.plus(friendSelectedCourses)

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

        // where button used to be

        // TITLES
        Row (
            modifier = Modifier
                .height(37.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
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