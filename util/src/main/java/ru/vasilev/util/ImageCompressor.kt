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
    /**
     * Сжимает изображение из Uri до ByteArray.
     * Реализует пункт 2.3 ТЗ: Оптимизация и лимит 10МБ.
     */
    fun compressImageWithLimit(uri: Uri, limitBytes: Long = 10 * 1024 * 1024): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // Декодируем изображение
                val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null

                var quality = 80
                var result: ByteArray
                val outputStream = ByteArrayOutputStream()

                // Сжимаем (пункт 2.3: Оптимизация)
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                result = outputStream.toByteArray()

                // Если вдруг файл все еще больше лимита, пробуем снизить качество (агрессивная оптимизация)
                while (result.size > limitBytes && quality > 20) {
                    outputStream.reset()
                    quality -= 10
                    originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    result = outputStream.toByteArray()
                }

                if (result.size <= limitBytes) result else null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}