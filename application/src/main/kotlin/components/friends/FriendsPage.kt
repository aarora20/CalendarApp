package components.friends
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarStats
import compose.icons.tablericons.Clock
import compose.icons.tablericons.Search
import compose.icons.tablericons.Users


@Immutable
sealed class FriendScreen {
    object Search: FriendScreen()
    object Notification: FriendScreen()
    object FriendList: FriendScreen()
    object CompareCalendar : FriendScreen()
}

@Composable
fun FriendsPage() {
    var currentScreen by remember { mutableStateOf<FriendScreen>(FriendScreen.Search) }
    var compareScreen by remember { mutableStateOf<CompareScreen>(CompareScreen.Select) }

    Row {
        NavigationRailSidebar(
            //onBackClick = onBackClick,
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
                    FriendCompare(compareScreen,
                        onSelect = {compareScreen = CompareScreen.Select},
                        onCompare = { compareScreen = CompareScreen.Compare}
                        )
                }
                is FriendScreen.Notification -> {
                    FriendNotification()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRailSidebar(
    onSearch : () -> Unit,
    onList : () -> Unit,
    onNotif: () -> Unit,
    onCompare: () -> Unit
) {
    NavigationRail() {
        Spacer(modifier = Modifier.height(60.dp))
        PlainTooltipBox(
            tooltip = { Text("Friend Search", color = Color.White) }
        ) {
            NavigationRailItem(
                selected = false,
                onClick = onSearch,
                icon = { Icon(imageVector = TablerIcons.Search, "search")},
                modifier = Modifier.tooltipAnchor()
            )
        }
        PlainTooltipBox(
            tooltip = { Text("Requests", color = Color.White) }
        ) {
            NavigationRailItem(
                selected = false,
                onClick = onNotif,
                icon = { Icon(imageVector = TablerIcons.Clock, "pending")},
                modifier = Modifier.tooltipAnchor()
            )
        }
        PlainTooltipBox(
            tooltip = { Text("Friends List", color = Color.White) }
        ) {
            NavigationRailItem(
                selected = false,
                onClick = onList,
                icon = { Icon(imageVector = TablerIcons.Users, "friends",
                )},
                modifier = Modifier.tooltipAnchor()
            )
        }
        PlainTooltipBox(
            tooltip = { Text("Compare Calendar", color = Color.White) }
        ) {
            NavigationRailItem(
                selected = false,
                onClick = onCompare,
                icon = { Icon(imageVector = TablerIcons.CalendarStats, "calendar")},
                modifier = Modifier.tooltipAnchor()
            )
        }
    }
}