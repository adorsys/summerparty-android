package de.adorsys.android.shared.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.preference.PreferenceManager
import android.util.Log
import android.media.ExifInterface
import com.google.firebase.firestore.DocumentSnapshot
import de.adorsys.android.shared.FirebaseProvider
import java.io.File

object ImageUtils {
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
            val orientation = when {
                orientationString != null -> Integer.parseInt(orientationString)
                else -> ExifInterface.ORIENTATION_NORMAL
            }

            val rotationAngle = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90F
                ExifInterface.ORIENTATION_ROTATE_180 -> 180F
                ExifInterface.ORIENTATION_ROTATE_270 -> 270F
                else -> 0F
            }

            val bitmap = BitmapFactory.decodeFile(filePath, options)

            val matrix = Matrix()
            matrix.setRotate(rotationAngle, (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat())

            Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true)
        } catch (e: Exception) {
            Log.e(javaClass.name, e.message)
            null
        }
    }

    @SuppressLint("ApplySharedPref") // operation already in background thread
    fun getImageFile(context: Context, reference: String?, snapshot: DocumentSnapshot?, onReadyAction: (file: File) -> Unit) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val cachedFilePath = preferences.getString(reference, null)
        val cachedFile =
                if (cachedFilePath == null) {
                    null
                } else {
                    File(cachedFilePath)
                }

        if (cachedFile != null && cachedFile.exists()) {
            onReadyAction(cachedFile)
        } else {
            FirebaseProvider.downloadImage(
                    reference,
                    { file ->
                        // Store the file path to the reference in the sharedPreferences
                        preferences.edit().putString(reference, file.path).commit()
                        onReadyAction(file)
                    },
                    {
                        Log.e("TAG_THINGS", "Could not correctly decode bitmap from $snapshot")
                    })
        }
    }
}