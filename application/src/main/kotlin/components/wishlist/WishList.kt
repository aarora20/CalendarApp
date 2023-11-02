package components.wishlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class wishCourses(
    // CS
    val subjectCode :String,
    // 346
    val catalogNumber :String,
    // Application Development
    val title :String,
)

@Composable
fun wishSelection(
    courseList: List<wishCourses>
) {
    var selectedCourses = remember { mutableStateListOf<wishCourses>().apply{addAll(courseList)} }
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
            Button(
                //modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    // Go back
                },
            ) {
                Text("Back")
            }
        }
        Row {
            LazyColumn(Modifier.padding(0.dp)) {
                items(selectedCourses) {
                    val (code, number, title) = it
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
                                selectedCourses.remove(it)
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