package components.auth

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun LoginScreen() {

    var text by remember { mutableStateOf("") }

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
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text("Login to Calendar App", style = TextStyle(
                fontWeight = FontWeight.Bold
            ) )
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("username") },
                leadingIcon = { Icon(Icons.Filled.AccountBox, contentDescription = null) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF92A3FD),
                    focusedLabelColor = Color(0xFF92A3FD),
                    cursorColor = Color(0xFF92A3FD)
                ),
            )
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("password") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF92A3FD),
                    focusedLabelColor = Color(0xFF92A3FD),
                    cursorColor = Color(0xFF92A3FD)
                ),
            )

            Button(
                onClick = {

                },
                modifier = Modifier
                    .width(200.dp),
                shape = RoundedCornerShape(50),

            ) {
                Text(text = "Login")
            }
        }
    }
}
