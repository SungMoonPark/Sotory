@file:Suppress("UNCHECKED_CAST")

package com.ssafy.sotory.data

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ssafy.sotory.data.dto.ErrorResponse
import com.ssafy.sotory.util.NetworkUtil
import com.ssafy.sotory.util.NetworkUtil.jsonBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object ApiResponseHandler {
    suspend fun <T : Any> handleApiResponse(execute: suspend () -> Response<BaseResponse<T>>): ResponseResult<T> {
        return try {
            val response: Response<BaseResponse<T>> = execute()
            val body: BaseResponse<T>? = response.body()

            when {
                response.isSuccessful && body != null -> {
                    ResponseResult.Success(body.data as T)
                }

                response.isSuccessful && body == null -> {
                    ResponseResult.Success(Unit as T)
                }

                else -> {
                    val errorBody = response.errorBody() ?: throw Exception("errorBody가 없습니다.")
                    val errorResponse = getErrorResponse(errorBody)
                    ResponseResult.ServerError(
                        code = errorResponse.code, message = errorResponse.message
                    )
                }
            }
        } catch (e: HttpException) {
            ResponseResult.ServerError(
                code = e.code().toString(), message = e.message()
            )
        } catch (e: Exception) {
            ResponseResult.Exception(
                e = e, message = e.message ?: "unknown error"
            )
        }
    }

    private fun getErrorResponse(errorBody: ResponseBody): ErrorResponse {
        val okHttpClient =
            OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃 설정
                .readTimeout(30, TimeUnit.SECONDS) // 읽기 타임아웃 설정
                .writeTimeout(30, TimeUnit.SECONDS).build()
        val retrofit = Retrofit.Builder().baseUrl(NetworkUtil.BASE_URL).client(okHttpClient)
            .addConverterFactory(
                jsonBuilder.asConverterFactory("application/json".toMediaType())
            ).build()
        return retrofit.responseBodyConverter<ErrorResponse>(
            ErrorResponse::class.java,
            ErrorResponse::class.java.annotations,
        ).convert(errorBody) ?: throw IllegalArgumentException("errorBody를 변환할 수 없습니다.")
    }

    suspend fun <T : Any> ResponseResult<T>.onSuccess(executable: suspend (T) -> Unit): ResponseResult<T> =
        apply {
            if (this is ResponseResult.Success<T>) {
                executable(data)
            }
        }


    suspend fun <T : Any> ResponseResult<T>.onServerError(
        executable: suspend (code: String, message: String) -> Unit,
    ): ResponseResult<T> = apply {
        if (this is ResponseResult.ServerError<T>) {
            executable(code, message)
        }
    }


    suspend fun <T : Any> ResponseResult<T>.onException(executable: suspend (e: Throwable, message: String) -> Unit): ResponseResult<T> =
        apply {
            if (this is ResponseResult.Exception<T>) {
                executable(e, message)
            }
        }
}