import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun CourseSearchScreen(onBackClick: () -> Unit) {
    // Content for Course Search screen
    Column {
        Text("Course Search Screen")
        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}
