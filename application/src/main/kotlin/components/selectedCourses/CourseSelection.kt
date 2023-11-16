package components.selectedCourses

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.calendar.render
import components.store
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.UserCourse

@Immutable
sealed class SelectionScreen {
    object CourseSelection : SelectionScreen()
    object Calendar : SelectionScreen()

}

@Composable
fun selectionScreen(/*onBackClick: () -> Unit*/) {
    var currentScreen by remember { mutableStateOf<SelectionScreen>(SelectionScreen.CourseSelection) }

    val selectedCourses = remember { mutableStateListOf<UserCourse>()}

    val userCourseScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        userCourseScope.launch{
            try {
                val courses = CourseSchedulesClient.getUserCourses(store.getState().userId)
                selectedCourses.addAll(courses)
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e : Exception) {
                println("Error parsing data: ${e.message}")
            }
        }
    }
    // Content for Course Selection screen
    Column {
        when (currentScreen) {
            is SelectionScreen.CourseSelection -> {
                courseSelection(selectedCourses /*, onBackClick*/) {
                    currentScreen = SelectionScreen.Calendar
                }
            }
            is SelectionScreen.Calendar -> {
                render(selectedCourses)

            }
        }
    }
}

@Composable
fun courseSelection(
    courseList: SnapshotStateList<UserCourse>,
    //onBackClick: () -> Unit,
    onCalendarClick: () -> Unit,
) {

    val updateScope = rememberCoroutineScope()
    // sets the page as a column
    //Button(onClick = onBackClick) {
        //Text("Back")
    //}
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {

        // set the header of the page letting the user know this will provide course selection options
        Row(
            Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Course Selection",
                color = Color.Black,
                fontSize = 30.sp,
                maxLines = 1
            )
            Button(onClick = onCalendarClick) {
                Text("Calendar View")
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "List of Selected Courses"
            )
            Button(
                onClick = {
                    updateScope.launch {
                        try {
                            CourseSchedulesClient.updateSchedule(courseList.toList(), store.getState().userId)
                        }catch (e: ClientRequestException) {
                            println("Error fetching data: ${e.message}")
                        }
                    }
                },
            ) {
                Text("Update Calendar")
            }
        }
        Row {
            LazyColumn(Modifier.padding(0.dp)) {
                items(courseList) {
                    val name = "${it.courseNum} ${it.component}: ${it.courseTitle}"
                    Row(
                        Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(name)
                        Button(
                            onClick = {
                                courseList.remove(it)
                            }
                        ) {
                            Text("Remove from Schedule")
                        }
                    }
                }
            }
        }
        Row (
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    // need to go to Search function
                }
            ) {
                Text("Add Courses")
            }
        }
    }
}
