package components.courseSearch

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import fuzzySearch.FuzzySearch
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun CourseSearchScreen(onBackClick: () -> Unit) {
    // Content for Course Search screen
    Column {
        Text("Course Search Screen")
        Button(onClick = onBackClick) {
            Text("Back")
        }
        CustomSearchBar()
    }
}


@Composable
fun CustomSearchBar() {
    var text by remember { mutableStateOf("") }
//    var courses by remember { mutableStateOf(emptyList<CourseDetails>()) }
    var courseNames by remember { mutableStateOf(emptyList<String>()) }
    var searchedCourses by remember { mutableStateOf(emptyList<String>()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        println("in first")
        scope.launch{
            try {
                courseNames = CourseSchedulesClient.getCourses().map { "${it.subjectCode}${it.catalogNumber}"}

            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            }
        }
    }

    LaunchedEffect(text) {
        println("in second")
        scope.launch{
            try {
                println(text)
                searchedCourses = FuzzySearch.extractTop(text.uppercase(Locale.getDefault()), courseNames, 5).map { it.toString() }
                println(searchedCourses)
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
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
            if (text.isNotEmpty()) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    searchedCourses.take(5).map { course ->
                        ListItem(
                            headlineContent = { Text(course) },
//                        supportingContent = { Text("Additional info") },
//                        leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
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
}


//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchBarSample() {
//    var text by rememberSaveable { mutableStateOf("") }
//    var active by rememberSaveable { mutableStateOf(false) }
//
//    Box(Modifier.fillMaxSize().semantics { isTraversalGroup = true }) {
//        DockedSearchBar(
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .semantics { traversalIndex = -1f },
//            query = text,
//            onQueryChange = { text = it },
//            onSearch = { active = false },
//            active = active,
//            onActiveChange = {
//                active = it
//            },
//            colors = SearchBarDefaults.colors(Color.DarkGray),
//            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
////            trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = null) },
//        ) {
//            repeat(4) { idx ->
//                val resultText = "Suggestion $idx"
//                ListItem(
//                    headlineContent = { Text(resultText) },
//                    supportingContent = { Text("Additional info") },
//                    leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
//                    modifier = Modifier
//                        .clickable {
//                            text = resultText
//                            active = false
//                        }
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 4.dp)
//                )
//            }
//        }
//
//        LazyColumn(
//            contentPadding = PaddingValues(start = 16.dp, top = 72.dp, end = 16.dp, bottom = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            val list = List(100) { "Text $it" }
//            items(count = list.size) {
//                Text(list[it], Modifier.fillMaxWidth().padding(horizontal = 16.dp))
//            }
//        }
//    }
//}
