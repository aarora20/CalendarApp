package store

data class AuthState(
    val token: String = "",
    val userId: String = ""
)