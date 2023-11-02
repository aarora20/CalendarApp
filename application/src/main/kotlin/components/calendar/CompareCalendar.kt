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
import models.UserCourse
import java.time.LocalDateTime
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CalendarCompareScreen(userList: List<UserCourse>, friendList: List<UserCourse>, onBackClick: () -> Unit) {
    val userSelectedCourses =  userList.map { UniClass(it.courseNum,
        it.component, Color(0x55ffeb46), it.weekPattern, LocalDateTime.parse(it.startTime),
        LocalDateTime.parse(it.endTime))
    }

    val friendSelectedCourses = friendList.map { UniClass(it.courseNum,
        it.component, Color(0x550096FF), it.weekPattern, LocalDateTime.parse(it.startTime),
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

        var isScrollingNeeded = false
        var screenHeight = LocalWindowInfo.current.containerSize.height

        Row (
            modifier = Modifier
                .weight(0.84f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .onSizeChanged { constraints ->
                    var contentHeight = constraints.height
                    print(contentHeight)

                    // Compare contentHeight and screenHeight to determine if scrolling is needed
                    isScrollingNeeded = contentHeight > screenHeight
                }

                .drawBehind {
                    val hourHeightHalf = hourHeight / 2
                    val hourHeightHalfPx = hourHeightHalf.toPx().roundToInt().toFloat()

                    repeat(hours * 2) {
                        drawLine(
                            start = Offset(x = 77f, y = it * hourHeightHalfPx),
                            end = Offset(x = size.width, y = it * hourHeightHalfPx),
                            strokeWidth = 0.5.dp.toPx(),
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
                ScheduleSidebarPreview()
            }

            var windowHeightScreenXXX = LocalWindowInfo.current.containerSize.height
            //print(windowHeightScreenXXX)
            var newHourHeight = hourHeight
            if (windowHeightScreenXXX > 975) newHourHeight = (windowHeightScreenXXX / hours).dp

            //print(windowHeightScreenXXX.dp)
            //print(isScrollingNeeded)
            //print(" ")

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