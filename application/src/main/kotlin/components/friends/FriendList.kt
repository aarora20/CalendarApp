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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import components.common.DividerComposable
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.Trash
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.User

@Composable
fun FriendListPage() {
    var listScope = rememberCoroutineScope()
    var friendList by remember { mutableStateOf(emptyList<User>()) }
    LaunchedEffect(true) {
        listScope.launch{
            try {
               friendList = FriendsClient.getFriendList(store.getState().userId)
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
                FriendItem(it)
            }
        }
    }
}

@Composable
fun FriendItem(user: User) {
//    var removed by  mutableStateOf(false)
//    val addScope = rememberCoroutineScope()
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
            AlertDialogExample(
                onDismissRequest = { openAlertDialog = false },
                onConfirmation = {
                    openAlertDialog = false
                    println("Confirmation registered") // Add logic here to handle confirmation.
                },
                dialogTitle = "Alert dialog example",
                dialogText = "This is an example of an alert dialog with buttons.",
                icon = Icons.Default.Info
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}