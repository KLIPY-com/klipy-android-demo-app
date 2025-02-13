package com.klipy.demoapp.data.infrastructure

import com.klipy.demoapp.data.dto.EmptyResponseBodyException
import retrofit2.HttpException
import retrofit2.Response

class ApiCallHelper {

    suspend fun <T> makeApiCall(
        apiCall: suspend () -> Response<T>
    ): Result<T> {
        return kotlin.runCatching {
            val response = apiCall.invoke()
            if (response.isSuccessful) {
                return response.body()?.let { body ->
                    Result.success(body)
                } ?: throw EmptyResponseBodyException()
            } else {
                throw HttpException(response)
            }
        }

    }
}