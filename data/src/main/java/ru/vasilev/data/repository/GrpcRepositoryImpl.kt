package ru.vasilev.data.repository

import com.google.protobuf.ByteString
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.vasilev.data.grpc.*
import ru.vasilev.domain.model.ImageStatus
import ru.vasilev.domain.model.ProtocolType
import ru.vasilev.domain.repository.ImageRepository
import java.util.concurrent.TimeUnit // Добавили для таймаутов
import javax.inject.Inject

class GrpcRepositoryImpl @Inject constructor(
    private val stub: ImageServiceGrpc.ImageServiceStub
) : ImageRepository {

    override suspend fun processImage(
        imageBytes: ByteArray,
        description: String,
        protocol: ProtocolType
    ): Flow<ImageStatus> = callbackFlow {

        // Отправляем начальный статус
        trySend(ImageStatus.Uploading)

        val request = ImageRequest.newBuilder()
            .setImageData(ByteString.copyFrom(imageBytes))
            .setDescription(description)
            .build()

        stub.withDeadlineAfter(10, TimeUnit.SECONDS)
            .processImage(request, object : StreamObserver<ImageResponse> {
                override fun onNext(response: ImageResponse) {
                    observeStatusStream(response.id, this@callbackFlow)
                }

                override fun onError(t: Throwable) {
                    // Мы не шлем ImageStatus.Error здесь, чтобы не пугать UI раньше времени.
                    // Просто закрываем поток с ошибкой для срабатывания retry.
                    close(t)
                }

                override fun onCompleted() {}
            })

        awaitClose { }
    }

    private fun observeStatusStream(requestId: String, scope: ProducerScope<ImageStatus>) {
        val statusRequest = StatusRequest.newBuilder().setId(requestId).build()

        stub.withDeadlineAfter(300, TimeUnit.SECONDS)
            .watchStatus(statusRequest, object : StreamObserver<StatusResponse> {
                var attempt = 0

                override fun onNext(value: StatusResponse) {
                    when (value.status) {
                        "completed" -> {
                            scope.trySend(ImageStatus.Success(value.result, "gRPC Stream Result"))
                            scope.close()
                        }
                        "processing" -> {
                            scope.trySend(ImageStatus.Polling(attempt++))
                        }
                        else -> {
                            // Если статус бизнес-логики (не сети), можно закрыть без ретрая
                            scope.close(Exception("Unknown status: ${value.status}"))
                        }
                    }
                }

                override fun onError(t: Throwable) {
                    // Ошибка стрима — тоже повод для ретрая
                    scope.close(t)
                }

                override fun onCompleted() {
                    scope.close()
                }
            })
    }
}