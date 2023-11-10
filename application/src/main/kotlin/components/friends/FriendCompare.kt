package components.friends

import APIclient.CourseSchedulesClient
import APIclient.FriendsClient
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import components.calendar.CalendarCompareScreen
import components.store
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.User
import models.UserCourse

@Immutable
sealed class CompareScreen {
    object Select: CompareScreen()
    object Compare: CompareScreen()

}

@Composable
fun FriendCompare(currentScreen: CompareScreen,
                  onSelect: () -> Unit,
                  onCompare: () -> Unit) {
    var friend by remember { mutableStateOf(User("", "", "")) }

    Column {
        when (currentScreen) {
            CompareScreen.Compare -> {
                if (friend.id.isNotEmpty()) {
                    CompareCalendar(friend, onSelect)
                }
            }

            CompareScreen.Select -> {
                SelectCompare(
                    onCompare
                ) { friend = it }
            }

        }
    }

}

@Composable
fun CompareCalendar(
    friend: User,
    onSelect: () -> Unit
) {
    val calendarScope = rememberCoroutineScope()
    var userList by remember { mutableStateOf(emptyList<UserCourse>()) }
    var checkedUserList by remember { mutableStateOf(emptyList<UserCourse>()) }
    var friendList by remember { mutableStateOf(emptyList<UserCourse>()) }
    var checkedFriendList by remember { mutableStateOf(emptyList<UserCourse>()) }
    var isChecked1 by remember { mutableStateOf(true) }
    var isChecked2 by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        calendarScope.launch{
            try {
                userList = CourseSchedulesClient.getUserCourses(store.getState().userId)
                checkedUserList = userList
                friendList = CourseSchedulesClient.getUserCourses(friend.id)
                checkedFriendList = friendList
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    Row {
        Column (
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
            ) {
                Text ("me")
                Checkbox(
                    checked = isChecked1,
                    onCheckedChange = {
                        isChecked1 = it
                        checkedUserList = if (!isChecked1) {
                            emptyList()
                        } else {
                            userList
                        }
                               },
                    modifier = Modifier.padding(4.dp),
                    colors = CheckboxDefaults.colors()
                )
            }

            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(friend.username)
                Checkbox(
                    checked = isChecked2,
                    onCheckedChange = { isChecked2 = it
                        checkedFriendList = if (!isChecked2) {
                            emptyList()
                        } else {
                            friendList
                        }
                    },
                    modifier = Modifier.padding(4.dp),
                    colors = CheckboxDefaults.colors()
                )
            }
        }
        CalendarCompareScreen(checkedUserList, checkedFriendList, onSelect)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun SelectCompare(
    onCompare: () -> Unit,
    onSelectFriend: (friend: User) -> Unit
) {
    var selectedUser by remember { mutableStateOf(User("", "", "")) }
    var userList by remember { mutableStateOf(emptyList<User>()) }
    val userScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        userScope.launch{
            try {
                userList = FriendsClient.getFriendList(store.getState().userId)
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        value = selectedUser.username,
                        onValueChange = {
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        userList.forEach { user ->
                            DropdownMenuItem(onClick = {
                                selectedUser = user
                                expanded = false
                            }) {
                                Text(text = user.username)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (selectedUser.id.isNotEmpty()) {
                            onSelectFriend(selectedUser)
                            onCompare()
                        }
                    },
                ) {
                    Text(text = "Compare")
                }

            }
        }
    }
}