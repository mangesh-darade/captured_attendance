package com.example.ui

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Base64
import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.BorderCamera
import com.example.ui.theme.CameraBackground
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

@Composable
fun CameraPreviewView(
    modifier: Modifier = Modifier,
    isCameraActive: Boolean,
    onCameraStarted: () -> Unit,
    onCameraFailed: (String) -> Unit,
    onFrameCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(CameraBackground)
            .border(1.dp, BorderCamera, RoundedCornerShape(12.dp))
            .testTag("camera_preview")
    ) {
        if (isCameraActive) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }

                            var lastAnalyzedTime = 0L

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                                .build()

                            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                val now = SystemClock.elapsedRealtime()
                                if (now - lastAnalyzedTime >= 900L) {
                                    lastAnalyzedTime = now
                                    try {
                                        val bitmap = imageProxy.toBitmap()
                                        val rotation = imageProxy.imageInfo.rotationDegrees
                                        val matrix = Matrix().apply {
                                            if (rotation != 0) postRotate(rotation.toFloat())
                                        }
                                        val rotated = Bitmap.createBitmap(
                                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                                        )

                                        // Downscale to target width 360, maintaining aspect ratio
                                        val targetW = 360
                                        val targetH = (targetW * (rotated.height.toFloat() / rotated.width)).toInt()
                                        val scaled = Bitmap.createScaledBitmap(rotated, targetW, targetH, true)

                                        val byteStream = ByteArrayOutputStream()
                                        scaled.compress(Bitmap.CompressFormat.JPEG, 65, byteStream)
                                        val base64 = Base64.encodeToString(byteStream.toByteArray(), Base64.NO_WRAP)

                                        onFrameCaptured(base64)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                imageProxy.close()
                            }

                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                                .build()

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                            onCameraStarted()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            onCameraFailed(e.message ?: "Camera binding failed")
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )
        }
    }
}
