package util

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class SizeWindow(val width: Int, val height: Int)

fun loadWindowSize(): DpSize? {
    val homeDir = System.getProperty("user.home")
    val file = File("$homeDir/windowsize.json")

    // if file already exists, read from it
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

// Save the window size as a json file when the user closes the application
fun saveWindowSize(size: DpSize) {
    val homeDir = System.getProperty("user.home")
    val file = File("$homeDir/windowsize.json")

    val jsonString = Json.encodeToString(SizeWindow(size.width.value.toInt(), size.height.value.toInt()))
    file.writeText(jsonString)
}
