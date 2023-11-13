package components.courseSearch

import APIclient.CourseSchedulesClient
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
import components.store
import fuzzySearch.FuzzySearch
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.CourseDetails
import java.util.*

@Immutable
sealed class SearchScreen {
    object Search : SearchScreen()
    object CourseInfo : SearchScreen()
}


@Composable
fun CourseSearchScreen(onBackClick: () -> Unit, courses: List<CourseDetails>) {
    val courseNames = courses.map { "${it.subjectCode}${it.catalogNumber}" }
    val courseMap =courses.associateBy { it.subjectCode + it.catalogNumber }
    var currentScreen by remember { mutableStateOf<SearchScreen>(SearchScreen.Search) }
    var course by remember { mutableStateOf("") }
    var addedCourses by remember { mutableStateOf(emptySet<String>()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scope.launch{
            try {
                addedCourses = CourseSchedulesClient.getUserCourses(store.getState().userId).map { it.courseNum + it.component }.toSet()
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    // Content for Course Search screen
    Column {
        when (currentScreen) {
            is SearchScreen.Search -> {
                CustomSearchBar(courseNames, onBackClick) {
                    course = it
                    currentScreen = SearchScreen.CourseInfo
                }
            }
            is SearchScreen.CourseInfo -> {
                courseMap[course]?.let {
                    coursePage(courseNames, addedCourses, onBackClick = {
                        currentScreen = SearchScreen.Search
                    }, it, { newCourse: String -> course = newCourse})
                }
            }
        }
    }
}

@Composable
fun CustomSearchBar(courses: List<String>, onBackClick: () -> Unit,
                    changeToCourseInfo: (course: String) -> Unit,
                    ) {
    var text by remember { mutableStateOf("") }

    var searchedCourses by remember { mutableStateOf(emptyList<String>()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(text) {
        scope.launch{
            try {
                searchedCourses = FuzzySearch.extractTop(text.uppercase(Locale.getDefault()), courses, 5).map { it.toString() }
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            }
        }
    }
    Column {
        Column (modifier = Modifier.padding(horizontal = 12.dp)) {
            Button(onClick = onBackClick) {
                Text("Back")
            }
        }
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
                modifier = Modifier.fillMaxWidth(0.7f)
                    .padding(8.dp) // Add some padding to create spacing
                    .background(
                        color = Color(0xFFE0E0E0), // Background color
                        shape = RoundedCornerShape(16.dp) // Rounded corners
                    ),
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (text.isNotEmpty()) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                searchedCourses.take(5).map { course ->
                    ListItem(
                        headlineContent = { Text(course) },
                        modifier = Modifier
                            .clickable {
                                changeToCourseInfo(course)
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

