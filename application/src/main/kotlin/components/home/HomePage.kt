package components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.TablerIcons
import compose.icons.tablericons.Calendar
import compose.icons.tablericons.Search
import compose.icons.tablericons.Star
import compose.icons.tablericons.Users
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    goToPage: (page: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 5.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        val currentDate = LocalDate.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))
        Column {
            Text(
                text = formattedDate,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
    Row {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text ="Welcome!", fontFamily = FontFamily.Cursive, fontSize = 40.sp)
            Text(text ="Click on one of the features below to get started!")
            Row (
                modifier = Modifier.padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Card (
                    modifier = Modifier.width(200.dp).height(200.dp).padding(horizontal = 8.dp)
                        .clickable {
                            goToPage("Course Search")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFfff44f)
                    )
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = TablerIcons.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(90.dp)
                        )
                        Text("Course Search", fontWeight = FontWeight.Bold)
                    }

                }
                Card (
                    modifier = Modifier.width(200.dp).height(200.dp).padding(horizontal = 8.dp)
                        .clickable {
                            goToPage("Friends")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFfff44f)
                    )
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = TablerIcons.Users,
                            contentDescription = "Friends",
                            modifier = Modifier.size(90.dp)
                        )
                        Text("Friends", fontWeight = FontWeight.Bold)
                    }
                }

            }
            Row (
                modifier = Modifier.padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Card (
                    modifier = Modifier.width(200.dp).height(200.dp).padding(horizontal = 8.dp)
                        .clickable {
                            goToPage("Wishlist")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFfff44f)
                    )
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = TablerIcons.Star,
                            contentDescription = "WishList",
                            modifier = Modifier.size(90.dp)
                        )
                        Text("Wishlist", fontWeight = FontWeight.Bold)
                    }
                }
                Card (
                    modifier = Modifier.width(200.dp).height(200.dp).padding(horizontal = 8.dp)
                        .clickable {
                            goToPage("Calendars")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFfff44f)
                    )
                ) {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = TablerIcons.Calendar,
                            contentDescription = "Calendars",
                            modifier = Modifier.size(90.dp)
                        )
                        Text("Calendars", fontWeight = FontWeight.Bold)
                    }
                }

            }
        }
    }
}