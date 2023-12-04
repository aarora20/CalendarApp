package components.friends

import APIclient.CourseSchedulesClient
import APIclient.FriendsClient
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.calendar.CalendarCompareScreen
import components.calendar.CompareTheme
import components.calendar.Theme
import components.common.CustomIconButton
import components.selectedCourses.ThemeDropdown
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.ChevronLeft
import compose.icons.tablericons.ChevronsDown
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
fun CompareThemeDropdown(
    expanded: Boolean,
    selectedTheme: CompareTheme,
    expandedFunc: (tf: Boolean) -> Unit,
    selectedThemeFunc: (theme: CompareTheme) -> Unit
) {
    Row (
        Modifier
            .padding(top = 15.dp).padding(end = 15.dp)
            .padding(0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dropdown Button
        Box(
            modifier = Modifier.clickable { expandedFunc(true) }
                .background(
                    color = if (selectedTheme == CompareTheme.OCEAN) {
                        Color(174, 214, 241)
                    } else if (selectedTheme == CompareTheme.PASTEL) {
                        Color(255, 207, 210)
                    } else if (selectedTheme == CompareTheme.SUNSET) {
                        Color(255,189,145)
                    } else {
                        Color(170,214,136)
                    },
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(horizontal = 10.dp, vertical = 7.5.dp)


        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${selectedTheme.name}",
                    fontSize = 15.sp
                )
                androidx.compose.material3.Icon(
                    imageVector = TablerIcons.ChevronsDown,
                    contentDescription = null
                )
            }

            androidx.compose.material3.DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expandedFunc(false) }
            ) {
                CompareTheme.values().forEach { theme ->
                    DropdownMenuItem(
                        onClick = {
                            selectedThemeFunc(theme)
                            expandedFunc(false)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = theme.name,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
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

    var expanded by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(CompareTheme.OCEAN) }

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

    Column {
        Row (
            modifier = Modifier.weight(0.1f),
            //horizontalArrangement = Arrangement.Center
        ) {

            CustomIconButton(
                onClick= onSelect,
                modifier= Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                tooltipText= "Go Back",
                buttonRadius= 36.dp,
                buttonSize= 15.dp,
                backgroundColor = Color.LightGray,
                icon = TablerIcons.ChevronLeft
            )

            Row (
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row (
                    modifier = Modifier.weight(1f)
                ) {
                    // FIRST CHECKBOX
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        //horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(start = 15.dp)
                    ) {
                        SelectionContainer {
                            Text("me")
                        }
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

                    // SECOND CHECKBOX
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        //horizontalArrangement = Arrangement.SpaceBetween,
                        //modifier = Modifier.padding(4.dp).fillMaxWidth()
                    ) {
                        Row (
                            //modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            SelectionContainer {
                                Text(friend.username,  maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
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


                Row (
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                ) {
                    // THEME button
                    fun expandedFunc(tf: Boolean) {
                        expanded = tf
                    }

                    fun selectedThemeFunc(theme: CompareTheme) {
                        selectedTheme = theme
                    }

                    CompareThemeDropdown(expanded, selectedTheme, ::expandedFunc, ::selectedThemeFunc)
                }

            }


        }

        Row (
            modifier = Modifier.weight(0.9f)
                .padding(start = 5.dp)

        ) {
            Row (
                horizontalArrangement = Arrangement.End,
            ) {
                CalendarCompareScreen(checkedUserList, checkedFriendList, onSelect, selectedTheme)
            }
        }
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