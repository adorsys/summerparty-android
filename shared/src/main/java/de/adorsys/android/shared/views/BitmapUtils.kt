package de.adorsys.android.shared.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log

object BitmapUtils {
    fun getScaledImage(size: Float, filePath: String): Bitmap? {
        return try {
            // Get the dimensions of the bitmap
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)
            val photoW = options.outWidth
            val photoH = options.outHeight

            // Determine how much to scale down the image
            val scaleFactor = Math.min(photoW / size, photoH / size)

            // Decode the image file into a Bitmap sized to fill the View
            options.inJustDecodeBounds = false
            options.inSampleSize = Math.round(scaleFactor)

            val exif = ExifInterface(filePath)
            val orientationString = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
            val orientation = if (orientationString != null) {
                Integer.parseInt(orientationString)
            } else {
                ExifInterface.ORIENTATION_NORMAL
            }

            var rotationAngle = 0F
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90F
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180F
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270F

            val bitmap = BitmapFactory.decodeFile(filePath, options)

            val matrix = Matrix()
            matrix.setRotate(rotationAngle, (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat())

            Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true)
        } catch (e: Exception) {
            Log.e(javaClass.name, e.message)
            null
        }
    }
}