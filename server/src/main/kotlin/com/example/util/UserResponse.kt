package com.example.util

import io.ktor.http.*
import kotlinx.serialization.Serializable


data class Response<T>(
    val statusCode: HttpStatusCode = HttpStatusCode.OK,
    val data: ResponseData<T>
) {

}

@Serializable
data class ResponseData<T>(
    val data: T? = null,
    val message: String? = null
)
