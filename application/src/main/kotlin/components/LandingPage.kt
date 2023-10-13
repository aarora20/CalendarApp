package components

import CourseSearchScreen
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp


@Immutable
sealed class Screen {
    object Landing : Screen()
    object CourseSelection : Screen()
    object CourseSearch : Screen()
}
@Composable
fun LandingPage() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Landing) }

    when (currentScreen) {
        is Screen.Landing -> {
            LandingScreen(
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
fun LandingScreen(
    onCourseSelectionClick: () -> Unit,
    onCourseSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onCourseSelectionClick) {
            Text("Course Selection")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCourseSearchClick) {
            Text("Course Search")
        }
    }
}

@Composable
fun CourseSelectionScreen(onBackClick: () -> Unit) {
    // Content for Course Selection screen
    Column {
        Text("Course Selection Screen")
        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}




