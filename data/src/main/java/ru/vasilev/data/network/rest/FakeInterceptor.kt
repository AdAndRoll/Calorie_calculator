package ru.vasilev.data.network.rest

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

class FakeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val soapAction = request.header("SOAPAction")

        return when {
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
            url.contains("/api/process") -> """{"id": "rest_123"}"""
            url.contains("/api/status/") -> """{
                "status": "completed", 
                "result": "{\"calories\": 450, \"items\": [\"REST_bread\"]}"
            }"""
            else -> """{"error": "not found"}"""
        }
        return createResponse(jsonResponse, "application/json")
    }

    private fun handleSoapRequest(request: Request, soapAction: String?): Response {
        // Читаем тело запроса, чтобы понять какой метод вызван, если SOAPAction пустой
        val requestBody = request.body?.let {
            val buffer = Buffer()
            it.writeTo(buffer)
            buffer.readUtf8()
        } ?: ""

        val xmlResponse = when {
            soapAction?.contains("ProcessImage") == true || requestBody.contains("ProcessImage") -> {
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
                """<?xml version="1.0" encoding="utf-8"?>
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                   <soap:Body>
                      <CheckStatusResponse>
                         <status>completed</status>
                         <result>{"calories": 600, "items": ["SOAP_meat"]}</result>
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
            .request(Request.Builder().url("https://fake.com").build()) // Заглушка
            .protocol(Protocol.HTTP_1_1)
            .body(content.toResponseBody(contentType.toMediaType()))
            .addHeader("content-type", contentType)
            .build()
    }
}