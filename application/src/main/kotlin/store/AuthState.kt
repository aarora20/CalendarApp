package store

data class AuthState(
    val token: String = "",
    val userId: String = "",
    val calendarTheme: String = "OCEAN"
)