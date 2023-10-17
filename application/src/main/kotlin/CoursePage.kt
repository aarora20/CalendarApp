import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

data class UniCourse( // The high-level information required for each course
    // Winter 2024
    val termName :String,
    // CS
    val subjectCode :String,
    // 346
    val catalogNumber :String,
    // Application Development
    val title :String,
    // course description
    val description :String,
    // Prereq: ...
    val requirementsDescription :String,
    // Has the course been added to schedule ? 1 : 0
    var courseAdded :Int
)

private val fakeCourse = UniCourse( // Using CS 346 as a test course
    termName = "Fall 2023",
    subjectCode = "CS",
    catalogNumber = "346",
    title = "Application Development",
    description = "Introduction to full-stack application design and development. Students will work in project teams" +
            " to design and build complete, working applications and services using standard tools. Topics include " +
            "best-practices in design, development, testing, and deployment.",
    requirementsDescription = "Prereq: CS 246/246E; Computer Science students only",
    courseAdded = 0
)

data class courseSection( // information for each course's individual section
    // 6904
    val classNumber :String,
    // LEC, TUT, TST, LAB
    val courseComponent :String,
    // 001, 002 (from LEC 001)
    val classSection :String,
    // days (M, Tu, W, Th, F, Sa, Su ...)
    val days :List<String>,
    // start time
    // "1:30:00"
    val classMeetingStartTime :String,
    // finish time
    val classMeetingEndTime :String,
    // If section has been added ? 1 : 0
    var sectionAdded :Int
)

private val fakeSections = listOf( // Using CS346's different sections as a test
    courseSection(
        classNumber = "6904",
        courseComponent = "LEC",
        classSection = "001",
        days = listOf("W"),
        classMeetingStartTime = "10:30 AM",
        classMeetingEndTime = "12:20 PM",
        sectionAdded = 0
    ),
    courseSection(
        classNumber = "6905",
        courseComponent = "LEC",
        classSection = "002",
        days = listOf("W"),
        classMeetingStartTime = "02:30 PM",
        classMeetingEndTime = "04:20 PM",
        sectionAdded = 0
    ),
    courseSection(
        classNumber = "6906",
        courseComponent = "LAB",
        classSection = "001",
        days = listOf("F"),
        classMeetingStartTime = "10:30 AM",
        classMeetingEndTime = "12:20 PM",
        sectionAdded = 0
    ),
    courseSection(
        classNumber = "6907",
        courseComponent = "LAB",
        classSection = "002",
        days = listOf("F"),
        classMeetingStartTime = "02:30 PM",
        classMeetingEndTime = "04:20 PM",
        sectionAdded = 0
    )
)

fun main() = application {
    Window(
        title = "Course Page",
        onCloseRequest = ::exitApplication
    ) {
        // takes courses and its sections as inputs to display the course page
        coursePage(fakeCourse, fakeSections)
    }
}

@Composable
fun coursePage(
    // accounts for the inputs
    classes: UniCourse,
    sections: List<courseSection>
) {
    // sets the page as a column
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
    ) {

        // set the header of the page letting the user know this will provide course info
        Text(
            text = "Course Information",
            color = Color.Black,
            fontSize = 30.sp,
            maxLines = 1
        )

        // subsequent row provides the course code and its name
        // also provides the user an option to add the course to their wish list
        Row (
            Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // provides the course code and name ie CS346: Application Development
            Text(
                text = classes.subjectCode + classes.catalogNumber + ": " + classes.title,
                style = MaterialTheme.typography.h6
            )

            // wish list option
            var wishList by remember { mutableStateOf("+ Wish List") }
            Button(
                //modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    wishList = "Added to Wish List!"
                },
            ) {
                Text(wishList)
            }
        }

        // provides the description of the course
        Row (
            Modifier.fillMaxWidth().padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = classes.description,
                fontSize = 15.sp,
            )
        }

        // provides the prereqs of the course
        Row (
            Modifier.fillMaxWidth().padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = classes.requirementsDescription,
                fontSize = 15.sp,
            )
        }

        // lets the user know that below this row is the schedule for the selected couse
        Row (
            Modifier.fillMaxWidth().padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Schedule for " + classes.termName + ":",
                fontSize = 15.sp,
            )
        }
        schedule(classes, sections)
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    button: Int
) {
    if (button == 1) {
        var addCoursestr by remember { mutableStateOf("+ Course Schedule") }
        TextButton(
            onClick = {
                Modifier
                    //.fillMaxWidth()
                    .weight(weight)
                    .border(0.dp, Color.Black)
                    .padding(0.dp)
                addCoursestr = "Added to Course Schedule!"

            }
        ) {
            Text(addCoursestr)
        }
    } else {
        Text(
            text = text,
            Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp)
        )
    }

}

@Composable
fun TableScreen(
    classes: UniCourse,
    sections: List<courseSection>
) {
    // Each cell of a column must have the same weight.
    val sectionWeight = .15f // 15%
    val classWeight = .10f // 10%
    val timeWeight = .3f // 30%
    val dateWeight = 0.25f // 25%
    val buttonWeight = .2f // 20%
    // The LazyColumn will be our table. Notice the use of the weights below
    LazyColumn(Modifier.fillMaxSize().padding(0.dp)) {
        // Here is the header
        item {
            Row(Modifier.background(Color.Gray)) {
                TableCell(text = "Class", weight = classWeight, button = 0)
                TableCell(text = "Section", weight = sectionWeight, button = 0)
                TableCell(text = "Time", weight = timeWeight, button = 0)
                TableCell(text = "Days", weight = dateWeight, button = 0)
                TableCell(text = "Add to Schedule", weight = buttonWeight, button = 0)
            }
        }
        // Here are all the lines of your table.
        items(sections) {
            val (classNum, courseComp, sectionNum, date, start, end)  = it
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell(text = classNum, weight = classWeight, button = 0)
                TableCell(text = courseComp + " " + sectionNum, weight = sectionWeight, button = 0)
                TableCell(text = start + " - " + end, weight = timeWeight, button = 0)
                TableCell(text = date.concat(), weight = dateWeight, button = 0)
                TableCell(text = "", weight = buttonWeight, button = 1)
            }
        }
    }
}

fun List<String>.concat() = this.joinToString("/") { it }.takeWhile { it.isDefined() }
@Composable
fun schedule(
    classes: UniCourse,
    sections: List<courseSection>
) {
    TableScreen(classes, sections)
    /*
    Row (
        Modifier.fillMaxWidth().padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Section",
            fontSize = 15.sp,
            //style = MaterialTheme.typography.h6
        )
        Text(
            text = "Class",
            fontSize = 15.sp,
            //style = MaterialTheme.typography.h6
        )
        Text(
            text = "Time",
            fontSize = 15.sp,
            //style = MaterialTheme.typography.h6
        )
        Text(
            text = "Date",
            fontSize = 15.sp,
            //style = MaterialTheme.typography.h6
        )
        Text(
            text = "",
            fontSize = 15.sp,
            //style = MaterialTheme.typography.h6
        )
    }
    var added = 0
    for (section in sections) {
        Row (
            Modifier.fillMaxWidth().padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.courseComponent + " " + section.classSection,
                fontSize = 15.sp,
                //style = MaterialTheme.typography.h6
            )
            Text(
                text = section.classNumber,
                fontSize = 15.sp,
                //style = MaterialTheme.typography.h6
            )
            Text(
                text = section.classMeetingStartTime + " - " + section.classMeetingEndTime,
                fontSize = 15.sp,
                //style = MaterialTheme.typography.h6
            )
            Text(
                text = section.classNumber,
                fontSize = 15.sp,
                //style = MaterialTheme.typography.h6
            )
            var addCoursestr by remember { mutableStateOf("+ Course Schedule") }
            var courseAdded = "Course Added"
            Button(
                //modifier = Modifier.align(Alignment.CenterVertically),
                onClick = {
                    addCoursestr = "Added to Course Schedule!"
                    added = 1
                }
            ) {
                if (added == 0) {
                    Text(addCoursestr)
                } else {
                    Text(courseAdded)
                }
            }
        }
    }
    */
}