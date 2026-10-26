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
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
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
import com.example.ui.theme.CameraBackground
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

/**
 * Front camera preview. Every 700 ms it sends a 720 px JPEG frame for face detection (only when the previous frame is done).
 */
@Composable
fun CameraPreviewView(
    modifier: Modifier = Modifier,
    isCameraActive: Boolean,
    onCameraStarted: () -> Unit,
    onCameraFailed: (String) -> Unit,
    canAcceptFrame: () -> Boolean = { true },
    onFrameCaptured: (String, Bitmap) -> Unit
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
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(20.dp))
            .background(CameraBackground)
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

                            val analysisResolution = ResolutionSelector.Builder()
                                .setResolutionStrategy(
                                    ResolutionStrategy(
                                        Size(1280, 720),
                                        ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                                    )
                                )
                                .build()

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setResolutionSelector(analysisResolution)
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                                .build()

                            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                try {
                                    val now = SystemClock.elapsedRealtime()
                                    if (!canAcceptFrame() || now - lastAnalyzedTime < 700L) {
                                        return@setAnalyzer
                                    }
                                    lastAnalyzedTime = now
                                    val bitmap = imageProxy.toBitmap()
                                    val rotation = imageProxy.imageInfo.rotationDegrees
                                    val upright = if (rotation == 0) {
                                        bitmap
                                    } else {
                                        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                                        Bitmap.createBitmap(
                                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                                        ).also { created ->
                                            if (created !== bitmap) bitmap.recycle()
                                        }
                                    }

                                    // Sharp enough for landmarks. 360px JPEG/65 was too soft to match a registered face.
                                    val maxEdge = 720
                                    val longest = maxOf(upright.width, upright.height)
                                    val output = if (longest > maxEdge) {
                                        val scale = maxEdge.toFloat() / longest
                                        Bitmap.createScaledBitmap(
                                            upright,
                                            (upright.width * scale).toInt().coerceAtLeast(1),
                                            (upright.height * scale).toInt().coerceAtLeast(1),
                                            true
                                        ).also { scaled ->
                                            if (scaled !== upright) upright.recycle()
                                        }
                                    } else {
                                        upright
                                    }

                                    val byteStream = ByteArrayOutputStream()
                                    output.compress(Bitmap.CompressFormat.JPEG, 80, byteStream)
                                    val base64 = Base64.encodeToString(byteStream.toByteArray(), Base64.NO_WRAP)
                                    onFrameCaptured(base64, output)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    imageProxy.close()
                                }
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
