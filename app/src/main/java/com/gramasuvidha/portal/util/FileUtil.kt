package com.gramasuvidha.portal.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object FileUtil {
    private const val TAG = "FileUtil"

    /**
     * Copies an image from a temporary URI to the app's internal storage
     * and returns the permanent "file://" URI string.
     */
    fun saveImageToInternalStorage(context: Context, uriString: String): String {
        if (uriString.isBlank() || !uriString.startsWith("content://")) return uriString
        
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return uriString
            val fileName = "proj_img_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}.jpg"
            val file = File(context.filesDir, fileName)
            
            FileOutputStream(file).use { outputStream ->
                inputStream.use { input ->
                    input.copyTo(outputStream)
                }
            }
            val fileUri = Uri.fromFile(file).toString()
            Log.d(TAG, "Image saved successfully to: $fileUri")
            fileUri
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image", e)
            uriString
        }
    }
}
