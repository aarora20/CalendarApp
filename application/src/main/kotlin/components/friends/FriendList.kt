package components.friends

import APIclient.FriendsClient
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import components.common.DividerComposable
import components.common.WarningDialog
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.Trash
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.User

@Composable
fun FriendListPage() {
    var listScope = rememberCoroutineScope()
    var friendList = remember { mutableStateListOf<User>() }
    LaunchedEffect(true) {
        listScope.launch{
            try {
               friendList.addAll(FriendsClient.getFriendList(store.getState().userId))
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
        DividerComposable("Friends")

        LazyColumn {
            items(friendList) {
                FriendItem(it) {
                    friendList.remove(it)
                }

            }
        }
    }
}

@Composable
fun FriendItem(user: User, removeFriend: (user: User) -> Unit) {
    val friendScope = rememberCoroutineScope()
    var openAlertDialog by remember {  mutableStateOf(false) }
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
                openAlertDialog = true
            }
        ) {
            Icon(
                imageVector = TablerIcons.Trash, "remove"
            )
        }
    }

    when {
        openAlertDialog -> {
            WarningDialog(
                onDismissRequest = { openAlertDialog = false },
                onConfirmation = {
                    friendScope.launch {
                        try {
                            val unfriendStatus = FriendsClient.unfriend(store.getState().userId, user.id)
                            if (unfriendStatus) {
                                removeFriend(user)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    openAlertDialog = false
                },
                dialogTitle = "Warning",
                dialogText = "Are you sure you want to unfriend this user?",
                icon = Icons.Default.Warning
            )
        }
    }

}