package components.playground

import APIclient.CustomCalendarClient
import APIclient.FriendsClient
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import components.common.CustomIconButton
import components.common.WarningDialog
import components.store
import compose.icons.TablerIcons
import compose.icons.tablericons.Plus
import compose.icons.tablericons.Trash
import compose.icons.tablericons.X
import io.ktor.client.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import models.CustomCalendar
import models.CustomCalendarParams

@Preview
@Composable
fun PlaygroundCalendarsPage(
    calendarList: List<CustomCalendar>,
    isCalendarDialogOpen: Boolean,
    toggleCalendarDialog: (Boolean) -> Unit,
    goToCurrent: () -> Unit,
    onClickCalendar: (calendar: CustomCalendar) -> Unit,
    onRemoveCalendar: (calendar: CustomCalendar) -> Unit,
    onCreateNewCalendar: (calendar: CustomCalendar) -> Unit
) {
    val calendarScope = rememberCoroutineScope()

    Column (
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Hello",
                modifier = Modifier
                    .padding(8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            CalendarListItem(goToCurrent)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "My Custom Calendars",
                modifier = Modifier
                    .padding(8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            CustomIconButton(
                onClick={ toggleCalendarDialog(true) },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                tooltipText = "Create Calendar",
                buttonRadius = 36.dp,
                buttonSize = 15.dp,
                backgroundColor = Color.LightGray,
                icon = TablerIcons.Plus
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2)
        ) {
            items(calendarList) {
                CalendarItem(it, onClickCalendar, onRemoveCalendar)
            }
        }

        when {
            isCalendarDialogOpen -> {
                CreateCalendarDialog(
                    onDismissRequest = { toggleCalendarDialog(false) },
                    onConfirmation = {
                        toggleCalendarDialog(false)
                    },
                    onCreateNewCalendar = onCreateNewCalendar,
                    calendarScope
                )
            }
        }
    }
}

@Composable
fun CalendarListItem(goToCurrent: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(CardDefaults.shape)
            .clickable {
                goToCurrent()
            }
        ,
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(
                text = "Current Calendar",
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
        }
    }
}


@Composable
fun CalendarItem(calendar: CustomCalendar,
                 onClickCalendar: (calendar: CustomCalendar) -> Unit,
                 onRemoveCalendar: (calendar: CustomCalendar) -> Unit
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .height(70.dp)
            .clip(CardDefaults.shape)
            .clickable {
                 onClickCalendar(calendar)
            }
        ,
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row (
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = calendar.name,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 8.dp),
                )
                CustomIconButton(
                    onClick= { isDialogOpen = true },
                    modifier= Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    tooltipText= "Remove Calendar",
                    buttonRadius= 36.dp,
                    buttonSize= 15.dp,
                    backgroundColor = Color.LightGray,
                    icon = TablerIcons.Trash
                )
            }

        }
    }
    if (isDialogOpen) {
        WarningDialog(
            onDismissRequest = { isDialogOpen = false },
            onConfirmation = {
                onRemoveCalendar(calendar)
                isDialogOpen = false
            },
            dialogTitle = "Warning",
            dialogText = "Are you sure you want to delete this calendar? The associated courses will also be deleted.",
            icon = Icons.Default.Warning
        )
    }
}

@Composable
fun CreateCalendarDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onCreateNewCalendar: (calendar: CustomCalendar) -> Unit,
    scope: CoroutineScope
) {
    Dialog(
       onDismissRequest = {onDismissRequest()}
    ) {
        var name by remember { mutableStateOf("") }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(275.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Create New Calendar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    CustomIconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
                        tooltipText = "Close Modal",
                        buttonRadius = 20.dp,
                        buttonSize = 15.dp,
                        backgroundColor = Color.Transparent,
                        icon = TablerIcons.X
                    )
                }

                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        label = { Text("name") },
                        modifier = Modifier
                            .fillMaxWidth().padding(horizontal = 20.dp)
//                    .height(30.dp)
                            .padding(top = 20.dp),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Black
//                    fontStyle = MaterialTheme.typography.body2.fontStyle
                        ),
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                try {
                                    if (name.isEmpty()) {
                                        // Add Snackbar
                                    } else {
                                        val result = CustomCalendarClient.addCalendar(store.getState().userId,
                                            CustomCalendarParams(name))
                                        if (result != null ) {
                                            onCreateNewCalendar(result)
                                            onConfirmation()
                                        } else {
                                            // error
                                        }
                                    }
                                }catch (e: ClientRequestException) {
                                    println("Error fetching data: ${e.message}")
                                } catch (e: Exception) {
                                    println(e.message)
                                }
                            }
                        },
                        modifier = Modifier.padding(20.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.inverseOnSurface
                        )
                    ) {
                        Text(text="Confirm", modifier = Modifier.padding(horizontal = 4.dp))
                    }
                }
            }
        }
    }
}