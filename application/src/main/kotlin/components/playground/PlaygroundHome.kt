package components.playground

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import components.common.DividerComposable
import components.friends.AlertDialogExample
import components.friends.FriendItem
import compose.icons.TablerIcons
import compose.icons.tablericons.Trash
import models.CustomCalendar
import models.User

@Composable
fun PlaygroundHome() {
    var customCalendars by remember { mutableStateOf(emptyList<CustomCalendar>()) }
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DividerComposable("My Calendars")

        LazyColumn {
            items(customCalendars) {
                CalendarItem(it)
            }
        }
    }
}

@Composable
fun CalendarItem(calendar: CustomCalendar) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Gray),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = calendar.name,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        )
    }
}