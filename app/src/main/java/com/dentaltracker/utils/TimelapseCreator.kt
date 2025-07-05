package com.dentaltracker.utils

import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.dentaltracker.data.DentalPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TimelapseCreator(private val context: Context) {
    
    suspend fun createTimelapse(
        photos: List<DentalPhoto>,
        onProgress: (Int) -> Unit = {},
        onComplete: (File?) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            if (photos.isEmpty()) {
                withContext(Dispatchers.Main) {
                    onComplete(null)
                }
                return@withContext
            }
            
            // Create temporary directory for processed frames
            val tempDir = File(context.cacheDir, "timelapse_frames")
            tempDir.mkdirs()
            
            // Copy and rename files for FFmpeg processing
            photos.forEachIndexed { index, photo ->
                val sourceFile = File(photo.filePath)
                val destFile = File(tempDir, String.format("frame_%04d.jpg", index))
                sourceFile.copyTo(destFile, overwrite = true)
                
                withContext(Dispatchers.Main) {
                    onProgress((index * 50) / photos.size) // First 50% for file preparation
                }
            }
            
            // Create output file
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val outputDir = File(context.getExternalFilesDir("Movies"), "DentalTracker")
            outputDir.mkdirs()
            val outputFile = File(outputDir, "timelapse_$timeStamp.mp4")
            
            // FFmpeg command for creating timelapse
            val command = "-framerate 2 -i ${tempDir.absolutePath}/frame_%04d.jpg " +
                    "-c:v libx264 -r 30 -pix_fmt yuv420p -y ${outputFile.absolutePath}"
            
            FFmpegKit.execute(command) { session ->
                val returnCode = session.returnCode
                
                // Clean up temporary files
                tempDir.deleteRecursively()
                
                withContext(Dispatchers.Main) {
                    if (ReturnCode.isSuccess(returnCode)) {
                        onProgress(100)
                        onComplete(outputFile)
                    } else {
                        onComplete(null)
                    }
                }
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onComplete(null)
            }
        }
    }
}