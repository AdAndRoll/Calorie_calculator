package ru.vasilev.data.grpc

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.vasilev.data.repository.GrpcRepositoryImpl
import ru.vasilev.domain.model.ImageStatus
import ru.vasilev.domain.model.ProtocolType

@RunWith(AndroidJUnit4::class)
class GrpcIntegrationTest {

    private lateinit var server: Server
    private lateinit var channel: ManagedChannel
    private lateinit var repository: GrpcRepositoryImpl
    private val serverName = InProcessServerBuilder.generateName()

    @Before
    fun setup() {
        // 1. Запускаем фейковый сервер в памяти
        server = InProcessServerBuilder
            .forName(serverName)
            .directExecutor()
            .addService(FakeImageService()) // Тот класс, что мы написали выше
            .build()
            .start()

        // 2. Создаем клиентский канал к этому серверу
        channel = InProcessChannelBuilder
            .forName(serverName)
            .directExecutor()
            .build()

        // 3. Инициализируем репозиторий со стабом
        val stub = ImageServiceGrpc.newStub(channel)
        repository = GrpcRepositoryImpl(stub)
    }

    @After
    fun teardown() {
        channel.shutdownNow()
        server.shutdownNow()
    }

    @Test
    fun testGrpcFlowFullCycle() = runBlocking {
        // Данные для теста
        val fakeBytes = byteArrayOf(1, 2, 3)
        val description = "Test image"

        // Собираем все состояния Flow в список
        val results = repository.processImage(fakeBytes, description, ProtocolType.GRPC).toList()

        // Проверяем цепочку статусов (ТЗ 2.4.2)
        // Ожидаем: Uploading -> Polling(0) -> Success
        assertTrue(results[0] is ImageStatus.Uploading)
        assertTrue(results[1] is ImageStatus.Polling)

        val lastResult = results.last()
        assertTrue(lastResult is ImageStatus.Success)

        val success = lastResult as ImageStatus.Success
        assertEquals("""{"calories": 520, "items": ["gRPC_cheese"]}""", success.jsonResponse)
    }
}