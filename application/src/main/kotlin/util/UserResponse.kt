package util

import io.ktor.http.*
import kotlinx.serialization.Serializable

data class RouteResponse<T>(
    val statusCode: HttpStatusCode = HttpStatusCode.OK,
    val data: RouteResponseData<T>
)

@Serializable
data class RouteResponseData<T>(
    val data: T? = null,
    val message: String? = null
)