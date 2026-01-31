package ru.vasilev.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.vasilev.domain.model.ImageStatus
import ru.vasilev.domain.model.ProtocolType
import ru.vasilev.domain.repository.ImageRepository
import javax.inject.Inject

class ProcessImageUseCase @Inject constructor(
    private val repository: ImageRepository
) {
    suspend operator fun invoke(
        imageBytes: ByteArray,
        description: String,
        protocol: ProtocolType
    ): Flow<ImageStatus> {
        return repository.processImage(imageBytes, description, protocol)
    }
}