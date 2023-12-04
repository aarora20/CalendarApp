package store

data class AuthState(
    val ktorToken: String = "",
    val springToken: String = "",
    val userId: String = "",
    val calendarTheme: String = "OCEAN"
)