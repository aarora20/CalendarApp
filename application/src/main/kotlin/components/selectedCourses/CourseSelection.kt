package components.selectedCourses

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.common.CustomIconButton
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.Calendar
import compose.icons.tablericons.DeviceFloppy
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Trash
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.UserCourse


@Composable
fun courseSelection(
    courseList: SnapshotStateList<UserCourse>,
    onCalendarClick: () -> Unit,
    removeCourse: (c : UserCourse) -> Unit,
    openSideSheet: () -> Unit
) {
    val courseMap = courseList.groupBy { "${it.courseNum} - ${it.courseTitle}" }
    val updateScope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = {
            SnackbarHost(hostState = snackbarState) {
                Snackbar(
                    snackbarData = it,
                    modifier = Modifier.width(200.dp),

                )
            }
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
        ) {

            // set the header of the page letting the user know this will provide course selection options
            Row(
                Modifier.fillMaxWidth().padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Course Selection",
                    color = Color.Black,
                    fontSize = 25.sp,
                    maxLines = 1
                )
                Button (
                    onClick = onCalendarClick,
                    modifier = Modifier.width(180.dp)
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = TablerIcons.Calendar,
                            contentDescription = "Calendar View",
                            tint = Color.White
                        )
                        Text("Calendar View")
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "List of Selected Courses",
                    fontSize = 20.sp
                )
                Row () {
                    CustomIconButton(
                        onClick= {
                            updateScope.launch {
                                try {
                                    val response = CourseSchedulesClient
                                        .updateSchedule(courseList.toList(), store.getState().userId)
                                    if (response) {
                                        snackbarState.showSnackbar(
                                            message = "Save Successful",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }catch (e: ClientRequestException) {
                                    println("Error fetching data: ${e.message}")
                                }
                            }
                        },
                        modifier= Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        tooltipText= "Save Calendar",
                        buttonRadius= 36.dp,
                        buttonSize= 15.dp,
                        backgroundColor = Color.LightGray,
                        icon = TablerIcons.DeviceFloppy
                    )
                    CustomIconButton(
                        onClick= openSideSheet,
                        modifier= Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        tooltipText= "Add Course",
                        buttonRadius= 36.dp,
                        buttonSize= 15.dp,
                        backgroundColor = Color.LightGray,
                        icon = TablerIcons.Plus
                    )
                }
            }
            Row {
                LazyColumn(Modifier.padding(0.dp)) {
                    items(courseMap.keys.toList()) {
                        courseMap[it]?.let { it1 -> CourseCluster(it1, it, removeCourse)}
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CourseCluster(components: List<UserCourse>, name: String, removeCourse: (c: UserCourse) -> Unit) {
    Column (
        Modifier.fillMaxWidth()
    ) {
        Row (
            modifier = Modifier.fillMaxWidth().border(0.dp, Color.Black)
                .background(Color.LightGray).padding(horizontal = 16.dp),
        ) {
            Text(text = name, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 4.dp))
        }
        Row (modifier = Modifier.fillMaxWidth().border(0.dp, Color.Black)
            .padding(horizontal = 12.dp, vertical = 4.dp)) {
            Column {
                for (course in components) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = course.component, modifier = Modifier.padding(vertical = 5.dp))

                        CustomIconButton(
                            onClick= {removeCourse(course)},
                            modifier= Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            tooltipText= "Remove Course",
                            buttonRadius= 36.dp,
                            buttonSize= 15.dp,
                            backgroundColor = Color.LightGray,
                            icon = TablerIcons.Trash
                        )
                    }
                }
            }
        }
    }
}

