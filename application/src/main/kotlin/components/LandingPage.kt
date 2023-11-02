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
import components.selectedCourses.selectionScreen
import components.wishlist.wishCourses
import components.wishlist.wishSelection
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.CourseDetails
import org.reduxkotlin.createThreadSafeStore
import store.AuthState
import store.rootReducer

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
    object CourseSelection : Screen()
    object CourseSearch : Screen()
    object FriendsPage : Screen()
    object Wishlish : Screen()
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
            )
        }
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
            wishSelection(listOfWishCourses)
        }
    }
}

@Composable
fun landingScreen(
    onCourseSelectionClick: () -> Unit,
    onCourseSearchClick: () -> Unit,
    onFriendsClick: () -> Unit,
    onWishlistClick: () -> Unit
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
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = onWishlistClick) {
            Text("Wishlist")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = onFriendsClick) {
            Text("Friends")
        }
    }
}




