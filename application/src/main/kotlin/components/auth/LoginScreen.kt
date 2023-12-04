package components.auth

import APIclient.AuthClient
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import components.common.DividerComposable
import components.store
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.UserParams
import store.SetUserID

@Preview
@Composable
fun LoginScreen(onSuccess: () -> Unit, onRegister: () -> Unit) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    color = MaterialTheme.colors.surface
                )
                .padding(
                    top = 16.dp,
                ).padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            SelectionContainer {
                Text("Login to Calendar App", style = TextStyle(
                    fontWeight = FontWeight.Bold
                ) )
            }
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("username") },
                leadingIcon = { Icon(Icons.Filled.AccountBox, contentDescription = null) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF92A3FD),
                    focusedLabelColor = Color(0xFF92A3FD),
                    cursorColor = Color(0xFF92A3FD)
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("password") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF92A3FD),
                    focusedLabelColor = Color(0xFF92A3FD),
                    cursorColor = Color(0xFF92A3FD)
                ),
                visualTransformation = if (password.isEmpty()) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                singleLine = true
            )

            Text(errorText, style = TextStyle(
                color = Color.Red
            ) )

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val response = AuthClient.loginUser(UserParams(username, password))
                            if (response != null) {
                                response.data?.let { SetUserID(it.userId) }?.let { store.dispatch(it) }
                                onSuccess()
                            } else {
                                errorText = "Wrong password or username does not exist"
                            }
                        } catch (e: ClientRequestException) {
                            println("Error fetching data: ${e.message}")
                            errorText = "Wrong password or username does not exist"
                        } catch (e: Exception) {
                            println(e.message)
                            errorText = "Wrong password or username does not exist"
                        }
                    }
                },
                modifier = Modifier
                    .width(200.dp).heightIn(48.dp),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().heightIn(48.dp).background(
                        brush = Brush.horizontalGradient(listOf(Color.Cyan, Color.Blue)),
                        shape = RoundedCornerShape(50.dp)
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Login", color = Color.White)
                }
            }
            DividerComposable("or")
            ClickableTextComposable("Are you a new user?", "register", onRegister)
        }
    }
}

@Composable
fun ClickableTextComposable(text: String, clickableText: String, onSwitch: () -> Unit) {
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectionContainer {
            Text(text, modifier = Modifier.padding(end = 5.dp))
        }
        ClickableText(text = AnnotatedString(clickableText), style = TextStyle(
            color = Color.Blue
        ), onClick = {
        onSwitch()
        })

    }
}

