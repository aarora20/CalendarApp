package components.playground

import APIclient.CustomCalendarClient
import androidx.compose.runtime.*
import components.store
import kotlinx.coroutines.launch
import models.CustomCalendar
import models.UserCalendarCourse

@Composable
fun PlaygroundHome(
                   calendarList: List<CustomCalendar>,
                   goToCurrent: () -> Unit,
                   changeToCalendar: (calendar: CustomCalendar) -> Unit,
                   fetchCalendars: () -> Unit) {
    val userCalendarCourseScope = rememberCoroutineScope()
    var courseList by remember { mutableStateOf(emptyList<UserCalendarCourse>()) }

    PlaygroundCalendarsPage(
        calendarList,
        goToCurrent = goToCurrent,

        onClickCalendar = { calendar ->
            userCalendarCourseScope.launch {
                try {
                    courseList = CustomCalendarClient.getCalendarCourses(store.getState().userId, calendar.id)
                    changeToCalendar(calendar)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        },
        onRemoveCalendar = { calendar ->
            userCalendarCourseScope.launch {
                try {
                   val response = CustomCalendarClient.deleteCalendar(store.getState().userId, calendar.id)
                    if (response) {
                        fetchCalendars()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        },
        onCreateNewCalendar = {
            changeToCalendar(it)
            fetchCalendars()
        }
    )

}
