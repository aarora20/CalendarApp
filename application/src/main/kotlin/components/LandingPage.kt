package components

import components.courseSearch.CourseSearchScreen
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Immutable
sealed class Screen {
    object Landing : Screen()
    object CourseSelection : Screen()
    object CourseSearch : Screen()
}

@Composable
fun landingPage() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Landing) }

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
            CourseSelectionScreen(onBackClick = {
                currentScreen = Screen.Landing
            })
        }
        is Screen.CourseSearch -> {
            CourseSearchScreen(onBackClick = {
                currentScreen = Screen.Landing
            })
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

@Composable
fun CourseSelectionScreen(onBackClick: () -> Unit) {
    var text by remember { mutableStateOf("") }
    // Content for Course Selection screen
    Column {
        Text("Course Selection Screen")
        Button(onClick = onBackClick) {
            Text("Back")
        }
        TextField( value = text,
            onValueChange = { text = it },)
    }
}




