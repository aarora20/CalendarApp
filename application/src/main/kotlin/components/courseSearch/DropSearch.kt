package components.courseSearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fuzzySearch.FuzzySearch
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropSearch(
    courses: List<String>,
    onClickCourse: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    var searchedCourses by remember { mutableStateOf(emptyList<String>()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(text) {
        scope.launch{
            var searchText = ""
            try {
                searchText = if (text.isEmpty()) {
                    "ACC"
                } else {
                    text
                }
                searchedCourses = FuzzySearch.extractTop(searchText.uppercase(Locale.getDefault()), courses, 5)
                    .map { it.toString() }
            }catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        DockedSearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .onFocusChanged { active = it.isFocused },
            query = text,
            onQueryChange = {text = it},
            onSearch = { println("On Search") },
            active = active,
            onActiveChange = {
                active = it
            },
            colors = SearchBarDefaults.colors(Color.LightGray),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        ) {
            searchedCourses.take(5).map { course ->
                ListItem(
                    headlineContent = { Text(course) },
                    modifier = Modifier
                        .clickable {
                            text = ""
                            onClickCourse(course)
                            active = false
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = ListItemDefaults.colors(Color.LightGray)
                )
            }
        }
    }
}