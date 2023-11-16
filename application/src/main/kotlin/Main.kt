import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import components.landingPage
import java.awt.Dimension


@Composable
@Preview
fun App() {
    landingPage()
}

fun main() = application {
    val state = rememberWindowState(placement = WindowPlacement.Maximized)
    Window(onCloseRequest = ::exitApplication, state) {
        window.minimumSize = Dimension(1500, 600)
        App()
    }
}

