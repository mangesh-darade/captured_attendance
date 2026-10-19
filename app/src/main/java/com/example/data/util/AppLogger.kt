package com.example.data.util

import android.content.Context
import android.os.Build
import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Persistent File Logger & Remote Sync for Android Kiosk.
 * - Saves complete error & diagnostic history to local app_error.log (1MB rotating)
 * - Automatically uploads network/API failures, exceptions, and crashes to Web POS
 *   (POST attendance_api/client_log) so administrators can inspect issues from the web.
 */
object AppLogger {

    private const val TAG = "ElintOM_App"
    private const val LOG_FILE_NAME = "app_error.log"
    private const val MAX_FILE_SIZE_BYTES = 1024 * 1024 // 1 MB limit

    private var logFile: File? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    @Volatile
    var activeServerUrl: String? = null
        private set

    @Volatile
    var activeAndroidCode: String? = null
        private set

    val deviceModel: String = "${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT})"
    const val appVersion: String = "1.0.0 (1)"

    // Dedicated lightweight HTTP client with 5s timeout for log sync (no recursion interceptor)
    private val syncHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .writeTimeout(6, TimeUnit.SECONDS)
            .build()
    }

    @Synchronized
    fun init(context: Context) {
        if (logFile != null) return
        try {
            val baseDir = context.getExternalFilesDir(null) ?: context.filesDir
            val logsDir = File(baseDir, "logs")
            if (!logsDir.exists()) {
                logsDir.mkdirs()
            }
            logFile = File(logsDir, LOG_FILE_NAME)

            // Setup uncaught exception handler
            val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                val stack = Log.getStackTraceString(throwable)
                e("CrashHandler", "Uncaught exception in thread ${thread.name}: ${throwable.message}", throwable)

                // Attempt quick sync before termination
                try {
                    reportToServerSync(
                        level = "CRASH",
                        tag = "CrashHandler",
                        message = "Uncaught crash in thread [${thread.name}]: ${throwable.message}",
                        details = stack
                    )
                } catch (_: Exception) {}

                previousHandler?.uncaughtException(thread, throwable)
            }

            i("AppLogger", "Logger initialized. Path: ${logFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize file logger", e)
        }
    }

    fun setServerContext(url: String?, code: String?) {
        if (!url.isNullOrBlank()) {
            activeServerUrl = url.trim().trimEnd('/')
        }
        if (!code.isNullOrBlank()) {
            activeAndroidCode = code.trim()
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

        // Asynchronously report application errors to Web POS
        reportToServerAsync(
            level = "ERROR",
            tag = tag,
            message = message,
            details = if (throwable != null) Log.getStackTraceString(throwable) else null
        )
    }

    fun logHttpError(url: String, method: String, statusCode: Int, responseBody: String?, durationMs: Long? = null) {
        if (url.contains("client_log")) return // Prevent infinite logging loop

        val timing = if (durationMs != null) " (${durationMs}ms)" else ""
        val preview = responseBody?.take(1000) ?: "Empty/Null body"
        val message = "HTTP $statusCode on $method $url$timing"
        val details = "Response Body:\n$preview"

        writeToFile("HTTP_ERR", "Network", "$message\n$details")
        Log.e("Network", "$message\n$details")

        // Try to derive base server URL from the failing request if activeServerUrl not yet set
        val targetBase = activeServerUrl ?: try {
            val uri = java.net.URI(url)
            "${uri.scheme}://${uri.host}${if (uri.port > 0 && uri.port != 80 && uri.port != 443) ":${uri.port}" else ""}"
        } catch (_: Exception) { null }

        reportToServerAsync(
            targetUrl = targetBase,
            level = "HTTP_ERR",
            tag = "Network",
            message = message,
            details = details
        )
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

    /**
     * Non-blocking background log report to Web POS backend (attendance_api/client_log)
     */
    fun reportToServerAsync(
        targetUrl: String? = activeServerUrl,
        code: String? = activeAndroidCode,
        level: String,
        tag: String,
        message: String,
        details: String? = null,
        onComplete: ((Boolean, String) -> Unit)? = null
    ) {
        val server = (targetUrl ?: activeServerUrl)?.trimEnd('/')
        if (server.isNullOrBlank()) {
            onComplete?.invoke(false, "No server URL configured yet")
            return
        }

        val endpoint = "$server/attendance_api/client_log"
        val formBody = FormBody.Builder()
            .add("android_code", code ?: activeAndroidCode ?: "")
            .add("device_name", deviceModel)
            .add("app_version", appVersion)
            .add("log_level", level)
            .add("tag", tag)
            .add("message", message)
            .add("details", details ?: "")
            .build()

        val request = Request.Builder()
            .url(endpoint)
            .post(formBody)
            .build()

        syncHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.w(TAG, "Remote log upload failed: ${e.message}")
                onComplete?.invoke(false, e.message ?: "Network error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        Log.i(TAG, "Remote log successfully recorded on server: $endpoint")
                        onComplete?.invoke(true, "Logs synced successfully with Web POS!")
                    } else {
                        Log.w(TAG, "Remote log HTTP ${it.code} from $endpoint")
                        onComplete?.invoke(false, "Server returned HTTP ${it.code}")
                    }
                }
            }
        })
    }

    /**
     * Synchronous log report (used during unhandled process crash before death)
     */
    private fun reportToServerSync(
        targetUrl: String? = activeServerUrl,
        code: String? = activeAndroidCode,
        level: String,
        tag: String,
        message: String,
        details: String? = null
    ) {
        val server = (targetUrl ?: activeServerUrl)?.trimEnd('/') ?: return
        val endpoint = "$server/attendance_api/client_log"
        val formBody = FormBody.Builder()
            .add("android_code", code ?: activeAndroidCode ?: "")
            .add("device_name", deviceModel)
            .add("app_version", appVersion)
            .add("log_level", level)
            .add("tag", tag)
            .add("message", message)
            .add("details", details ?: "")
            .build()

        val request = Request.Builder()
            .url(endpoint)
            .post(formBody)
            .build()

        try {
            syncHttpClient.newCall(request).execute().close()
        } catch (_: Exception) {}
    }

    /**
     * Upload full recent local logs buffer to the Web POS backend
     */
    fun syncAllRecentLogs(targetUrl: String? = null, code: String? = null, onComplete: (Boolean, String) -> Unit) {
        val recentLogs = getRecentLogs(150)
        reportToServerAsync(
            targetUrl = targetUrl ?: activeServerUrl,
            code = code ?: activeAndroidCode,
            level = "INFO",
            tag = "ClientDiagnostics",
            message = "Manual diagnostics sync from Android device",
            details = recentLogs,
            onComplete = onComplete
        )
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
