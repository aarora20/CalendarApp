package components.friends

import APIclient.CourseSchedulesClient
import APIclient.FriendsClient
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import components.common.DividerComposable
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.Check
import compose.icons.tablericons.Trash
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.Friend
import models.User

@Composable
fun FriendNotification(

) {
    var notificationScope = rememberCoroutineScope()
    var pendingList = remember { mutableStateListOf<User>() }
    LaunchedEffect(true) {
        notificationScope.launch{
            try {
                pendingList.addAll(FriendsClient.getPendingList(store.getState().userId))
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DividerComposable("Pending")

        LazyColumn {
            items(pendingList) {
                NotificationItem(it, pendingList)
            }
        }
    }
}

@Composable
fun NotificationItem(
    user: User,
    requests: SnapshotStateList<User>
) {
    val requestScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Gray),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = user.username,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        )

        IconButton(
            onClick = {
                requestScope.launch {
                    try {
                        val friend = FriendsClient.acceptFriendRequest(store.getState().userId, user.id)
                        if (friend != null) {
                            requests.remove(user)
                        }
                    }catch (e: ClientRequestException) {
                        println("Error fetching data: ${e.message}")
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
            }
        ) {
            Icon(
                imageVector = TablerIcons.Check, "remove"
            )
        }
        IconButton(
                onClick = {
                    requestScope.launch {
                        try {
                            val rejectStatus = FriendsClient.rejectFriendRequest(store.getState().userId, user.id)
                            if (rejectStatus) {
                                requests.remove(user)
                            }
                        }catch (e: ClientRequestException) {
                            println("Error fetching data: ${e.message}")
                        } catch (e: Exception) {
                            println(e.message)
                        }
                    }
                }
                ) {
            Icon(
                imageVector = TablerIcons.Trash, "remove"
            )
        }
    }

}