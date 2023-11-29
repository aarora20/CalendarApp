package components.wishlist

import APIclient.CourseSchedulesClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.common.CustomIconButton
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.Minus
import compose.icons.tablericons.Plus
import io.ktor.client.plugins.*
import kotlinx.coroutines.launch
import models.WishCourse

data class wishCourses(
    // 1
    val year: String,
    // A
    val term: String,
    // CS
    val subjectCode: String,
    // 346
    val catalogNumber: String,
    // Application Development
    val title: String,
)
/*
data class Term(
    val termTitle: String,
    val courses: List<wishCourses>
)

 */

private val inputTerms = listOf(
    wishCourses(
        year = "1",
        term = "A",
        subjectCode = "MATH",
        catalogNumber = "135",
        title = "Algebra for Honours Mathematics"
    ),
    wishCourses(
        year = "1",
        term = "A",
        subjectCode = "MATH",
        catalogNumber = "137",
        title = "Calculus 1 for Honours Mathematics"
    ),
    wishCourses(
        year = "1",
        term = "A",
        subjectCode = "CS",
        catalogNumber = "135",
        title = "Designing Functional Programs"
    ),
    wishCourses(
        year = "1",
        term = "B",
        subjectCode = "MATH",
        catalogNumber = "136",
        title = "Linear Algebra 1 for Honours Mathematics"
    ),
    wishCourses(
        year = "1",
        term = "B",
        subjectCode = "MATH",
        catalogNumber = "138",
        title = "Calculus 2 for Honours Mathematics"
    ),
    wishCourses(
        year = "1",
        term = "B",
        subjectCode = "CS",
        catalogNumber = "136",
        title = "Elementary Algorithm Design and Data Abstraction"
    ),
    wishCourses(
        year = "2",
        term = "A",
        subjectCode = "CS",
        catalogNumber = "245",
        title = "Logic and Computation"
    ),
    wishCourses(
        year = "2",
        term = "A",
        subjectCode = "CS",
        catalogNumber = "246",
        title = "Software Abstraction and Specification"
    ),
    wishCourses(
        year = "2",
        term = "A",
        subjectCode = "STAT",
        catalogNumber = "230",
        title = "Probability"
    ),
    wishCourses(
        year = "2",
        term = "B",
        subjectCode = "CS",
        catalogNumber = "241",
        title = "Foundations of Sequential Programs"
    ),
    wishCourses(
        year = "2",
        term = "B",
        subjectCode = "ECON",
        catalogNumber = "101",
        title = "Introduction to Microeconomics"
    ),
    wishCourses(
        year = "2",
        term = "B",
        subjectCode = "STAT",
        catalogNumber = "231",
        title = "Statistics"
    ),
    wishCourses(
        year = "3",
        term = "A",
        subjectCode = "CS",
        catalogNumber = "240",
        title = "Data Structures and Data Management"
    ),
    wishCourses(
        year = "3",
        term = "A",
        subjectCode = "MATH",
        catalogNumber = "239",
        title = " Introduction to Combinatorics"
    ),
    wishCourses(
        year = "3",
        term = "A",
        subjectCode = "ECON",
        catalogNumber = "102",
        title = "Introduction to Macroeconomics"
    ),
    wishCourses(
        year = "4",
        term = "B",
        subjectCode = "ECON",
        catalogNumber = "201",
        title = "Money and Banking"
    ),
)

/*
private val inputTerms = listOf(
    Term(
        termTitle = "1A",
        courses = listOf(
            wishCourses(
                subjectCode = "MATH",
                catalogNumber = "135",
                title = "Algebra for Honours Mathematics"
            ),
            wishCourses(
                subjectCode = "MATH",
                catalogNumber = "137",
                title = "Calculus 1 for Honours Mathematics"
            ),
            wishCourses(
                subjectCode = "CS",
                catalogNumber = "135",
                title = "Designing Functional Programs"
            ),
        )
    ),
    Term(
        termTitle = "1B",
        courses = listOf(
            wishCourses(
                subjectCode = "MATH",
                catalogNumber = "136",
                title = "Linear Algebra 1 for Honours Mathematics"
            ),
            wishCourses(
                subjectCode = "MATH",
                catalogNumber = "138",
                title = "Calculus 2 for Honours Mathematics"
            ),
            wishCourses(
                subjectCode = "CS",
                catalogNumber = "136",
                title = "Elementary Algorithm Design and Data Abstraction"
            ),
        )
    ),
    Term(
        termTitle = "2A",
        courses = listOf(
            wishCourses(
                subjectCode = "CS",
                catalogNumber = "245",
                title = "Logic and Computation"
            ),
            wishCourses(
                subjectCode = "CS",
                catalogNumber = "246",
                title = "Software Abstraction and Specification"
            ),
            wishCourses(
                subjectCode = "STAT",
                catalogNumber = "230",
                title = "Probability"
            ),
        )
    ),
    Term(
        termTitle = "2B",
        courses = listOf(
            wishCourses(
                subjectCode = "CS",
                catalogNumber = "241",
                title = "Foundations of Sequential Programs"
            ),
            wishCourses(
                subjectCode = "ECON",
                catalogNumber = "101",
                title = "Introduction to Microeconomics"
            ),
            wishCourses(
                subjectCode = "STAT",
                catalogNumber = "231",
                title = "Statistics"
            ),
        )
    ),
    Term(
        termTitle = "3A",
        courses = listOf(
            wishCourses(
                subjectCode = "CS",
                catalogNumber = "240",
                title = "Data Structures and Data Management"
            ),
            wishCourses(
                subjectCode = "MATH",
                catalogNumber = "239",
                title = " Introduction to Combinatorics"
            ),
            wishCourses(
                subjectCode = "ECON",
                catalogNumber = "102",
                title = "Introduction to Macroeconomics"
            ),
        )
    ),
)
*/

@Composable
fun wishSelection(
    //courseWishList: SnapshotStateList<wishCourses>,
    /*onBackClick: () -> Unit*/
) {
    val userId = store.getState().userId // replace this with the user ID
    var selectedCourses by remember { mutableStateOf(emptyList<WishCourse>()) }
    val scope = rememberCoroutineScope()

    val courseMap = inputTerms.groupBy { it.year + it.term }.toMutableMap()
    val allPossibleYears = listOf("1", "2", "3", "4")
    val allPossibleTerms = listOf("A", "B")

    // Iterate through all possible combinations
    for (year in allPossibleYears) {
        for (term in allPossibleTerms) {
            val key = year + term
            if (!courseMap.containsKey(key)) {
                // If the combination is missing, add an empty list for that combination
                courseMap[key] = emptyList()
            }
        }
    }
    val wishMap = courseMap.toList().sortedBy { it.first }.toMap()
    print(wishMap)

    LaunchedEffect(userId) {
        scope.launch {
            try {
                selectedCourses = CourseSchedulesClient.getWishlist(userId)
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
        LazyRow (
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            val years = intArrayOf(1, 2, 3, 4)
            items(years.toList()) {year ->
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Year $year",
                        fontSize = 25.sp,
                        color = Color.Black,
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                    for (terms in wishMap) {
                        //print("$terms \n")
                        if (year.digitToChar() == terms.key[0]) {
                            if (terms.key[1].toString() == "A") {
                                if (terms.value.isEmpty()) {
                                    TermBox("${year}A", terms.value, empty = true)
                                    Spacer(modifier = Modifier.width(16.dp))
                                } else {
                                    TermBox(terms.key, terms.value, empty = false)
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                            } else if (terms.key[1].toString() == "B") {
                                if (terms.value.isEmpty()) {
                                    TermBox("${year}B", terms.value, empty = true)
                                    Spacer(modifier = Modifier.width(16.dp))
                                } else {
                                    TermBox(terms.key, terms.value, empty = false)
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        /*
        LazyRow (
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
        ) {
            var counter = 1
            val wishMapTerms = inputTerms.groupBy { it.year + it.term }
            items(wishMapTerms.keys.toList()) { year ->
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Year $year",
                        fontSize = 25.sp,
                        color = Color.Black,
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                    for (terms in wishMapTerms) {
                        TermBox(term = terms.key, listCourses = terms.value)
                        Spacer(modifier = Modifier.width(16.dp))
                        //val wishMapTerms = terms.value { it.term }
                        print("$terms \n")
                    }
                    //print(wishMapYears)
                    /*
                    for (term in termPair) {
                        print("${wishMapYears[term]} \n")
                        TermBox(term = term, listCourses = wishMapYears[term]!!)
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                     */
                }
                counter++
            }
        }

         */
        /* What we had before adding multiple terms to wishlist
        Row {
            LazyColumn(Modifier.padding(0.dp)) {
                items(selectedCourses) { course ->
                    val (code, number, title) = course
                    val name = "$code $number: $title"
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .background(Color.White, RoundedCornerShape(8.dp)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            name,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp
                        )
                        CustomIconButton(
                            onClick = {
                                scope.launch {
                                    val userId = store.getState().userId
                                    val success = CourseSchedulesClient.removeFromWishlist(userId, course.subjectCode, course.catalogNumber)
                                    if (success) {
                                        // Update local state after successful removal
                                        selectedCourses = selectedCourses.toMutableList().also { list ->
                                            list.remove(course)
                                        }
                                    } else {
                                        println("Error removing course from wishlist.")
                                    }
                                }
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                            tooltipText = "Remove",
                            buttonRadius =  36.dp,
                            buttonSize = 15.dp,
                            backgroundColor = Color.LightGray,
                            icon = TablerIcons.Minus
                        )
                    }
                }
            }
        }
         */
    }
}

@Composable
fun TermBox(term: String, listCourses: List<wishCourses>, empty: Boolean) {
    Surface(
        modifier = Modifier
            .padding(8.dp)
            .width(350.dp)
            .height(350.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.background)
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp)),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = term,
                fontSize = 20.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            CourseList(courses = listCourses, empty = empty)
        }
    }
}

@Composable
fun CourseList(courses: List<wishCourses>, empty: Boolean) {
    Column {
        if (!empty) {
            courses.forEach { course ->
                CourseRow(course)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        // Add Course to Wishlist Button
        Box(Modifier.fillMaxSize()){
            CustomIconButton(
                onClick = {
                    /*
                    scope.launch {
                        val userId = store.getState().userId
                        val success = CourseSchedulesClient.removeFromWishlist(userId, course.subjectCode, course.catalogNumber)
                        if (success) {
                            // Update local state after successful removal
                            selectedCourses = selectedCourses.toMutableList().also { list ->
                                list.remove(course)
                            }
                        } else {
                            println("Error removing course from wishlist.")
                        }
                    }

                     */
                },
                modifier = Modifier.align(Alignment.BottomCenter),
                tooltipText = "Add",
                buttonRadius =  30.dp,
                buttonSize = 15.dp,
                backgroundColor = Color.LightGray,
                icon = TablerIcons.Plus
            )
        }
    }
}

@Composable
fun CourseRow(course: wishCourses) {
    Box(Modifier.fillMaxWidth()){
        Text(
            text = "${course.subjectCode} ${course.catalogNumber}",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Text(
            text = course.title,
            fontSize = 10.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.BottomStart)
        )
        CustomIconButton(
            onClick = {
                /*
                scope.launch {
                    val userId = store.getState().userId
                    val success = CourseSchedulesClient.removeFromWishlist(userId, course.subjectCode, course.catalogNumber)
                    if (success) {
                        // Update local state after successful removal
                        selectedCourses = selectedCourses.toMutableList().also { list ->
                            list.remove(course)
                        }
                    } else {
                        println("Error removing course from wishlist.")
                    }
                }

                 */
            },
            modifier = Modifier.align(Alignment.CenterEnd),
            tooltipText = "Remove",
            buttonRadius =  30.dp,
            buttonSize = 15.dp,
            backgroundColor = Color.LightGray,
            icon = TablerIcons.Minus
        )
    }
}