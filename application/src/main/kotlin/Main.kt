import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import components.landingPage
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.Dimension
import java.io.File

@Composable
@Preview
fun App() {
    landingPage()
}

@Serializable
data class SizeWindow(val width: Int, val height: Int)

fun loadWindowSize(): DpSize? {
    val homeDir = System.getProperty("user.home")
    val file = File("$homeDir/windowsize.json")

    return if (file.exists()) {
        try {
            val jsonString = file.readText()

            val size: SizeWindow = Json.decodeFromString(jsonString)
            DpSize(size.width.dp, size.height.dp)
        } catch (e: Exception) {
            null
        }

    } else {
        // Set to null if default size does not exist
        null
    }
}

fun saveWindowSize(size: DpSize) {
    val homeDir = System.getProperty("user.home")
    val file = File("$homeDir/windowsize.json")

    val jsonString = Json.encodeToString(SizeWindow(size.width.value.toInt(), size.height.value.toInt()))
    file.writeText(jsonString)
}

fun main() = application {
    val size = loadWindowSize()
    val state = rememberWindowState()
    if (size != null) {
        state.size = size
    } else {
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

