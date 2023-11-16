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
import components.auth.RegisterScreen
import components.courseSearch.CourseSearchScreen
import components.friends.FriendsPage
import components.Home.HomeScreen
import components.calendar.render
import components.selectedCourses.selectionScreen
import components.wishlist.wishCourses
import components.wishlist.wishSelection
import components.calendar.Schedule
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.CourseDetails
import models.UserCourse
import org.reduxkotlin.createThreadSafeStore
import store.AuthState
import store.rootReducer
import androidx.compose.material3.*

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.rememberDrawerState

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// fake data for now for wishlist
// Should replace with api get results
private val listOfWishCourses = listOf(
    wishCourses(
        subjectCode = "CS",
        catalogNumber = "346",
        title = "Application Development"
    ),
    wishCourses(
        subjectCode = "CS",
        catalogNumber = "240",
        title = "Data Structures and Data Management"
    ),
    wishCourses(
        subjectCode = "STAT",
        catalogNumber = "373",
        title = "Regression and Forecasting Methods in Finance"
    )
)

@Immutable
sealed class Screen {
    object Login: Screen()
    object SignUp: Screen()
    object Landing : Screen()
    //object CourseSelection : Screen()
    //object CourseSearch : Screen()
    //object FriendsPage : Screen()
    //object Wishlish : Screen()
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
                courseList = courseList)

                        /*
                onCourseSelectionClick = {
                    currentScreen = Screen.CourseSelection
                },
                onCourseSearchClick = {
                    currentScreen = Screen.CourseSearch
                },
                onFriendsClick = {
                    currentScreen = Screen.FriendsPage
                },
                onWishlistClick = {
                    currentScreen = Screen.Wishlish
                }

                */


        }

        /*
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

        is Screen.FriendsPage -> {
            FriendsPage(onBackClick = {
                currentScreen = Screen.Landing
            })
        }
        // when click on wishlist, it will prompt to this screen
        is Screen.Wishlish -> {
            wishSelection(onBackClick = { currentScreen = Screen.Landing })
        }


         */
    }
}


@Immutable
sealed class AppScreen {
    object Home : AppScreen()
    object CourseSelection : AppScreen()
    object CourseSearch : AppScreen()
    object FriendsPage : AppScreen()
    object Wishlish : AppScreen()

    object Calendar: AppScreen()

}


@Composable
fun landingScreen(courseList: List<CourseDetails>) {



    var showInNav by remember { mutableStateOf<AppScreen>(AppScreen.Home) }

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
                            .width(200.dp)

                    ) {
                        Text("Welcome Jeff!", modifier = Modifier.padding(16.dp))
                        Divider()

                        NavigationDrawerItem(
                            label = { Text(text = "Home") },
                            selected = false,
                            onClick = {showInNav = AppScreen.Home}
                        )

                        NavigationDrawerItem(
                            label = { Text(text = "Course Search") },
                            selected = false,
                            onClick = {showInNav = AppScreen.CourseSearch}
                        )

                        NavigationDrawerItem(
                            label = { Text(text = "Course Selection") },
                            selected = false,
                            onClick = {showInNav = AppScreen.CourseSelection}
                        )

                        Divider()

                        NavigationDrawerItem(
                            label = { Text(text = "Calendar") },
                            selected = false,
                            onClick = {showInNav = AppScreen.Calendar}
                        )

                        Divider()

                        NavigationDrawerItem(
                            label = { Text(text = "Friends") },
                            selected = false,
                            onClick = {showInNav = AppScreen.FriendsPage}
                        )

                        Divider()

                        NavigationDrawerItem(
                            label = { Text(text = "Wishlist") },
                            selected = false,
                            onClick = {showInNav = AppScreen.Wishlish}
                        )

                        Divider()
                    }



                    // ...other drawer items
                }
            }
        ) {

            when (showInNav) {

                is AppScreen.Home -> {
                    HomeScreen()
                }

                is AppScreen.CourseSelection -> {
                    selectionScreen()
                }

                is AppScreen.CourseSearch -> {
                    CourseSearchScreen(courses = courseList)
                }

                is AppScreen.FriendsPage -> {
                    FriendsPage()
                }

                is AppScreen.Wishlish -> {
                    wishSelection()
                }

                is AppScreen.Calendar -> {

                    val selectedCourses = remember { mutableStateListOf<UserCourse>()}
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

                    render(courseList = selectedCourses)
                }
            }
        }

    }
}




