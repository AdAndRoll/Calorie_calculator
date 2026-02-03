package ru.vasilev.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCompressor @Inject constructor(
    private val context: Context
) {

    companion object {
        private const val MAX_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB по ТЗ
        private const val INITIAL_QUALITY = 90
        private const val MIN_QUALITY = 20
        private const val STEP_QUALITY = 10
    }

    /**
     * Точка входа для Uri (Галерея)
     */
    fun compressFromUri(uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream) ?: return null
                compress(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Точка входа для Bitmap (Камера)
     */
    fun compressFromBitmap(bitmap: Bitmap): ByteArray? {
        return compress(bitmap)
    }

    /**
     * Основная логика сжатия и оптимизации (Пункт 2.3 ТЗ)
     */
    private fun compress(bitmap: Bitmap): ByteArray? {
        var currentBitmap = bitmap
        var quality = INITIAL_QUALITY
        val outputStream = ByteArrayOutputStream()

        try {
            // 1. Первая попытка сжатия
            currentBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            var result = outputStream.toByteArray()

            // 2. Если размер превышен, сначала агрессивно уменьшаем разрешение (Scale)
            // Это эффективнее, чем просто снижать качество до 0
            if (result.size > MAX_SIZE_BYTES) {
                currentBitmap = scaleDown(currentBitmap, 2000) // ограничиваем сторону 2000px
            }

            // 3. Цикл оптимизации качества (Пункт 2.3 ТЗ)
            while (result.size > MAX_SIZE_BYTES && quality > MIN_QUALITY) {
                outputStream.reset()
                quality -= STEP_QUALITY
                currentBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                result = outputStream.toByteArray()
            }

            return if (result.size <= MAX_SIZE_BYTES) result else null

        } finally {
            outputStream.close()
        }
    }

    /**
     * Оптимизация: уменьшение физического размера изображения
     */
    private fun scaleDown(realBitmap: Bitmap, maxResolution: Int): Bitmap {
        val ratio = realBitmap.width.toFloat() / realBitmap.height.toFloat()
        val width: Int
        val height: Int

        if (realBitmap.width > realBitmap.height) {
            width = maxResolution
            height = (maxResolution / ratio).toInt()
        } else {
            height = maxResolution
            width = (maxResolution * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(realBitmap, width, height, true)
    }
}