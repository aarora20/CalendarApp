import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.window.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.*

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp


// This doesn't work? - just copied it from the slides
@Composable
fun CustomTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = MaterialTheme.colors.copy(primary = Color.Red, secondary = Color.Magenta),
        shapes = MaterialTheme.shapes.copy(small = AbsoluteCutCornerShape(0.dp),
            medium = AbsoluteCutCornerShape(0.dp), large = AbsoluteCutCornerShape(0.dp)
        )
    ) { content() }
}

// Learning Compose
@Composable
@Preview
fun SimpleRow() {

    CustomTheme {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                modifier = Modifier
                    //.background(
                    //    color = Color.Blue
                    //)
                    .fillMaxHeight()
                    .weight(1f),

                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("f")
                Text("d")
            }

            Column (
                modifier = Modifier
                    //.background(
                    //    color = Color.Red
                    //)
                    .fillMaxHeight()
                    .weight(1f),

                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("x")
                Text("y")
            }

            Column (
                modifier = Modifier
                    //.background(
                    //    color = Color.Green
                    //)
                    .fillMaxHeight()
                    .weight(1f),

                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("a")
                Text("b")
            }
        }
    }
}

@Composable
@Preview
fun Day() {

}



@Composable
@Preview
fun Calendar() {
    Column () {
        Row (
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth()
                .background(
                    color = Color.Blue
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Main Course Schedule Calendar View")
        }

        Row (
            modifier = Modifier
                .weight(0.9f)

        ) {

            // TIMES


            // Sunday
            Column (
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Sunday")
            }

            // Monday
            Column (
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Monday")
            }

            // Tuesday
            Column (
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Tuesday")
            }

            // Wednesday
            Column (
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Wednesday")
            }

            // Thursday
            Column (
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Thursday")
            }

            // Friday
            Column (
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Friday")
            }

            // Saturday
            Column (
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("Saturday")
            }

        }

    }

}



fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        Calendar()
    }
}


/*
fun main() = application {
    Window(
        title = "Hello Window",
        onCloseRequest = ::exitApplication,
        state = WindowState(width = 300.dp, height = 250.dp, position = WindowPosition(50.dp, 50.dp))
    ) {
        CustomTheme {
            Column {

                Text("HELLO")

            }
        }
    }
}
*/