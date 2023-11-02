package components.friends

import APIclient.AuthClient
import APIclient.CourseSchedulesClient
import APIclient.FriendsClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.Screen
import components.auth.LoginScreen
import components.auth.RegisterScreen
import components.common.DividerComposable
import components.courseSearch.CourseSearchScreen
import components.landingScreen
import components.selectedCourses.selectionScreen
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.CourseDetails
import models.User
import models.UserParams
import store.SetUserID


@Immutable
sealed class FriendScreen {
    object Search: FriendScreen()
    object Notification: FriendScreen()
    object FriendList: FriendScreen()
    object CompareCalendar : FriendScreen()
}

@Composable
fun FriendsPage(onBackClick: () -> Unit) {
    var currentScreen by remember { mutableStateOf<FriendScreen>(FriendScreen.Search) }

    Row {
        NavigationRailSidebar(
            onBackClick = onBackClick,
            onSearch = {currentScreen = FriendScreen.Search},
            onNotif = {currentScreen = FriendScreen.Notification },
            onList = { currentScreen = FriendScreen.FriendList},
            onCompare = { currentScreen = FriendScreen.CompareCalendar }
            )
        Column {
            when (currentScreen) {
                is FriendScreen.Search -> {
                   FriendSearch()
                }
                is FriendScreen.FriendList -> {
                    FriendListPage()
                }
                is FriendScreen.CompareCalendar -> {
                    FriendCompare()
                }
                is FriendScreen.Notification -> {
//                    FriendNotification()
                }
            }
        }
    }
}

@Composable
fun NavigationRailSidebar(
    onBackClick: () -> Unit,
    onSearch : () -> Unit,
    onList : () -> Unit,
    onNotif: () -> Unit,
    onCompare: () -> Unit
) {
    NavigationRail() {
        NavigationRailItem(
            selected = false,
            onClick = onBackClick,
            icon = { Icon(Icons.Default.Home, "home")}
        )
        Spacer(modifier = Modifier.height(60.dp))
        NavigationRailItem(
            selected = false,
            onClick = onSearch,
            icon = { Icon(imageVector = TablerIcons.Search, "search")}
        )
        NavigationRailItem(
            selected = false,
            onClick = onNotif,
            icon = { Icon(imageVector = TablerIcons.Clock, "pending")}
        )
        NavigationRailItem(
            selected = false,
            onClick = onList,
            icon = { Icon(imageVector = TablerIcons.Users, "friends",
                )}
        )
        NavigationRailItem(
            selected = false,
            onClick = onCompare,
            icon = { Icon(imageVector = TablerIcons.CalendarStats, "calendar")}
        )
    }
}