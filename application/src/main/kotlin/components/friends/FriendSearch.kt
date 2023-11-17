package components.friends

import APIclient.FriendsClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.common.DividerComposable
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.Clock
import compose.icons.tablericons.Plus
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.User

@Composable
fun FriendSearch() {
    var query by remember { mutableStateOf("") }
    var userList by remember { mutableStateOf(emptyList<User>()) }
    val friendScope = rememberCoroutineScope()
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
            },
            modifier = Modifier
                .fillMaxWidth(0.7f)
//                    .height(30.dp)
                .padding(top = 20.dp),
//                    .onFocusChanged { focusState ->
//                        emailState.onFocusChange(focusState.isFocused)
//                        if (!focusState.isFocused) {
//                            emailState.enableShowErrors()
//                        }
//                    },
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Black
//                    fontStyle = MaterialTheme.typography.body2.fontStyle
            ),
//                isError = emailState.showErrors(),
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    imeAction = imeAction,
//                    keyboardType = KeyboardType.Email
//                ),
//                keyboardActions = KeyboardActions(
//                    onDone = {
//                        onImeAction()
//                    }
//                ),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                friendScope.launch {
                    try {
                        userList = FriendsClient.searchUsers(store.getState().userId, query)
                        if (userList.isNotEmpty()) {
                            query = ""
                        }
                    }catch (e: ClientRequestException) {
                        println("Error fetching data: ${e.message}")
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
            },
            modifier = Modifier
                .width(200.dp).heightIn(48.dp),
            contentPadding = PaddingValues(),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(Color.Transparent)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().heightIn(48.dp).background(
                    brush = Brush.horizontalGradient(listOf(Color(0xFFCF9FFF), Color(0xFFBF40BF))),
                    shape = RoundedCornerShape(20.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Search User", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        DividerComposable("Users")

        LazyColumn {
            items(userList) {
                FriendSearchItem(it)
            }
        }
    }
}

@Composable
fun FriendSearchItem(user: User) {
    var added by  mutableStateOf(false)
    val addScope = rememberCoroutineScope()
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
                addScope.launch {
                    try {
                        val friend = FriendsClient.sendFriendRequest(store.getState().userId, user.id)
                        if (friend == null) {
                            println("Error sending request")
                        } else {
                            added = true
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
                if (added) {
                    TablerIcons.Clock
                } else {
                    TablerIcons.Plus
                }, "add user"
            )
        }
    }

}
