package ru.vasilev.data.repository

import android.util.Base64
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.vasilev.data.network.soap.SoapApi
import ru.vasilev.data.network.soap.dto.*
import ru.vasilev.domain.model.ImageStatus
import ru.vasilev.domain.model.ProtocolType
import ru.vasilev.domain.repository.ImageRepository
import javax.inject.Inject

class SoapRepositoryImpl @Inject constructor(
    private val soapApi: SoapApi
) : ImageRepository {

    override suspend fun processImage(
        imageBytes: ByteArray,
        description: String,
        protocol: ProtocolType
    ): Flow<ImageStatus> = flow {

        emit(ImageStatus.Uploading)

        try {
            // 1. Кодируем изображение в Base64 (ТЗ 2.4.1)
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

            // 2. Создаем Envelope для отправки
            val requestEnvelope = ProcessImageRequestEnvelope(
                body = ProcessImageRequestBody(
                    request = ProcessImageRequest(
                        imageBase64 = base64Image,
                        description = description
                    )
                )
            )

            // 3. Отправляем запрос
            val responseEnvelope = soapApi.processImage(requestEnvelope)
            val requestId = responseEnvelope.body.response.id

            // 4. Polling (Опрос статуса)
            var isCompleted = false
            var retryCount = 0

            while (!isCompleted) {
                emit(ImageStatus.Polling(retryCount))

                val statusEnvelope = soapApi.checkStatus(
                    CheckStatusRequestEnvelope(
                        body = CheckStatusRequestBody(
                            request = CheckStatusRequest(id = requestId)
                        )
                    )
                )

                val response = statusEnvelope.body.response

                if (response.status == "completed") {
                    emit(ImageStatus.Success(
                        jsonResponse = response.result ?: "{}",
                        imageUri = ""
                    ))
                    isCompleted = true
                } else {
                    delay(1000) // Задержка 1 сек по ТЗ
                    retryCount++
                }

                if (retryCount > 300) throw Exception("SOAP Timeout")
            }

        } catch (e: Exception) {
            emit(ImageStatus.Error("SOAP Error: ${e.localizedMessage}"))
        }
    }
}