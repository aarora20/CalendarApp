package components.selectedCourses
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.calendar.CalendarRender
import components.common.CustomIconButton
import compose.icons.TablerIcons
import compose.icons.tablericons.Calendar
import compose.icons.tablericons.List
import compose.icons.tablericons.Plus
import models.UserCourse
import components.calendar.Theme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material.*
import compose.icons.tablericons.ChevronsDown

@Composable
fun CalendarContainer(
    selectedCourses: SnapshotStateList<UserCourse>,
    onClickList: () -> Unit,
    openSideSheet: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(Theme.THEME1) }

    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            Modifier.fillMaxWidth().padding(top = 15.dp).padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Course Selection",
                color = Color.Black,
                fontSize = 25.sp,
                maxLines = 1
            )

            Row () {

                Row (
                    Modifier
                        .padding(top = 15.dp).padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dropdown Button
                    Box(
                        modifier = Modifier.clickable { expanded = true }

                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${selectedTheme.name}",
                                fontSize = 18.sp
                                )
                            Icon(
                                imageVector = TablerIcons.ChevronsDown,
                                contentDescription = null
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            Theme.values().forEach { theme ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedTheme = theme
                                        expanded = false
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Text(
                                            text = theme.name,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                CustomIconButton(
                    onClick= openSideSheet,
                    modifier= Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    tooltipText= "Add Course",
                    buttonRadius= 36.dp,
                    buttonSize= 15.dp,
                    backgroundColor = Color.LightGray,
                    icon = TablerIcons.Plus
                )

                Button(
                    onClick = onClickList,
                    modifier = Modifier.width(180.dp),
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = TablerIcons.List,
                            contentDescription = "List View",
                            tint = Color.White
                        )
                        Text("List View")
                    }
                }
            }
        }
        CalendarRender(selectedCourses, selectedTheme)
    }
}