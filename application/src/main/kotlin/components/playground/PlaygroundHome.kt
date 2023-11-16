package components.playground

import APIclient.CustomCalendarClient
import androidx.compose.runtime.*
import models.UserCalendarCourse
import components.store
import kotlinx.coroutines.launch
import models.CourseDetails
import models.CustomCalendar

@Immutable
sealed class PlaygroundScreen {
    object Home: PlaygroundScreen()
    object CalendarView: PlaygroundScreen()
}

@Composable
fun PlaygroundHome(allCourses:  List<CourseDetails>, calendarList: List<CustomCalendar>) {
    var currentScreen by remember { mutableStateOf<PlaygroundScreen>(PlaygroundScreen.Home) }
    val userCalendarCourseScope = rememberCoroutineScope()
    var courseList by remember { mutableStateOf(emptyList<UserCalendarCourse>()) }
    var selectedCalendar by remember { mutableStateOf(CustomCalendar("", "")) }

    when (currentScreen) {
        is PlaygroundScreen.Home -> {
            PlaygroundCalendarsPage(
                calendarList,
                onClickCalendar = { calendar ->
                    userCalendarCourseScope.launch {
                        try {
                            courseList = CustomCalendarClient.getCalendarCourses(store.getState().userId, calendar.id)
                            selectedCalendar = calendar
                            currentScreen = PlaygroundScreen.CalendarView

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                onCreateNewCalendar = {
                    selectedCalendar = it
                    currentScreen = PlaygroundScreen.CalendarView
                }
            )
        }
        is PlaygroundScreen.CalendarView -> {
            if (selectedCalendar.id != "") {
                CalendarEditView(courseList, allCourses, selectedCalendar)
            }
        }
    }
}
