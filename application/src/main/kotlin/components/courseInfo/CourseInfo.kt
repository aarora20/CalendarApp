package components.courseSearch

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import models.CourseDetails

@Composable
fun CourseInfo(onBackClick: () -> Unit, course: CourseDetails) {
    Column {
        Button(onClick = onBackClick) {
            Text("Back")
        }
        Text(course.subjectCode)
        Text(course.catalogNumber)
        Text(course.courseId)
        Text(course.description)
    }
}
