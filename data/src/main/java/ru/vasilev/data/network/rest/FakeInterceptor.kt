package ru.vasilev.data.network.rest

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.util.concurrent.TimeUnit

class FakeInterceptor : Interceptor {

    // Счётчик попыток для имитации процесса обработки
    private var pollCounter = 0

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val soapAction = request.header("SOAPAction")
        val contentType = request.header("Content-Type") ?: ""

        // Имитируем небольшую задержку сети
        Thread.sleep(100)

        return when {
            // --- ЛОГИКА gRPC (Заглушка) ---
            // gRPC всегда использует application/grpc
            contentType.contains("application/grpc") -> {
                // Interceptor не может легко подделать бинарный Protobuf ответ,
                // поэтому просто пропускаем или выдаем 404, если сервера нет.
                chain.proceed(request)
            }

            // --- ЛОГИКА SOAP ---
            url.contains("service/soap") || soapAction != null -> {
                handleSoapRequest(request, soapAction)
            }

            // --- ЛОГИКА REST ---
            else -> handleRestRequest(url)
        }
    }

    private fun handleRestRequest(url: String): Response {
        val jsonResponse = when {
            url.contains("/api/process") -> {
                pollCounter = 0 // Сбрасываем счётчик для нового процесса
                """{"id": "rest_123"}"""
            }
            url.contains("/api/status/") -> {
                pollCounter++
                if (pollCounter < 3) {
                    // Имитируем, что всё еще в процессе (ТЗ: механизм опроса)
                    """{"status": "processing", "result": ""}"""
                } else {
                    """{
                        "status": "completed", 
                        "result": "{\"calories\": 450, \"items\": [\"REST_bread\"]}"
                    }"""
                }
            }
            else -> """{"error": "not found"}"""
        }
        return createResponse(jsonResponse, "application/json")
    }

    private fun handleSoapRequest(request: Request, soapAction: String?): Response {
        val requestBody = request.body?.let {
            val buffer = Buffer()
            it.writeTo(buffer)
            buffer.readUtf8()
        } ?: ""

        val xmlResponse = when {
            soapAction?.contains("ProcessImage") == true || requestBody.contains("ProcessImage") -> {
                pollCounter = 0
                """<?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                   <soap:Body>
                      <ProcessImageResponse>
                         <id>soap_789</id>
                         <status>processing</status>
                      </ProcessImageResponse>
                   </soap:Body>
                </soap:Envelope>"""
            }
            soapAction?.contains("CheckStatus") == true || requestBody.contains("CheckStatus") -> {
                pollCounter++
                val status = if (pollCounter < 3) "processing" else "completed"
                val result = if (status == "completed") """{"calories": 600, "items": ["SOAP_meat"]}""" else ""

                """<?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                   <soap:Body>
                      <CheckStatusResponse>
                         <status>$status</status>
                         <result>$result</result>
                      </CheckStatusResponse>
                   </soap:Body>
                </soap:Envelope>"""
            }
            else -> """<error>Unknown SOAP Action</error>"""
        }
        return createResponse(xmlResponse, "text/xml")
    }

    private fun createResponse(content: String, contentType: String): Response {
        return Response.Builder()
            .code(200)
            .message("OK")
            .request(Request.Builder().url("https://fake.com").build())
            .protocol(Protocol.HTTP_1_1)
            .body(content.toResponseBody(contentType.toMediaType()))
            .addHeader("content-type", contentType)
            .build()
    }
}