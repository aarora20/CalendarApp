package store

fun ktorTokenReducer(state: String, action: Any): String =
    when (action) {
        is SetKtorToken -> action.token
        else -> state
    }

fun springTokenReducer(state: String, action: Any): String =
    when (action) {
        is SetSpringToken -> action.token
        else -> state
    }


fun idReducer(state: String, action: Any): String =
    when (action) {
        is SetUserID -> action.userId
        else -> state
    }

fun calendarThemeReducer(state: String, action: Any): String =
    when (action) {
        is SetCalendarTheme -> action.calendarTheme
        else -> state
    }


fun rootReducer(state: AuthState, action: Any) = when (action) {
    is LogoutUser -> AuthState() // Resets to default state
    else -> AuthState(
        ktorToken = ktorTokenReducer(state.ktorToken, action),
        springToken = springTokenReducer(state.springToken, action),
        userId = idReducer(state.userId, action),
        calendarTheme = calendarThemeReducer(state.calendarTheme, action)
    )
}
