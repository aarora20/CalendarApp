package util

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import components.calendar.Theme
import components.store
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import store.SetCalendarTheme
import java.io.File

@Serializable
data class SizeWindow(val width: Int, val height: Int)


@Serializable
data class UserPreference(val size: SizeWindow, val calendarTheme: String)

fun loadPreferences(): DpSize? {
    val homeDir = System.getProperty("user.home")
    val file = File("$homeDir/userPreferences.json")

    // if file already exists, read from it
    return if (file.exists()) {
        try {
            val jsonString = file.readText()

            val preference: UserPreference = Json.decodeFromString(jsonString)
            try {
                val theme = Theme.valueOf(preference.calendarTheme)
                store.dispatch(SetCalendarTheme(theme.name))
            } catch (e: Exception) {
                store.dispatch(SetCalendarTheme("OCEAN"))
            }
            DpSize(preference.size.width.dp, preference.size.height.dp)
        } catch (e: Exception) {
            store.dispatch(SetCalendarTheme("OCEAN"))
            null
        }

    } else {
        // Set to null if default size does not exist and set calendar theme to default OCEAN
        store.dispatch(SetCalendarTheme("OCEAN"))
        null
    }
}

// Save the window size as a json file when the user closes the application
fun savePreferences(size: DpSize) {
    val homeDir = System.getProperty("user.home")
    val file = File("$homeDir/userPreferences.json")

    val jsonString = Json.encodeToString(UserPreference(SizeWindow(size.width.value.toInt(),
        size.height.value.toInt()), store.getState().calendarTheme))
    file.writeText(jsonString)
}
