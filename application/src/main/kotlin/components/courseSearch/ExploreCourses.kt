package components.courseSearch

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import models.CourseDetails

@Composable
fun ExploreCoursesScreen(subjectCode: String, allCourses: List<CourseDetails>, onBackClick: () -> Unit) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val category = selectedCategory  // Immutable local copy of selectedCategory

    val filteredCourses = allCourses.filter {
        it.subjectCode == subjectCode && when (category) {
            null -> true
            "5XX+" -> it.catalogNumber.toIntOrNull()?.let { num -> num >= 500 } == true
            else -> {
                // Modified regex to include optional letters at the end
                it.catalogNumber.matches(Regex("${category[0]}\\d\\d\\w*"))
            }
        }
    }.sortedWith(compareBy(
        { it.catalogNumber.filter { char -> char.isDigit() }.toIntOrNull() ?: Int.MAX_VALUE },
        { it.catalogNumber.filter { char -> char.isLetter() } }
    ))

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        // Page header
        Text(
            text = "Explore $subjectCode Courses",
            color = Color.Black,
            fontSize = 30.sp,
            maxLines = 1
        )

        // Back button and filter options
        Row(
            Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onBackClick) {
                Text("Back")
            }

            // Catalogue Number Filter
            listOf("1XX", "2XX", "3XX", "4XX", "5XX+").forEach { category ->
                Button(
                    onClick = { selectedCategory = category },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(category)
                }
            }
            Button(onClick = { selectedCategory = null }) {
                Text("Clear")
            }
        }

        // List of courses
        LazyColumn(Modifier.padding(0.dp)) {
            items(filteredCourses) { course ->
                Row(
                    Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val courseName = "${course.subjectCode} ${course.catalogNumber}"
                    Text("$courseName: ${course.title}")
                }
                Divider(color = Color.Black, thickness = 1.dp)
            }
        }
    }
}
