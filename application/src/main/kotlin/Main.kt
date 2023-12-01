import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.*
import components.landingPage
import util.loadWindowSize
import util.saveWindowSize
import java.awt.Dimension

@Composable
@Preview
fun App() {
    landingPage()
}

fun main() = application {
    val size = loadWindowSize()
    val state = rememberWindowState()
    if (size != null) {
        state.size = size
    } else {
        // By default, the window will be maximized
        state.placement = WindowPlacement.Maximized
    }

    Window(onCloseRequest = {
        saveWindowSize(state.size)
         exitApplication()
    } , state) {
        window.minimumSize = Dimension(1000, 700)
        App()
    }
}

