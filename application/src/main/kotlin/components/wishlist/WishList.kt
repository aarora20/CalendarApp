package components.wishlist

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.store
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.WishCourse

data class wishCourses(
    // CS
    val subjectCode: String,
    // 346
    val catalogNumber: String,
    // Application Development
    val title: String,
)

@Composable
fun wishSelection(/*onBackClick: () -> Unit*/) {
    val userId = store.getState().userId // replace this with the user ID
    var selectedCourses by remember { mutableStateOf(emptyList<WishCourse>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        scope.launch {
            try {
                selectedCourses = CourseSchedulesClient.getWishlist(userId)
            } catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
    // sets the page as a column
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {

        // set the header of the page letting the user know this will provide course selection options
        Text(
            text = "Wish List",
            color = Color.Black,
            fontSize = 30.sp,
            maxLines = 1
        )
        Row(
            Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Wish List of Courses (Click Course Name for More Info)"
            )
            // wish list option
            //Button(
                //modifier = Modifier.align(Alignment.CenterVertically),
                //onClick = {
                    //onBackClick()
                //},
            //) {
                //Text("Back")
            //}
        }
        Row {
            LazyColumn(Modifier.padding(0.dp)) {
                items(selectedCourses) { course ->
                    val (code, number, title) = course
                    val name = "$code $number: $title"
                    Row(
                        Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //Text(name)
                        TextButton(
                            onClick = {
                                // Go to course info page
                            }
                        ) {
                            Text(name)
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    val userId = store.getState().userId
                                    val success = CourseSchedulesClient.removeFromWishlist(userId, course.subjectCode, course.catalogNumber)
                                    if (success) {
                                        // Update local state after successful removal
                                        selectedCourses = selectedCourses.toMutableList().also { list ->
                                            list.remove(course)
                                        }
                                    } else {
                                        println("Error removing course from wishlist.")
                                    }
                                }
                            }
                        ) {
                            Text("Remove from Wish List")
                        }
                    }
                    Divider(color = Color.Black, thickness = 1.dp)
                }
            }
        }
    }
}
