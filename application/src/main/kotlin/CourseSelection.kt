
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        title = "Course Selection",
        onCloseRequest = ::exitApplication
    ) {
        // takes courses and its sections as inputs to display the course page
        courseSelection()
    }
}

@Composable
fun courseSelection() {
    // sets the page as a column
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {

        // set the header of the page letting the user know this will provide course selection options
        Text(
            text = "Course Selection",
            color = Color.Black,
            fontSize = 30.sp,
            maxLines = 1
        )

        Row (
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var select by remember { mutableStateOf("CS346: Application Development") }
            Button(
                //modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    select = "Course Removed"
                    // go to Search function
                    // wishList = course selected from search function
                },
            ) {
                Text(select)
            }
        }
        Row (
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var wishList by remember { mutableStateOf("+ Add Course") }
            Button(
                //modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    // go to Search function
                    // wishList = course selected from search function
                },
            ) {
                Text(wishList)
            }
        }
        Row (
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var wishList by remember { mutableStateOf("+ Add Course") }
            Button(
                //modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    // go to Search function
                    // wishList = course selected from search function
                },
            ) {
                Text(wishList)
            }
        }
        Row (
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var wishList by remember { mutableStateOf("+ Add Course") }
            Button(
                //modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    // go to Search function
                    // wishList = course selected from search function
                },
            ) {
                Text(wishList)
            }
        }
        Row (
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var wishList by remember { mutableStateOf("+ Add Course") }
            Button(
                //modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    // go to Search function
                    // wishList = course selected from search function
                },
            ) {
                Text(wishList)
            }
        }
    }
}