package ru.vasilev.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.vasilev.data.network.rest.RestApi
import ru.vasilev.domain.model.ImageStatus
import ru.vasilev.domain.model.ProtocolType
import ru.vasilev.domain.repository.ImageRepository
import javax.inject.Inject

class RestRepositoryImpl @Inject constructor(
    private val api: RestApi
) : ImageRepository {

    override suspend fun processImage(
        imageBytes: ByteArray,
        description: String,
        protocol: ProtocolType
    ): Flow<ImageStatus> = flow {

        // 1. Начало загрузки
        emit(ImageStatus.Uploading)

        try {
            // Подготовка Multipart (Пункт 2.3 ТЗ)
            val requestFile = imageBytes.toRequestBody("image/jpeg".toMediaType())
            val body = MultipartBody.Part.createFormData("image", "upload.jpg", requestFile)

            // Метаданные как RequestBody (JSON по ТЗ)
            val metadata = """{"description":"$description"}""".toRequestBody("application/json".toMediaType())

            // 2. Выполняем запрос
            val uploadResponse = api.uploadImage(body, metadata)
            val requestId = uploadResponse.id

            // 3. Запускаем Polling (Пункт 2.4.2 ТЗ: интервал 1 сек)
            var isCompleted = false
            var retryCount = 0

            while (!isCompleted) {
                emit(ImageStatus.Polling(retryCount))

                val statusUpdate = api.checkStatus(requestId)

                if (statusUpdate.status == "completed") {
                    emit(ImageStatus.Success(
                        jsonResponse = statusUpdate.result ?: "{}",
                        imageUri = "" // Можно добавить логику получения ссылки
                    ))
                    isCompleted = true
                } else {
                    delay(1000) // Ждем 1 секунду перед следующим опросом
                    retryCount++
                }

                // Опционально: добавить проверку на таймаут (например, 5 минут)
                if (retryCount > 300) throw Exception("Timeout")
            }

        } catch (e: Exception) {
            emit(ImageStatus.Error(e.localizedMessage ?: "Unknown error"))
        }
    }
}