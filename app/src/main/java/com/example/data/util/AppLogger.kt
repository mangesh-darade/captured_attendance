package com.example.data.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Persistent File Logger for capturing API errors, network failures, and unhandled crashes.
 * Automatically saves error history to a local log file on the Android device
 * so errors can be inspected even without USB debugging / Logcat.
 */
object AppLogger {

    private const val TAG = "ElintOM_App"
    private const val LOG_FILE_NAME = "app_error.log"
    private const val MAX_FILE_SIZE_BYTES = 1024 * 1024 // 1 MB limit

    private var logFile: File? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    @Synchronized
    fun init(context: Context) {
        if (logFile != null) return
        try {
            // Prefer external files dir so user/developer can access it via device file manager, fallback to internal
            val baseDir = context.getExternalFilesDir(null) ?: context.filesDir
            val logsDir = File(baseDir, "logs")
            if (!logsDir.exists()) {
                logsDir.mkdirs()
            }
            logFile = File(logsDir, LOG_FILE_NAME)

            // Setup uncaught exception handler
            val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                e("CrashHandler", "Uncaught exception in thread ${thread.name}", throwable)
                previousHandler?.uncaughtException(thread, throwable)
            }

            i("AppLogger", "Logger initialized. Path: ${logFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize file logger", e)
        }
    }

    fun i(tag: String, message: String) {
        Log.i(tag, message)
        writeToFile("INFO", tag, message)
    }

    fun w(tag: String, message: String) {
        Log.w(tag, message)
        writeToFile("WARN", tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
        val fullMsg = if (throwable != null) {
            "$message\n${Log.getStackTraceString(throwable)}"
        } else {
            message
        }
        writeToFile("ERROR", tag, fullMsg)
    }

    fun logHttpError(url: String, method: String, statusCode: Int, responseBody: String?, durationMs: Long? = null) {
        val timing = if (durationMs != null) " (${durationMs}ms)" else ""
        val preview = responseBody?.take(500) ?: "Empty/Null body"
        val message = "HTTP $statusCode on $method $url$timing\nResponse Body: $preview"
        writeToFile("HTTP_ERR", "Network", message)
        Log.e("Network", message)
    }

    @Synchronized
    private fun writeToFile(level: String, tag: String, message: String) {
        val file = logFile ?: return
        try {
            // Rotate if file is larger than 1MB
            if (file.exists() && file.length() > MAX_FILE_SIZE_BYTES) {
                val backup = File(file.parentFile, "app_error.old.log")
                if (backup.exists()) backup.delete()
                file.renameTo(backup)
            }

            val timestamp = dateFormat.format(Date())
            FileWriter(file, true).use { fw ->
                PrintWriter(fw).use { pw ->
                    pw.println("[$timestamp] [$level] [$tag]: $message")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Could not write to log file", e)
        }
    }

    @Synchronized
    fun getRecentLogs(maxLines: Int = 100): String {
        val file = logFile ?: return "Log file not initialized."
        if (!file.exists()) return "No logs recorded yet."
        return try {
            val lines = file.readLines()
            lines.takeLast(maxLines).joinToString("\n")
        } catch (e: Exception) {
            "Failed to read logs: ${e.message}"
        }
    }

    fun getLogFilePath(): String {
        return logFile?.absolutePath ?: "Unavailable"
    }

    @Synchronized
    fun clearLogs() {
        try {
            logFile?.delete()
            writeToFile("INFO", "AppLogger", "Logs cleared by user")
        } catch (_: Exception) {}
    }
}
