package ru.vasilev.calorie_calculator.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.ByteArrayOutputStream

fun Uri.toRawBytes(context: Context): ByteArray? {
    return context.contentResolver.openInputStream(this)?.use { it.readBytes() }
}

fun Bitmap.toRawBytes(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    return stream.toByteArray()
}