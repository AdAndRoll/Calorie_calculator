package ru.vasilev.data.network.rest

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

class FakeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val uri = chain.request().url.toUri().toString()

        val jsonResponse = when {
            // Имитируем ответ на загрузку (возвращаем ID запроса)
            uri.contains("/api/process") -> """{"id": "test_request_123"}"""

            // Имитируем ответ на проверку статуса
            uri.contains("/api/status/") -> """{
                "status": "completed", 
                "result": "{\"calories\": 450, \"items\": [\"bread\", \"cheese\"]}"
            }"""

            else -> """{"error": "not found"}"""
        }

        return Response.Builder()
            .code(200)
            .message("OK")
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .body(jsonResponse.toResponseBody("application/json".toMediaType()))
            .addHeader("content-type", "application/json")
            .build()
    }
}