package ru.vasilev.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.vasilev.domain.model.ImageStatus
import ru.vasilev.domain.model.ProtocolType
import ru.vasilev.domain.repository.ImageRepository
import ru.vasilev.util.retryWithExponentialBackoff
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val restRepository: RestRepositoryImpl,
    private val soapRepository: SoapRepositoryImpl,
    private val grpcRepository: GrpcRepositoryImpl
) : ImageRepository {

    override suspend fun processImage(
        imageBytes: ByteArray,
        description: String,
        protocol: ProtocolType
    ): Flow<ImageStatus> {

        // Выбираем источник данных
        val sourceFlow = when (protocol) {
            ProtocolType.REST -> restRepository.processImage(imageBytes, description, protocol)
            ProtocolType.SOAP -> soapRepository.processImage(imageBytes, description, protocol)
            ProtocolType.GRPC -> grpcRepository.processImage(imageBytes, description, protocol)
        }

        return sourceFlow
            // 1. Применяем логику повторов (1с, 2с, 4с)
            .retryWithExponentialBackoff()
            // 2. Выполняем всю вышестоящую работу (сеть, delay, логи) в фоновом потоке
            .flowOn(Dispatchers.IO)
    }
}