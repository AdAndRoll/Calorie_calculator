package ru.vasilev.util

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException
import kotlin.math.pow

/**
 * Экспоненциальный бекофф для любого Flow.
 * @param maxRetries - количество попыток (по ТЗ - 3)
 * @param baseDelay - базовая задержка в мс (1000мс = 1с)
 */
private const val TAG = "RetryBackoff"

fun <T> Flow<T>.retryWithExponentialBackoff(
    maxRetries: Int = 3,
    baseDelay: Long = 1000
): Flow<T> = this.retryWhen { cause, attempt ->
    if (attempt < maxRetries) {
        // Вычисляем задержку
        val delayTime = baseDelay * 2.0.pow(attempt.toDouble()).toLong()

        Log.w(TAG, "Ошибка: ${cause.message}. Попытка №${attempt + 1}. Ждем ${delayTime}мс...")

        delay(delayTime)
        true // Перезапускаем поток
    } else {
        Log.e(TAG, "Все попытки ($maxRetries) исчерпаны. Ошибка: ${cause.message}")
        false // Пробрасываем ошибку дальше в UI
    }
}