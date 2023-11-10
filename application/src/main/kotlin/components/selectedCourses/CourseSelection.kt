package components.selectedCourses

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.store
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.UserCourse


@Composable
fun courseSelection(
    courseList: SnapshotStateList<UserCourse>,
) {

    val courseMap = courseList.groupBy { "${it.courseNum} - ${it.courseTitle}" }

    val updateScope = rememberCoroutineScope()
    // sets the page as a column
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {

        // set the header of the page letting the user know this will provide course selection options
        Row(
            Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Course Selection",
                color = Color.Black,
                fontSize = 30.sp,
                maxLines = 1
            )
        }
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
                    updateScope.launch {
                        try {
                            CourseSchedulesClient.updateSchedule(courseList.toList(), store.getState().userId)
                        }catch (e: ClientRequestException) {
                            println("Error fetching data: ${e.message}")
                        }
                    }
                },
            ) {
                Text("Update Calendar")
            }
        }
//        Row {
//            LazyColumn(Modifier.padding(0.dp)) {
//                items(courseList) {
//                    val name = "${it.courseNum} ${it.component}: ${it.courseTitle}"
//                    Row(
//                        Modifier.fillMaxWidth().padding(10.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(name)
//                        Button(
//                            onClick = {
//                                courseList.remove(it)
//                            }
//                        ) {
//                            Text("Remove from Schedule")
//                        }
//                    }
//                }
//            }
//        }

        Row {
            LazyColumn(Modifier.padding(0.dp)) {
                items(courseMap.keys.toList()) {
                    courseMap.get(it)?.let { it1 -> CourseCluster(it1, it) }
                    Spacer(modifier = Modifier.height(10.dp))
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
    }
}

@Composable
fun CourseCluster(components: List<UserCourse>, name: String) {
    Column (
        Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth().border(0.dp, Color.Black)
                .background(Color.LightGray).padding(horizontal = 16.dp),
        ) {
            Text(name)
        }

        Row (modifier = Modifier.fillMaxWidth().border(0.dp, Color.Black)
            .padding(horizontal = 16.dp)) {
            Column {
                for (course in components) {
                    Text(course.component)
                }

            }
        }
    }
}
