package com.example.face

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.os.SystemClock
import org.json.JSONArray
import java.io.InputStream
import java.util.concurrent.atomic.AtomicLong

data class FaceDetectionResult(
    val faceCount: Int,
    val score: Double,
    val descriptor: List<Float>,
    val alignedFace: Bitmap? = null
) {
    val isGoodFace: Boolean get() = faceCount == 1 && score >= 0.55 && descriptor.size == 128
}

class FaceDetectionBridge(
    private val onModelStatusChanged: (Boolean, String) -> Unit,
    private val onDetectionResultReceived: (FaceDetectionResult) -> Unit
) {
    private val frameSentAt = AtomicLong(0L)

    /** Skip another JPEG while face-api is still reading the previous frame. */
    fun canAcceptFrame(): Boolean {
        val sent = frameSentAt.get()
        if (sent == 0L) return true
        return SystemClock.elapsedRealtime() - sent > 1800L
    }

    fun markFrameSent() {
        frameSentAt.set(SystemClock.elapsedRealtime())
    }

    @JavascriptInterface
    fun onModelsLoaded(success: Boolean, message: String) {
        onModelStatusChanged(success, message)
    }

    @JavascriptInterface
    fun onDetectionResult(faceCount: Int, score: Double, descriptorJson: String, alignedJpegBase64: String) {
        frameSentAt.set(0L)
        val list = mutableListOf<Float>()
        try {
            val jsonArray = JSONArray(descriptorJson)
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getDouble(i).toFloat())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val aligned = decodeAlignedFace(alignedJpegBase64)
        onDetectionResultReceived(FaceDetectionResult(faceCount, score, list, aligned))
    }

    private fun decodeAlignedFace(base64: String): Bitmap? {
        if (base64.isBlank()) return null
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

class HiddenFaceWebViewManager(
    private val context: Context,
    private val bridge: FaceDetectionBridge
) {
    private var webView: WebView? = null
    var isDestroyed = false
        private set

    fun initialize(): WebView {
        val wv = WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.databaseEnabled = true

            addJavascriptInterface(bridge, "AndroidBridge")

            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    val url = request?.url ?: return null
                    val path = url.path ?: ""

                    val assetPath = when {
                        path.endsWith("face_detector.html") || path == "/" -> "face_detector.html"
                        path.endsWith("face-api.min.js") -> "face-api.min.js"
                        path.contains("models/") -> {
                            val idx = path.indexOf("models/")
                            path.substring(idx)
                        }
                        else -> null
                    }

                    if (assetPath != null) {
                        return try {
                            val stream: InputStream = context.assets.open(assetPath)
                            val mimeType = when {
                                assetPath.endsWith(".html") -> "text/html"
                                assetPath.endsWith(".js") -> "application/javascript"
                                assetPath.endsWith(".json") -> "application/json"
                                else -> "application/octet-stream"
                            }
                            val response = WebResourceResponse(mimeType, "UTF-8", stream)
                            response.responseHeaders = mapOf(
                                "Access-Control-Allow-Origin" to "*",
                                "Access-Control-Allow-Methods" to "GET, OPTIONS",
                                "Access-Control-Allow-Headers" to "*"
                            )
                            response
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }

                    return super.shouldInterceptRequest(view, request)
                }
            }

            loadUrl("https://appassets.local/face_detector.html")
        }
        this.webView = wv
        return wv
    }

    fun detectFrame(base64Jpeg: String) {
        if (isDestroyed) return
        webView?.post {
            webView?.evaluateJavascript("detectFace('$base64Jpeg');", null)
        }
    }

    fun destroy() {
        isDestroyed = true
        webView?.post {
            webView?.removeJavascriptInterface("AndroidBridge")
            webView?.destroy()
            webView = null
        }
    }
}
