package components

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.auth.LoginScreen
import components.courseSearch.CourseSearchScreen
import components.selectedCourses.selectionScreen
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.CourseDetails
import org.reduxkotlin.createThreadSafeStore
import store.AuthState
import store.rootReducer
import store.tokenReducer

@Immutable
sealed class Screen {
    object Login: Screen()
    object SignUp: Screen()
    object Landing : Screen()
    object CourseSelection : Screen()
    object CourseSearch : Screen()
}

val INITIAL_STATE = AuthState("", "")

val store = createThreadSafeStore(::rootReducer, INITIAL_STATE)

@Composable
fun landingPage() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
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
        is Screen.Login -> {
            LoginScreen(onSuccess = {
                currentScreen = Screen.Landing
            })
        }
        is Screen.SignUp -> {

        }
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




