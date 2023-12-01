import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.*
import components.landingPage
import util.loadPreferences
import util.savePreferences
import java.awt.Dimension

@Composable
@Preview
fun App(
    windowScope: FrameWindowScope,
    ) {

    landingPage(windowScope)
}

fun main() = application {
    val preference = loadPreferences()
    val state = rememberWindowState()
    if (preference != null) {
        state.size = preference
    } else {
        // By default, the window will be maximized
        state.placement = WindowPlacement.Maximized
    }

    Window(onCloseRequest = {
        savePreferences(state.size)
         exitApplication()
    } , state) {
        window.minimumSize = Dimension(1000, 700)

        App(this)
    }
}

