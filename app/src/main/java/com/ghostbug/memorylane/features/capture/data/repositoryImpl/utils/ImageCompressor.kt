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
    maxDimension: Int = 1080,
    quality: Int = 75
): ByteArray = withContext(Dispatchers.IO) {
    // 1. Read EXIF Orientation
    val exif = try {
        context.contentResolver.openInputStream(uri)?.use { ExifInterface(it) }
    } catch (e: Exception) { null }
    val orientation = exif?.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    ) ?: ExifInterface.ORIENTATION_UNDEFINED

    // 2. Decode bounds to get original dimensions
    val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, options)
    }

    val origWidth = options.outWidth
    val origHeight = options.outHeight

    // 3. Calculate inSampleSize power-of-2 downsampling
    var inSampleSize = 1
    if (origWidth > maxDimension || origHeight > maxDimension) {
        val max = maxOf(origWidth, origHeight)
        inSampleSize = Integer.highestOneBit(max / maxDimension)
        if (inSampleSize < 1) inSampleSize = 1
    }

    // 4. Decode downsampled Bitmap
    val decodeOptions = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
    var bitmap = context.contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, decodeOptions)
    } ?: error("Failed to decode bitmap")

    // 5. Apply EXIF rotation if photo was rotated
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

    // 6. Scale bitmap down to EXACT 1080px max dimension (Instagram style)
    val width = bitmap.width
    val height = bitmap.height
    val currentMax = maxOf(width, height)

    if (currentMax > maxDimension) {
        val scale = maxDimension.toFloat() / currentMax
        val targetWidth = (width * scale).toInt()
        val targetHeight = (height * scale).toInt()

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        if (scaledBitmap != bitmap) {
            bitmap.recycle()
            bitmap = scaledBitmap
        }
    }

    // 7. Compress JPEG at 75% quality
    ByteArrayOutputStream().use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        bitmap.recycle()
        outputStream.toByteArray()
    }
}