package com.ghostbug.memorylane.features.capture.data.repositoryImpl.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

suspend fun compressImage(
    context: Context,
    uri: Uri,
    maxDimension: Int = 1920,
    quality: Int = 85
): ByteArray = withContext(Dispatchers.IO) {
    // 1. Read EXIF Orientation
    val exif = try {
        context.contentResolver.openInputStream(uri)?.use { ExifInterface(it) }
    } catch (e: Exception) { null }
    val orientation = exif?.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    ) ?: ExifInterface.ORIENTATION_UNDEFINED

    // 2. Decode image bounds first (0 memory allocated)
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, options)
    }

    val width = options.outWidth
    val height = options.outHeight

    // 3. Calculate sample size (downsampling)
    var inSampleSize = 1
    if (width > maxDimension || height > maxDimension) {
        val halfWidth = width / 2
        val halfHeight = height / 2
        while ((halfWidth / inSampleSize) >= maxDimension && (halfHeight / inSampleSize) >= maxDimension) {
            inSampleSize *= 2
        }
    }

    // 4. Decode downsampled Bitmap
    val decodeOptions = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
    var bitmap = context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, decodeOptions)
    } ?: error("Failed to decode bitmap")

    // 5. Apply EXIF rotation matrix
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }
    if (!matrix.isIdentity) {
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated != bitmap) {
            bitmap.recycle()
            bitmap = rotated
        }
    }

    // 6. Compress to JPEG ByteArray at 85% quality
    ByteArrayOutputStream().use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        bitmap.recycle()
        outputStream.toByteArray()
    }
}