package components.selectedCourses
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.calendar.CalendarRender
import components.calendar.Theme
import components.common.CustomIconButton
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.ChevronsDown
import compose.icons.tablericons.List
import compose.icons.tablericons.Plus
import models.UserCourse
import store.SetCalendarTheme

@Composable
fun ThemeDropdown(
    expanded: Boolean,
    selectedTheme: Theme,
    expandedFunc: (tf: Boolean) -> Unit,
    selectedThemeFunc: (theme: Theme) -> Unit
) {
    Row (
        Modifier
            .padding(top = 6.dp).padding(end = 8.dp)
            .padding(0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dropdown Button
        Box(
            modifier = Modifier.clickable { expandedFunc(true) }
                .background(
                    color = if (selectedTheme == Theme.OCEAN) {
                        Color(174, 214, 241)
                    } else if (selectedTheme == Theme.PASTEL) {
                        Color(255, 207, 210)
                    } else if (selectedTheme == Theme.SUNSET) {
                        Color(255,189,145)
                    } else {
                        Color(170,214,136)
                           },
                    shape = RoundedCornerShape(7.dp)
                )
                .padding(horizontal = 10.dp, vertical = 7.5.dp)

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${selectedTheme.name} ",
                    fontSize = 15.sp
                )
                Icon(
                    imageVector = TablerIcons.ChevronsDown,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expandedFunc(false) },
            ) {
                Theme.values().forEach { theme ->
                    DropdownMenuItem(
                        onClick = {
                            selectedThemeFunc(theme)
                            expandedFunc(false)
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
}

@Composable
fun CalendarContainer(
    selectedCourses: SnapshotStateList<UserCourse>,
    onClickList: () -> Unit,
    openSideSheet: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
//    var selectedTheme by remember { mutableStateOf(Theme.OCEAN) }

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
                fun expandedFunc(tf: Boolean) {
                    expanded = tf
                }

                fun selectedThemeFunc(theme: Theme) {
                    store.dispatch(SetCalendarTheme(theme.name))
                }

                ThemeDropdown(expanded, Theme.valueOf(store.getState().calendarTheme), ::expandedFunc, ::selectedThemeFunc)

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

        CalendarRender(selectedCourses, Theme.valueOf(store.getState().calendarTheme))
    }
}