package components.wishlist

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.common.CustomIconButton
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.Minus
import compose.icons.tablericons.Plus
import io.ktor.client.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import models.WishCourse


@Composable
fun wishSelection() {
    val userId = store.getState().userId
    var selectedCourses by remember { mutableStateOf(emptyList<WishCourse>()) }
    val scope = rememberCoroutineScope()


    var wishMap by remember { mutableStateOf(mapOf<String, List<WishCourse>>()) }
    val allPossibleYears = listOf("1", "2", "3", "4")
    val allPossibleTermYears = listOf("1A", "1B", "2A", "2B", "3A", "3B", "4A", "4B")

    val scrollState = rememberScrollState()

    LaunchedEffect(userId) {
        scope.launch {
            try {
                selectedCourses = CourseSchedulesClient.getWishlist(userId)
                // Group courses by termYear
                wishMap = selectedCourses.groupBy { it.termYear }
            } catch (e: ClientRequestException) {
                println("Error fetching data: ${e.message}")
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
    // sets the page as a column
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        // set the header of the page letting the user know this will provide course selection options
        Text(
            text = "Wish List",
            color = Color.Black,
            fontSize = 30.sp,
            maxLines = 1
        )
        Row(
            Modifier.fillMaxWidth().padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Plan your schedule beyond the current term using the Wish List below:"
            )
        }
        Box {
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allPossibleYears.forEach { year ->
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Year $year",
                            fontSize = 25.sp,
                            color = Color.Black
                        )
                        allPossibleTermYears.filter { it.startsWith(year) }.forEach { termYear ->
                            val coursesForTerm = wishMap[termYear] ?: emptyList()
                            TermBox(termYear, coursesForTerm, selectedCourses, userId, scope) { newCourses ->
                                selectedCourses = newCourses
                                wishMap = selectedCourses.groupBy { it.termYear }
                            }
                        }
                    }
                }
            }

            // Horizontal Scrollbar
            HorizontalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState),
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
fun TermBox(termYear: String, listCourses: List<WishCourse>, selectedCourses: List<WishCourse>, userId: String,
            scope: CoroutineScope, updateCourses: (List<WishCourse>) -> Unit) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .width(350.dp)
            .heightIn(min = 100.dp, max = 400.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.surface)
            .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
        color = MaterialTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = termYear,
                fontSize = 20.sp,
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            listCourses.forEachIndexed { index, course ->
                CourseRow(course, selectedCourses, userId, scope, updateCourses)
                if (index < listCourses.size - 1) {
                    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f))
                }
            }
            Spacer(modifier = Modifier.weight(1f)) // Push the button to the bottom
            CustomIconButton(
                onClick = {
                    // Button functioning
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                tooltipText = "Add Course",
                buttonRadius = 30.dp,
                buttonSize = 15.dp,
                backgroundColor = Color.LightGray,
                icon = TablerIcons.Plus
            )
            Spacer(modifier = Modifier.height(16.dp)) // Space after the button
        }
    }
}

@Composable
fun CourseRow(course: WishCourse, selectedCourses: List<WishCourse>, userId: String,
              scope: CoroutineScope, updateCourses: (List<WishCourse>) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = "${course.subjectCode} ${course.catalogNumber}",
                fontSize = 14.sp,
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = course.courseTitle,
                fontSize = 12.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
        }
        if (selectedCourses.any {
                it.subjectCode == course.subjectCode &&
                        it.catalogNumber == course.catalogNumber &&
                        it.termYear == course.termYear  }) {
            CustomIconButton(
                onClick = {
                    scope.launch {
                        val success = CourseSchedulesClient.removeFromWishlist(userId, course.subjectCode, course.catalogNumber, course.termYear)
                        if (success) {
                            // Fetch the updated list from the backend
                            val updatedCourses = CourseSchedulesClient.getWishlist(userId)
                            // Update the frontend state
                            updateCourses(updatedCourses)
                        } else {
                            println("Error removing course from wishlist.")
                        }
                    }
                },
                modifier = Modifier.padding(end = 16.dp),
                tooltipText = "Remove",
                buttonRadius = 30.dp,
                buttonSize = 15.dp,
                backgroundColor = Color.LightGray,
                icon = TablerIcons.Minus
            )
        }
    }
}
