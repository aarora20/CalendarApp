package util

import io.ktor.http.*
import kotlinx.serialization.Serializable

data class UserResponse<T>(
    val statusCode: HttpStatusCode = HttpStatusCode.OK,
    val data: UserResponseData<T>
)

@Serializable
data class UserResponseData<T>(
    val data: T? = null,
    val message: String? = null
)