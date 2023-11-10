package components.selectedCourses

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.calendar.render
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.Calendar
import compose.icons.tablericons.List
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.UserCourse

@Immutable
sealed class SelectionScreen {
    object CourseSelection : SelectionScreen()
    object Calendar : SelectionScreen()
    object Playground : SelectionScreen()

}

@Composable
fun selectionScreen(onBackClick: () -> Unit) {
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

    Row {
        NavigationRailSelected(
            onBackClick = onBackClick,
            onSelection = { currentScreen = SelectionScreen.CourseSelection },
            onCalendar = { currentScreen = SelectionScreen.Calendar },
        )
        Column {
            when (currentScreen) {
                is SelectionScreen.CourseSelection -> {
                    courseSelection(selectedCourses)
                }
                is SelectionScreen.Calendar -> {
                    render(selectedCourses)
                }
                is SelectionScreen.Playground -> {

                }
            }
        }
    }
}

@Composable
fun NavigationRailSelected(
    onBackClick: () -> Unit,
    onSelection : () -> Unit,
    onCalendar : () -> Unit,
) {
    NavigationRail() {
        NavigationRailItem(
            selected = false,
            onClick = onBackClick,
            icon = { Icon(Icons.Default.Home, "home") }
        )
        Spacer(modifier = Modifier.height(60.dp))
        NavigationRailItem(
            selected = false,
            onClick = onSelection,
            icon = { Icon(imageVector = TablerIcons.List, "Courses") }
        )
        NavigationRailItem(
            selected = false,
            onClick = onCalendar,
            icon = { Icon(imageVector = TablerIcons.Calendar, "Calendar") }
        )
    }
}