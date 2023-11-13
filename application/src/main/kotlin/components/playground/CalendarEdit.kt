package components.playground

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.calendar.render
import models.UserCourse


val FakeData = listOf(
    UserCourse(
        courseId = "1",
        courseNum = "346",
        courseTitle = "Application Development",
        component = "LEC 001",
        startTime = "2023-11-10T10:30:00",
        endTime = "2023-11-10T12:20:00",
        weekPattern = "WF"
    )
)

@Composable
fun CalendarEditView() {
    var isSheetOpen by remember { mutableStateOf(false) }
    Draggable(modifier = Modifier.fillMaxSize()) {
        Row (modifier = Modifier.fillMaxSize()) {
            ScheduleTarget(isSheetOpen) { isSheetOpen = !isSheetOpen }
            if (isSheetOpen) {
                ScheduleSideSheet()
            }
        }
    }
}

@Composable
fun ScheduleSideSheet() {
    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            items(FakeData) {
                DraggableSchedule(it)
            }
        }
    }
}

@Composable
fun DraggableSchedule(courseSection: UserCourse) {
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
                ScheduleCell(text = courseSection.component, weight = 0.2f)
                ScheduleCell(text = "${courseSection.startTime} - ${courseSection.endTime}"
                    , weight = 0.5f)
                ScheduleCell(text = courseSection.weekPattern, weight = 0.2f)
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
fun ScheduleTarget(isSheetOpen: Boolean, toggleSideSheet: () -> Unit) {
    val listOfClasses = remember { mutableStateListOf<UserCourse>() }

    DropTarget<UserCourse>(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(if (isSheetOpen) { 0.7f} else {1f})
    ) {
        isInBound, course ->
        course?.let {
            if (isInBound) {
                listOfClasses.add(course)
            }

        }
        Button(
            onClick = toggleSideSheet
        ) {
            Text("Open")
        }

        render(listOfClasses)
    }
}




