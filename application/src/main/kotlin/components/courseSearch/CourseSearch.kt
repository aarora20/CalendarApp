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
import fuzzySearch.FuzzySearch
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun CourseSearchScreen(onBackClick: () -> Unit, courses: List<String>) {
    // Content for Course Search screen
    Column {
        Button(onClick = onBackClick) {
            Text("Back")
        }
        CustomSearchBar(courses)
    }
}


@Composable
fun CustomSearchBar(courses: List<String>) {
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
                                text = course
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

