package store

fun tokenReducer(state: String, action: Any): String =
    when (action) {
        is SetToken -> action.token
        else -> state
    }


fun idReducer(state: String, action: Any): String =
    when (action) {
        is SetUserID -> action.userId
        else -> state
    }


fun rootReducer(state: AuthState, action: Any) = AuthState(
    token = tokenReducer(state.token, action),
    userId = idReducer(state.userId, action)
)