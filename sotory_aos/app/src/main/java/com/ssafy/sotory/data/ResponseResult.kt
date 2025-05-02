package com.ssafy.sotory.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val exception: Throwable) : ApiResult<Nothing>
    data object Loading : ApiResult<Nothing>
}

fun <T> Flow<T>.asApiResult(): Flow<ApiResult<T>> = map<T, ApiResult<T>> { ApiResult.Success(it) }
    .onStart { emit(ApiResult.Loading) }
    .catch { emit(ApiResult.Error(it)) }


@Serializable
data class BaseResponse<T>(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
    @SerialName("data") val data: T? = null,
)

sealed interface ResponseResult<T : Any> {
    // 서버에서 정상적으로 데이터를 반환할 경우
    class Success<T : Any>(val data: T) : ResponseResult<T>

    // 서버에서 에러를 반환할 경우
    class ServerError<T : Any>(val code: String, val message: String) : ResponseResult<T>

    // 내부 에러를 반환할 경우
    class Exception<T : Any>(val e: Throwable, val message: String) : ResponseResult<T>
}
