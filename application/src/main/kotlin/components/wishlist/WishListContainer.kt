import APIclient.CourseSchedulesClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import components.courseSearch.DropSearch
import components.store
import components.wishlist.wishSelection
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.CourseDetails
import models.WishCourse

@Composable
fun wishlistContainer(
    courses:  List<CourseDetails>
) {
    val userId = store.getState().userId
    val selectedCourses =  remember { mutableStateListOf<WishCourse>() }
    val scope = rememberCoroutineScope()
    var term by remember { mutableStateOf("1A") }
    var isSheetOpen by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        scope.launch {
            try {
                val courses = CourseSchedulesClient.getWishlist(userId)
                selectedCourses.addAll(courses)
            } catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    Row (
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            modifier = Modifier.fillMaxHeight().fillMaxWidth(if (isSheetOpen) { 0.7f} else {1f})
        ) {
            wishSelection(selectedCourses, {
                term = it
                isSheetOpen = !isSheetOpen
            }) {
                selectedCourses.remove(it)
            };

        }

        if (isSheetOpen) {
            WishlistSideSheet(selectedCourses, term, courses.map { "${it.subjectCode}${it.catalogNumber}" },
                courses.associateBy { it.subjectCode + it.catalogNumber })
        }
    }
}

@Composable
fun WishlistSideSheet(
    selectedCourses: SnapshotStateList<WishCourse>,
    term: String,
    courseNames:  List<String>,
    courseMap:  Map<String, CourseDetails>
) {
    val addScope = rememberCoroutineScope()
    Column (
        modifier = Modifier.fillMaxSize().drawBehind {
            val strokeWidth = 2f
            val y = size.height - strokeWidth

            drawLine(
                color = Color.Black,
                start = Offset(0f, 0f), //(0,0) at top-left point of the box
                end = Offset(0f, y),//bottom-left point of the box
                strokeWidth = strokeWidth
            )
        }
    ) {
        Box (modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                SelectionContainer {
                    Text("Search for a course to add to your wishlist.")
                }
            }
            Box(
                modifier = Modifier.zIndex(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 30.dp).fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    DropSearch(courseNames, onClickCourse = {
                        addScope.launch {
                            try {
                                val course = courseMap[it]
                                if (course != null) {
                                    val newCourse =  WishCourse(course.subjectCode, course.catalogNumber, course.title, term)
                                    val success = CourseSchedulesClient.addToWishlist(store.getState().userId, newCourse)
                                    if (success) {
                                        selectedCourses.add(newCourse)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    })
                }
            }
        }
    }
}

