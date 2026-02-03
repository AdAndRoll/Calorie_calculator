package ru.vasilev.data.network.soap

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import ru.vasilev.data.network.soap.dto.CheckStatusRequestEnvelope
import ru.vasilev.data.network.soap.dto.CheckStatusResponseEnvelope
import ru.vasilev.data.network.soap.dto.ProcessImageRequestEnvelope
import ru.vasilev.data.network.soap.dto.ProcessImageResponseEnvelope

interface SoapApi {

    /**
     * Отправка изображения на сервер в формате SOAP.
     * Согласно ТЗ 2.4.1, используется Base64 кодирование внутри XML.
     */
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: http://fake-api-soap.com/ProcessImage"
    )
    @POST("service/soap")
    suspend fun processImage(
        @Body envelope: ProcessImageRequestEnvelope
    ): ProcessImageResponseEnvelope

    /**
     * Опрос статуса обработки изображения (Polling).
     * Согласно ТЗ 2.4.2, выполняется до получения статуса "completed".
     */
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: http://fake-api-soap.com/CheckStatus"
    )
    @POST("service/soap")
    suspend fun checkStatus(
        @Body envelope: CheckStatusRequestEnvelope
    ): CheckStatusResponseEnvelope
}