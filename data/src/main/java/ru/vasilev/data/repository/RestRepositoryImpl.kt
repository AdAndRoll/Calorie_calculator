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

        // 1. Статус начала
        emit(ImageStatus.Uploading)

        // Подготовка данных
        val requestFile = imageBytes.toRequestBody("image/jpeg".toMediaType())
        val body = MultipartBody.Part.createFormData("image", "upload.jpg", requestFile)
        val metadata = """{"description":"$description"}""".toRequestBody("application/json".toMediaType())

        // 2. Выполняем запрос (без try-catch здесь!)
        // Если API выкинет IOException (нет сети) или 500 ошибку,
        // Flow прервется и сработает наш внешний Retry Backoff.
        val uploadResponse = api.uploadImage(body, metadata)
        val requestId = uploadResponse.id

        // 3. Polling
        var isCompleted = false
        var retryCount = 0

        while (!isCompleted) {
            emit(ImageStatus.Polling(retryCount))

            // Опрос статуса
            val statusUpdate = api.checkStatus(requestId)

            if (statusUpdate.status == "completed") {
                emit(ImageStatus.Success(
                    jsonResponse = statusUpdate.result ?: "{}",
                    imageUri = ""
                ))
                isCompleted = true
            } else {
                delay(1000)
                retryCount++
            }

            // Защитный таймаут опроса
            if (retryCount > 300) throw Exception("Превышено время ожидания обработки (300c) ")
        }
    }
}