import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import components.landingPage

@Composable
@Preview
fun App() {
    landingPage()
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
