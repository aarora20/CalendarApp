package components.selectedCourses

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import components.Screen
import components.calendar.render
import components.courseSearch.CourseSearchScreen
import components.landingScreen

@Immutable
sealed class SelectionScreen {
    object CourseSelection : SelectionScreen()
    object Calendar : SelectionScreen()

}


@Composable
fun CourseSelectionScreen(onBackClick: () -> Unit) {
    var text by remember { mutableStateOf("") }
    var currentScreen by remember { mutableStateOf<SelectionScreen>(SelectionScreen.CourseSelection) }
    // Content for Course Selection screen
    Column {
        Button(onClick = onBackClick) {
            Text("Back")
        }
        Button(onClick = { currentScreen = SelectionScreen.Calendar }) {
            Text("View Calendar")
        }
        when (currentScreen) {
            is SelectionScreen.CourseSelection -> {

            }
            is SelectionScreen.Calendar -> {
                render()
            }

        }
    }
}