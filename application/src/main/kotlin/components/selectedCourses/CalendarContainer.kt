package components.selectedCourses

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
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


@Composable
fun CalendarContainer(
    selectedCourses: SnapshotStateList<UserCourse>,
    onClickList: () -> Unit,
    openSideSheet: () -> Unit
) {

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
        CalendarRender(selectedCourses)
    }


}