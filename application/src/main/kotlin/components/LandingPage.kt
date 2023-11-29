package components

import APIclient.CourseSchedulesClient
import APIclient.CustomCalendarClient
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import components.auth.LoginScreen
import components.auth.RegisterScreen
import components.courseSearch.CourseSearchScreen
import components.friends.FriendsPage
import components.home.HomeScreen
import components.playground.CalendarEditView
import components.playground.PlaygroundCalendarsPage
import components.selectedCourses.selectionScreen
import components.wishlist.wishCourses
import components.wishlist.wishSelection
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.CourseDetails
import models.CustomCalendar
import models.UserCalendarCourse
import org.reduxkotlin.createThreadSafeStore
import store.AuthState
import store.LogoutUser
import store.rootReducer

// fake data for now for wishlist
// Should replace with api get results
private val listOfWishCourses = listOf(
    wishCourses(
        year = "1",
        term = "A",
        subjectCode = "MATH",
        catalogNumber = "135",
        title = "Algebra for Honours Mathematics"
    ),
    wishCourses(
        year = "1",
        term = "A",
        subjectCode = "MATH",
        catalogNumber = "137",
        title = "Calculus 1 for Honours Mathematics"
    ),
    wishCourses(
        year = "1",
        term = "A",
        subjectCode = "CS",
        catalogNumber = "135",
        title = "Designing Functional Programs"
    ),
)

@Immutable
sealed class Screen {
    object Login: Screen()
    object SignUp: Screen()
    object Landing : Screen()
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
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    when (currentScreen) {
        is Screen.Login -> {
            LoginScreen(onSuccess = {
                currentScreen = Screen.Landing
            }, onRegister = {
                currentScreen = Screen.SignUp
            })
        }
        is Screen.SignUp -> {
            RegisterScreen(
                onSuccess = {
                    currentScreen = Screen.Landing
                }, onLogin = {
                    currentScreen = Screen.Login
                }
            )
        }
        is Screen.Landing -> {
            landingScreen(
                courseList = courseList,
                onLogout = {
                    currentScreen = Screen.Login
                }
            )

        }
    }
}


@Immutable
sealed class AppScreen {
    object Home : AppScreen()
    object CourseSelection : AppScreen()
    object CourseSearch : AppScreen()
    object FriendsPage : AppScreen()
    object Wishlist : AppScreen()

    object Playground: AppScreen()
    object AlternateSchedule: AppScreen()

}


@Composable
fun landingScreen(
    courseList: List<CourseDetails>,
    onLogout: () -> Unit
) {
    var showInNav by remember { mutableStateOf<AppScreen>(AppScreen.Home) }
    val calendarScope = rememberCoroutineScope()
    var customCalendars by remember { mutableStateOf(emptyList<CustomCalendar>()) }
    var selectedCalendar by remember { mutableStateOf(CustomCalendar("", "")) }
//    var userCourses by remember { mutableStateOf(emptyList<UserCalendarCourse>()) }

    LaunchedEffect(true) {
        calendarScope.launch {
            try {
                customCalendars = CustomCalendarClient.getCalendars(store.getState().userId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    Row (
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    ) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet {

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(300.dp)

                    ) {
                        Card(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 7.dp)
                        ) {
                            NavigationDrawerItem(
                                label = {
                                    Row (
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = TablerIcons.Home, contentDescription = "Home")
                                        Text(text = "Home", modifier = Modifier.padding(start = 8.dp))
                                    }
                                        },
                                selected = showInNav == AppScreen.Home,
                                onClick = {showInNav = AppScreen.Home},
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent,
                                    selectedContainerColor = Color(0xFF6699CC)
                                ),
                                shape = CardDefaults.shape
                            )

                            NavigationDrawerItem(
                                label = {
                                    Row (
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = TablerIcons.Search, contentDescription = "Course Search")
                                        Text(text = "Course Search", modifier = Modifier.padding(start = 8.dp))
                                    }
                                },
                                selected = showInNav == AppScreen.CourseSearch,
                                onClick = {showInNav = AppScreen.CourseSearch},
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent,
                                    selectedContainerColor = Color(0xFF6699CC)
                                ),
                                shape = CardDefaults.shape
                            )
                        }
                        Divider()
                        Card(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 7.dp)
                        ) {
                            NavigationDrawerItem(
                                label = { Row (
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = TablerIcons.Users, contentDescription = "Friends")
                                    Text(text = "Friends", modifier = Modifier.padding(start = 8.dp))
                                } },
                                selected = showInNav == AppScreen.FriendsPage,
                                onClick = {showInNav = AppScreen.FriendsPage},
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent,
                                    selectedContainerColor = Color(0xFF6699CC)
                                ),
                                shape = CardDefaults.shape
                            )

                            NavigationDrawerItem(
                                label = {
                                    Row (
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = TablerIcons.Star, contentDescription = "Wishlist")
                                        Text(text = "Wishlist", modifier = Modifier.padding(start = 8.dp))
                                    } },
                                selected = showInNav == AppScreen.Wishlist,
                                onClick = {showInNav = AppScreen.Wishlist},
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent,
                                    selectedContainerColor = Color(0xFF6699CC)
                                ),
                                shape = CardDefaults.shape
                            )
                        }
                        Divider()
                        Card(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 7.dp)
                                .fillMaxHeight(0.85f)
                        ) {
                            NavigationDrawerItem(
                                label = {
                                    Row (
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = TablerIcons.Calendar, contentDescription = "Calendars")
                                        Text(text = "My Calendars", modifier = Modifier.padding(start = 8.dp))
                                    }
                                    },
                                selected = showInNav == AppScreen.Playground,
                                onClick = {showInNav = AppScreen.Playground},
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent,
                                    selectedContainerColor = Color(0xFF6699CC)
                                ),
                                shape = CardDefaults.shape
                            )
                            Box (
                                modifier = Modifier.fillMaxSize()
                            ) {
                                val stateVertical = rememberScrollState(0)
                                Box (
                                    modifier = Modifier.fillMaxHeight().verticalScroll(stateVertical)
                                ) {
                                    Column (
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        NavigationDrawerItem(
                                            label = { Text(text = "Current Calendar") },
                                            selected = showInNav == AppScreen.CourseSelection,
                                            onClick = {showInNav = AppScreen.CourseSelection},
                                            colors = NavigationDrawerItemDefaults.colors(
                                                unselectedContainerColor = Color.Transparent,
                                                selectedContainerColor = Color(0xFF6699CC)
                                            ),
                                            shape = CardDefaults.shape
                                        )

                                        for (it in customCalendars) {
                                            NavigationDrawerItem(
                                                label = { Text(text = it.name) },
                                                selected = showInNav == AppScreen.AlternateSchedule &&
                                                        selectedCalendar.id == it.id,
                                                onClick = {
                                                    calendarScope.launch {
                                                        try {
                                                            selectedCalendar = it
                                                            showInNav = AppScreen.AlternateSchedule

                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                    }
                                                },
                                                colors = NavigationDrawerItemDefaults.colors(
                                                    unselectedContainerColor = Color.Transparent,
                                                    selectedContainerColor = Color(0xFF6699CC)
                                                ),
                                                shape = CardDefaults.shape
                                            )
                                        }
                                    }
                                }
                                VerticalScrollbar(
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                        .fillMaxHeight(),
                                    adapter = rememberScrollbarAdapter(stateVertical)
                                )
                            }
                        }
                        Card(
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 7.dp)
                        ) {
                            NavigationDrawerItem(
                                label = { Text(text = "Logout") },
                                selected = false,
                                onClick = {
                                    // Dispatch LogoutUser action
                                    store.dispatch(LogoutUser())
                                    // Navigate to Login Screen or perform other necessary cleanup
                                    onLogout()
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent,
                                ),
                                shape = CardDefaults.shape
                            )
                        }
                    }
                    // ...other drawer items
                }
            }
        ) {

            when (showInNav) {

                is AppScreen.Home -> {
                    HomeScreen {
                        if (it == "Course Search") {
                            showInNav = AppScreen.CourseSearch
                        } else if (it == "Friends") {
                            showInNav = AppScreen.FriendsPage
                        } else if (it == "Wishlist") {
                            showInNav = AppScreen.Wishlist
                        } else {
                            showInNav = AppScreen.Playground
                        }
                    }
                }

                is AppScreen.CourseSelection -> {
                    selectionScreen(courses = courseList)
                }

                is AppScreen.CourseSearch -> {
                    CourseSearchScreen(courses = courseList)
                }

                is AppScreen.FriendsPage -> {
                    FriendsPage()
                }

                is AppScreen.Wishlist -> {
                    wishSelection()
                }

                is AppScreen.Playground -> {
                    PlaygroundCalendarsPage(customCalendars,
                        {
                          showInNav = AppScreen.CourseSelection
                        },
                        {
                            calendarScope.launch {
                                try {
                                    selectedCalendar = it
                                    showInNav = AppScreen.AlternateSchedule

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                    }, {
                        calendarScope.launch {
                            try {
                                val response = CustomCalendarClient.deleteCalendar(store.getState().userId, it.id)
                                if (response) {
                                    customCalendars = CustomCalendarClient.getCalendars(
                                        store.getState().userId)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        }) {
                        calendarScope.launch {
                            try {
                                selectedCalendar = it
                                customCalendars = CustomCalendarClient.getCalendars(
                                    store.getState().userId)
                                showInNav = AppScreen.AlternateSchedule
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                is AppScreen.AlternateSchedule -> {
                    key (selectedCalendar) {
                        CalendarEditView(courseList, selectedCalendar,
                            { showInNav = AppScreen.Playground })
                    }
                }
            }
        }
    }
}




