package ru.vasilev.domain.model


sealed class ImageStatus {
    // 1. Ничего не происходит
    object Idle : ImageStatus()

    // 2. Идет физическая загрузка байтов на сервер (ProgressBar)
    object Uploading : ImageStatus()

    // 3. Файл на сервере, опрашиваем статус раз в секунду (Пункт 2.4.2 ТЗ)
    // Можно передать текст типа "Ожидание ответа... 5с"
    data class Polling(val retryCount: Int) : ImageStatus()

    // 4. Успех (Пункт 2.1 ТЗ - JSON + Изображение)
    data class Success(
        val jsonResponse: String,
        val imageUri: String
    ) : ImageStatus()

    // 5. Ошибка (Пункт 3.2 ТЗ - сетевые сбои, таймауты)
    data class Error(val message: String) : ImageStatus()
}