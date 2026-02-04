package ru.vasilev.data.grpc

import io.grpc.stub.StreamObserver

/**
 * Фейковая реализация gRPC сервиса для тестов
 */
class FakeImageService : ImageServiceGrpc.ImageServiceImplBase() {

    override fun processImage(request: ImageRequest, responseObserver: StreamObserver<ImageResponse>) {
        // Имитируем успешный прием изображения
        val response = ImageResponse.newBuilder()
            .setId("grpc_fake_123")
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override fun watchStatus(request: StatusRequest, responseObserver: StreamObserver<StatusResponse>) {
        // Имитируем стриминг статуса (ТЗ 2.4.2)

        // 1. Отправляем "обработка"
        responseObserver.onNext(
            StatusResponse.newBuilder()
                .setStatus("processing")
                .build()
        )

        // 2. Отправляем финальный результат
        val finalResult = """{"calories": 520, "items": ["gRPC_cheese"]}"""
        responseObserver.onNext(
            StatusResponse.newBuilder()
                .setStatus("completed")
                .setResult(finalResult)
                .build()
        )

        responseObserver.onCompleted()
    }
}