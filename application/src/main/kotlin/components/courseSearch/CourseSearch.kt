package components.courseSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ListItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import components.courseInfo.coursePage
import fuzzySearch.FuzzySearch
import kotlinx.coroutines.launch
import models.CourseDetails
import java.util.*

sealed class SearchScreen {
    object Search : SearchScreen()
    object CourseInfo : SearchScreen()
    class ExploreCourses(val subjectCode: String) : SearchScreen()
}

@Composable
fun CourseSearchScreen(courses: List<CourseDetails>) {
    val courseNames = courses.map { "${it.subjectCode}${it.catalogNumber}" }
    val courseMap = courses.associateBy { it.subjectCode + it.catalogNumber }
    var currentScreen by remember { mutableStateOf<SearchScreen>(SearchScreen.Search) }
    var previousScreen by remember { mutableStateOf<SearchScreen?>(null) }
    var course by remember { mutableStateOf("") }
    var addedCourses by remember { mutableStateOf(emptySet<String>()) }

    val scope = rememberCoroutineScope()

    val changeToCourseInfo: (String) -> Unit = { selectedCourse ->
        previousScreen = currentScreen
        course = selectedCourse
        currentScreen = SearchScreen.CourseInfo
    }

    val onBackClick: () -> Unit = {
        currentScreen = previousScreen ?: SearchScreen.Search
        previousScreen = null // Reset the previous screen
    }

    LaunchedEffect(true) {
        scope.launch {
            // Fetch and handle added courses
        }
    }

    Column {
        when (val screen = currentScreen) {
            is SearchScreen.Search -> {
                CustomSearchBar(courseNames, changeToCourseInfo, { subjectCode ->
                    previousScreen = currentScreen
                    currentScreen = SearchScreen.ExploreCourses(subjectCode)
                })
            }
            is SearchScreen.CourseInfo -> {
                courseMap[course]?.let { courseDetails ->
                    coursePage(courseNames, addedCourses, onBackClick, courseDetails, changeToCourseInfo, currentScreen)
                }
            }
            is SearchScreen.ExploreCourses -> {
                ExploreCoursesScreen(
                    subjectCode = screen.subjectCode,
                    allCourses = courses,
                    changeToCourseInfo = changeToCourseInfo,
                    onBackClick = onBackClick
                )
            }
            else -> {
                // Default action when none of the known screen types are matched
                currentScreen = SearchScreen.Search
            }
        }
    }
}

@Composable
fun CustomSearchBar(courses: List<String>, changeToCourseInfo: (course: String) -> Unit,
                    exploreCourses: (subjectCode: String) -> Unit) {
    var text by remember { mutableStateOf("") }
    var searchedCourses by remember { mutableStateOf(emptyList<String>()) }
    val subjectCodes = courses.map { it.split(Regex("[0-9]+"))[0] }.distinct()

    val scope = rememberCoroutineScope()

    LaunchedEffect(text) {
        scope.launch {
            searchedCourses = FuzzySearch.extractTop(text.uppercase(Locale.getDefault()), courses, 5).map { it.toString() }
        }
    }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Search") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(8.dp)
                    .background(color = Color(0xFFE0E0E0), shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (text.isNotEmpty()) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                searchedCourses.forEach { course ->
                    ListItem(
                        headlineContent = { Text(course) },
                        modifier = Modifier
                            .clickable { changeToCourseInfo(course) }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                val isSubjectCodeValid = subjectCodes.any { it.equals(text, ignoreCase = true) }
                if (isSubjectCodeValid) {
                    Button(
                        onClick = { exploreCourses(text.uppercase()) },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Explore All ${text.uppercase()} Courses")
                    }
                }
            }
        }
    }
}
