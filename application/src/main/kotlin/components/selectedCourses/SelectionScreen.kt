package components.selectedCourses

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.calendar.CalendarRender
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
fun selectionScreen() {
    var currentScreen by remember { mutableStateOf<SelectionScreen>(SelectionScreen.CourseSelection) }

    val selectedCourses = remember { mutableStateListOf<UserCourse>() }

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
    Column {
        when (currentScreen) {
            is SelectionScreen.CourseSelection -> {
                courseSelection(selectedCourses, {}, { course ->
                    selectedCourses.remove(course)
                })
            }
            is SelectionScreen.Calendar -> {
                CalendarRender(selectedCourses)
            }
        }
    }

}
