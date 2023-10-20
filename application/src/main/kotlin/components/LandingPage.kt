package components

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.courseSearch.CourseSearchScreen
import components.selectedCourses.courseSelection
import components.selectedCourses.selectionScreen
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.CourseDetails
import models.UserCourse

@Immutable
sealed class Screen {
    object Landing : Screen()
    object CourseSelection : Screen()
    object CourseSearch : Screen()
}

@Composable
fun landingPage() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Landing) }
    var courseList by remember { mutableStateOf(emptyList<CourseDetails>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch{
            try {
                courseList = CourseSchedulesClient.getCourses()
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            }
        }
    }

    when (currentScreen) {
        is Screen.Landing -> {
            landingScreen(
                onCourseSelectionClick = {
                    currentScreen = Screen.CourseSelection
                },
                onCourseSearchClick = {
                    currentScreen = Screen.CourseSearch
                }
            )
        }
        is Screen.CourseSelection -> {
            selectionScreen(onBackClick = {
                currentScreen = Screen.Landing
            })
        }
        is Screen.CourseSearch -> {
            CourseSearchScreen(onBackClick = {
                currentScreen = Screen.Landing
            },  courses = courseList)
        }
    }
}

@Composable
fun landingScreen(
    onCourseSelectionClick: () -> Unit,
    onCourseSearchClick: () -> Unit
) {

    Row (
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = onCourseSelectionClick) {
            Text("Course Selection")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = onCourseSearchClick) {
            Text("Course Search")
        }
    }
}




