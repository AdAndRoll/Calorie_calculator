package ru.vasilev.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.vasilev.domain.model.ImageStatus
import ru.vasilev.domain.model.ProtocolType

interface ImageRepository {
    suspend fun processImage(
        imageBytes: ByteArray,
        description: String,
        protocol: ProtocolType
    ): Flow<ImageStatus>
}