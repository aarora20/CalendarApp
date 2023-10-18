
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

data class addedCourses(
    // CS
    val subjectCode :String,
    // 346
    val catalogNumber :String,
    // Application Development
    val title :String,
)

private val listOfAddedCourses = listOf(
    addedCourses(
        subjectCode = "CS",
        catalogNumber = "346",
        title = "Application Development"
    ),
    addedCourses(
        subjectCode = "CS",
        catalogNumber = "240",
        title = "Data Structures and Data Management"
    ),
    addedCourses(
        subjectCode = "STAT",
        catalogNumber = "373",
        title = "Regression and Forecasting Methods in Finance"
    )
)
fun main() = application {
    Window(
        title = "Course Selection",
        onCloseRequest = ::exitApplication
    ) {
        // takes courses and its sections as inputs to display the course page
        courseSelection(listOfAddedCourses)
    }
}

@Composable
fun courseSelection(
    courseList: List<addedCourses>
) {
    var selectedCourses = remember { mutableStateListOf<addedCourses>().apply{addAll(courseList)} }
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
        Row(
            Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "List of Selected Courses"
            )
            Button(
                onClick = {
                    // Update Calendar View
                },
            ) {
                Text("Update Calendar")
            }
        }
        val n = courseList.size
        val extra = 5 - n
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
                        Text(name)
                        Button(
                            onClick = {
                                selectedCourses.remove(it)
                            }
                        ) {
                            Text("Remove from Schedule")
                        }
                    }
                }
            }
        }
        Row (
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    // need to go to Search function
                }
            ) {
                Text("Add Courses")
            }
        }
            /*
            Row(
                Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {

                    }
                ) {
                    Text("Remove from Schedule")
                }
            }

             */
        /*
        Row(
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
        */
    }
}