package components

import APIclient.CourseSchedulesClient
import APIclient.CustomCalendarClient
import APIclient.TokenClient
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import components.auth.LoginScreen
import components.auth.RegisterScreen
import components.courseSearch.CourseSearchScreen
import components.friends.FriendsPage
import components.home.HomeScreen
import components.playground.CalendarEditView
import components.playground.PlaygroundCalendarsPage
import components.selectedCourses.selectionScreen
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import models.CourseDetails
import models.CustomCalendar
import org.reduxkotlin.createThreadSafeStore
import store.*
import wishlistContainer

@Immutable
sealed class Screen {
    object Login: Screen()
    object SignUp: Screen()
    object Landing : Screen()
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

val INITIAL_STATE = AuthState("", "", "", "OCEAN")

val store = createThreadSafeStore(::rootReducer, INITIAL_STATE)

@Composable
fun landingPage(
    windowScope: FrameWindowScope,
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    var showInNav by remember { mutableStateOf<AppScreen>(AppScreen.Home) }
    var isComputing by remember { mutableStateOf(false) }
    var isCalendarDialogOpen by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var courseList by remember { mutableStateOf(emptyList<CourseDetails>()) }
    val scope = rememberCoroutineScope()

    windowScope.MenuBar {
        Menu("File", mnemonic = 'F') {
            Item(
                "New Calendar",
                onClick = {
                    if (currentScreen == Screen.Landing && !isComputing) {
                        showInNav = AppScreen.Playground
                        isCalendarDialogOpen = true
                    }
                },
                shortcut = KeyShortcut(
                    Key.N, ctrl = true
                )
            )
        }
        Menu("View", mnemonic = 'V') {
            Item(
                "Search",
                onClick = {
                          if (currentScreen == Screen.Landing && !isComputing) {
                              showInNav = AppScreen.CourseSearch
                          }
                },
                shortcut = KeyShortcut(
                    Key.S, ctrl = true
                )
            )
        }
    }

    LaunchedEffect(true) {
        scope.launch{
            try {
                isLoading = true
                var token = TokenClient.getIdToken("ktor")
                store.dispatch(SetKtorToken(token))
                courseList = CourseSchedulesClient.getCourses()
                token = TokenClient.getIdToken("spring")
                store.dispatch(SetSpringToken(token))
                isLoading = false
                while (true) {
                    delay(2500000);
                    token = TokenClient.getIdToken("ktor")
                    store.dispatch(SetKtorToken(token))
                    token = TokenClient.getIdToken("spring")
                    store.dispatch(SetSpringToken(token))
                }
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    if (isLoading) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    } else {
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
                    toggleComputing = { isComputing = it },
                    isCalendarDialogOpen = isCalendarDialogOpen,
                    toggleCalendarDialog = { isCalendarDialogOpen = it},
                    onLogout = {
                        currentScreen = Screen.Login
                    },
                    showInNav = showInNav
                ) {
                    showInNav = it
                }

            }
        }

    }
}

@Composable
fun landingScreen(
    courseList: List<CourseDetails>,
    toggleComputing: (Boolean) -> Unit,
    isCalendarDialogOpen: Boolean,
    toggleCalendarDialog: (Boolean) -> Unit,
    onLogout: () -> Unit,
    showInNav:  AppScreen,
    navigateNav: (AppScreen) -> Unit
) {

    val calendarScope = rememberCoroutineScope()
    var customCalendars by remember { mutableStateOf(emptyList<CustomCalendar>()) }
    var selectedCalendar by remember { mutableStateOf(CustomCalendar("", "")) }

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
                                onClick = {navigateNav(AppScreen.Home)},
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
                                onClick = {navigateNav(AppScreen.CourseSearch)},
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
                                onClick = {navigateNav(AppScreen.FriendsPage)},
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
                                onClick = {navigateNav(AppScreen.Wishlist)},
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
                                onClick = {navigateNav(AppScreen.Playground)},
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
                                            onClick = {navigateNav(AppScreen.CourseSelection)},
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
                                                            navigateNav(AppScreen.AlternateSchedule)

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
                                    store.dispatch(SetUserID(""))
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
                            navigateNav(AppScreen.CourseSearch)
                        } else if (it == "Friends") {
                            navigateNav(AppScreen.FriendsPage)
                        } else if (it == "Wishlist") {
                            navigateNav(AppScreen.Wishlist)
                        } else {
                            navigateNav(AppScreen.Playground)
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
                    wishlistContainer(courses = courseList)
                }

                is AppScreen.Playground -> {
                    PlaygroundCalendarsPage(customCalendars,
                        isCalendarDialogOpen,
                        toggleCalendarDialog,
                        {
                          navigateNav(AppScreen.CourseSelection)
                        },
                        {
                            calendarScope.launch {
                                try {
                                    selectedCalendar = it
                                    navigateNav(AppScreen.AlternateSchedule)

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
                                navigateNav(AppScreen.AlternateSchedule)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                is AppScreen.AlternateSchedule -> {
                    key (selectedCalendar) {
                        CalendarEditView(courseList, selectedCalendar, toggleComputing
                        ) { navigateNav(AppScreen.Playground) }
                    }
                }
            }
        }
    }
}




