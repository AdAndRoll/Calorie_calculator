package ru.vasilev.data.repository

import kotlinx.coroutines.flow.Flow
import ru.vasilev.domain.model.ImageStatus
import ru.vasilev.domain.model.ProtocolType
import ru.vasilev.domain.repository.ImageRepository
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val restRepository: RestRepositoryImpl,
    private val soapRepository: SoapRepositoryImpl
) : ImageRepository {

    override suspend fun processImage(
        imageBytes: ByteArray,
        description: String,
        protocol: ProtocolType
    ): Flow<ImageStatus> {
        // Выбираем нужную стратегию в зависимости от протокола
        return when (protocol) {
            ProtocolType.REST -> restRepository.processImage(imageBytes, description, protocol)
            ProtocolType.SOAP -> soapRepository.processImage(imageBytes, description, protocol)
            ProtocolType.GRPC -> TODO()
        }
    }
}
