package com.example.agritour.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun compressImage(context: Context, uri: Uri): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            // Calculate new dimensions (Max 800px width/height)
            val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
            val width = if (ratio > 1) 800 else (800 * ratio).toInt()
            val height = if (ratio > 1) (800 / ratio).toInt() else 800

            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true)
            val outputStream = ByteArrayOutputStream()

            // Compress quality to 75%
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            null
        }
    }
}