package components.courseSearch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.TablerIcons
import compose.icons.tablericons.ChevronLeft
import models.CourseDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreCoursesScreen(
    subjectCode: String,
    allCourses: List<CourseDetails>,
    changeToCourseInfo: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val currentCategory = selectedCategory

    val filteredCourses = allCourses.filter {
        it.subjectCode == subjectCode && when (currentCategory) {
            null -> true
            "5XX+" -> it.catalogNumber.toIntOrNull()?.let { num -> num >= 500 } == true
            else -> it.catalogNumber.matches(Regex("${currentCategory[0]}\\d\\d\\w*"))
        }
    }.sortedWith(compareBy(
        { it.catalogNumber.filter { char -> char.isDigit() }.toIntOrNull() ?: Int.MAX_VALUE },
        { it.catalogNumber.filter { char -> char.isLetter() } }
    ))

    Box(modifier = Modifier.fillMaxSize()) {
        // Back Button at the top
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Start) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(24.dp)
                        .background(color = Color.LightGray, shape = CircleShape)
                ) {
                    Icon(imageVector = TablerIcons.ChevronLeft, contentDescription = "Back", modifier = Modifier.size(15.dp))
                }
            }
        }

        Column(modifier = Modifier.padding(top = 56.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            SelectionContainer {
                Text(
                    text = "Explore $subjectCode Courses",
                    color = Color.Black,
                    fontSize = 30.sp,
                    maxLines = 1
                )
            }

            Row(
                Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            // Displaying the filtered and sorted courses
            LazyColumn(Modifier.padding(top = 8.dp)) {
                items(filteredCourses) { course ->
                    CourseRow(course, changeToCourseInfo)
                }
            }
        }
    }
}

@Composable
fun CourseRow(course: CourseDetails, changeToCourseInfo: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .clickable { changeToCourseInfo("${course.subjectCode}${course.catalogNumber}") }
            .background(Color.White, RoundedCornerShape(8.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${course.subjectCode} ${course.catalogNumber}: ${course.title}",
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp
        )
        Icon(Icons.Filled.Info, contentDescription = "Details", modifier = Modifier.padding(16.dp))
    }
}
