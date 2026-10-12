package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.R
import com.example.data.model.AttendanceButtonState
import com.example.data.model.ToastType
import com.example.face.FaceDetectionBridge
import com.example.face.HiddenFaceWebViewManager
import com.example.ui.theme.BorderBottomBar
import com.example.ui.theme.BorderDefault
import com.example.ui.theme.BorderToggle
import com.example.ui.theme.BrandActiveTint
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.HeaderBackground
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.PageBackground
import com.example.ui.theme.PanelBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSlate
import com.example.ui.theme.TextStatus
import com.example.ui.theme.TextWarning
import com.example.ui.theme.ToastErrorBg
import com.example.ui.theme.ToastErrorBorder
import com.example.ui.theme.ToastErrorText
import com.example.ui.theme.ToastInfoBg
import com.example.ui.theme.ToastInfoBorder
import com.example.ui.theme.ToastInfoText
import com.example.ui.theme.ToastSuccessBg
import com.example.ui.theme.ToastSuccessBorder
import com.example.ui.theme.ToastSuccessText

@Composable
fun AttendanceKioskScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Permissions launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locGranted = (permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false) ||
                (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false)

        if (cameraGranted) {
            viewModel.onCameraStarted()
        } else {
            viewModel.onCameraFailed("Camera permission denied.")
        }

        if (locGranted) {
            viewModel.triggerLocationAndDerive()
        } else {
            viewModel.showToast(ToastType.ERROR, "Location permission denied.", 5500L)
        }
    }

    LaunchedEffect(Unit) {
        val hasCam = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val hasFine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasCam || (!hasFine && !hasCoarse)) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.onCameraStarted()
            viewModel.triggerLocationAndDerive()
        }
    }

    // Hidden WebView for offline face-api.js 0.22.2 TinyFaceDetector + FaceLandmark68Net + FaceRecognitionNet
    val faceBridge = remember {
        FaceDetectionBridge(
            onModelStatusChanged = { success, msg ->
                viewModel.onFaceModelLoaded(success, msg)
            },
            onDetectionResultReceived = { result ->
                viewModel.onDetectionTick(result)
            }
        )
    }

    val webViewManager = remember {
        HiddenFaceWebViewManager(context, faceBridge)
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewManager.destroy()
        }
    }

    // Embed hidden WebView into the hierarchy so it runs WebGL/Canvas properly
    Box(modifier = Modifier.size(1.dp).alpha(0.001f)) {
        AndroidView(
            factory = { webViewManager.initialize() },
            modifier = Modifier.size(1.dp)
        )
    }

    // Page background #E8F0FB with full height white panel
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PageBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        // Centered white panel (width max 600dp for tablet/desktop kiosk ergonomics)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .background(PanelBackground)
        ) {
            // 1) Header bar bg #E7EBF1, padding 10x14
            HeaderBar(
                tenantLogoUrl = uiState.tenantLogoUrl,
                tenantSiteName = uiState.tenantSiteName,
                onLogoLongClick = { viewModel.onSettingsClicked() }
            )

            // Scrollable body
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Location & IN / OUT
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CardBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.locationOptions.size > 1) {
                            LocationPickerSection(
                                options = uiState.locationOptions,
                                selectedLocation = uiState.selectedLocation,
                                onLocationSelected = { viewModel.onLocationSelected(it) }
                            )
                        }

                        ToggleIndicators(
                            activeToggle = uiState.activeToggle,
                            onToggleSelected = { viewModel.onToggleClicked(it) }
                        )
                    }
                }

                // Card 2: Last activity
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CardBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF006D77),
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.label_last_activity_prefix),
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Normal
                                )
                            )
                            Text(
                                text = uiState.lastActivityText.ifBlank { "--" },
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                // Card 3: Notes
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CardBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NotesSection(
                        notes = uiState.notes,
                        onNotesChanged = { viewModel.onNotesChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    )
                }

                // Card 4: Camera Preview & Status Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, CardBorder),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                        ) {
                            CameraPreviewView(
                                isCameraActive = uiState.isCameraActive,
                                onCameraStarted = { viewModel.onCameraStarted() },
                                onCameraFailed = { viewModel.onCameraFailed(it) },
                                onFrameCaptured = { base64, bitmap ->
                                    viewModel.onLatestFrame(bitmap)
                                    webViewManager.detectFrame(base64)
                                }
                            )

                            val captured = uiState.capturedBitmap
                            if (captured != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.Black)
                                        .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                 ) {
                                    Image(
                                        bitmap = captured.asImageBitmap(),
                                        contentDescription = "Captured Face",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xCC006D77),
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = "Captured",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Status text with frame icon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CenterFocusWeak,
                                contentDescription = null,
                                tint = Color(0xFF006D77),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.statusText,
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155),
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.testTag("status_text")
                            )
                        }

                        // Powered by row
                        val uriHandler = LocalUriHandler.current
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    try {
                                        uriHandler.openUri("https://elintom.io")
                                    } catch (_: Exception) {
                                    }
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.powered_by),
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFF64748B)
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Image(
                                painter = painterResource(R.drawable.ic_elintom_favicon),
                                contentDescription = "ElintOm",
                                modifier = Modifier.size(16.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }

            // 9) Sticky bottom bar (white, top border #DBE5F1, upward shadow)
            StickyBottomBar(
                buttonState = uiState.buttonState,
                isPosting = uiState.isPosting,
                onCaptureClick = { viewModel.onCaptureButtonClicked() }
            )
        }

        // 10) Toasts at top: success (#DFF0D8 bg, #3C763D text, 7s), error (#F2DEDE bg, #A94442 text, 5.5s), info (#D9EDF7 bg, #31708F text)
        AnimatedVisibility(
            visible = uiState.toast != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 580.dp)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            uiState.toast?.let { toast ->
                ToastBanner(
                    toast = toast,
                    onDismiss = { viewModel.dismissToast() }
                )
            }
        }

        // Hidden Settings Dialog for Tenant Base URL & API Key configuration
        if (uiState.showSettingsDialog) {
            SettingsDialog(
                currentBaseUrl = uiState.baseUrl,
                currentApiKey = uiState.apiKey,
                currentAutoSubmit = uiState.isAutoSubmitEnabled,
                onDismiss = { viewModel.dismissSettingsDialog() },
                onSave = { url, key, autoSubmit -> viewModel.updateSettings(url, key, autoSubmit) },
                onTestConnection = { url, key, cb -> viewModel.testConnection(url, key, cb) }
            )
        }
    }
}

@Composable
private fun HeaderBar(
    tenantLogoUrl: String?,
    tenantSiteName: String?,
    onLogoLongClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFE2F6F8),
                        Color(0xFFC7EFF4)
                    )
                )
            )
    ) {
        // Decorative soft teal curves in top-right
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val w = size.width
            drawCircle(
                color = Color(0x304DD0E1),
                radius = 160.dp.toPx(),
                center = Offset(w + 20.dp.toPx(), -20.dp.toPx())
            )
            drawCircle(
                color = Color(0x2526A69A),
                radius = 110.dp.toPx(),
                center = Offset(w + 10.dp.toPx(), -10.dp.toPx())
            )
            drawCircle(
                color = Color(0x2000838F),
                radius = 65.dp.toPx(),
                center = Offset(w, 0f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(42.dp)
                    .pointerInput(Unit) {
                        coroutineScope {
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false)
                                val job = launch {
                                    delay(3000L)
                                    onLogoLongClick()
                                }
                                waitForUpOrCancellation()
                                job.cancel()
                            }
                        }
                    }
                    .testTag("header_logo")
            ) {
                val fallbackPainter = painterResource(R.drawable.ic_tenant_full_logo)
                Image(
                    painter = fallbackPainter,
                    contentDescription = "ElintOM Logo",
                    modifier = Modifier.height(42.dp),
                    contentScale = ContentScale.Fit
                )
                if (!tenantLogoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(tenantLogoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = tenantSiteName ?: "Logo",
                        modifier = Modifier.height(42.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationPickerSection(
    options: List<com.example.data.model.LocationOption>,
    selectedLocation: com.example.data.model.LocationOption?,
    onLocationSelected: (com.example.data.model.LocationOption) -> Unit
) {
    if (options.size <= 1) return

    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable { expanded = true }
                .padding(horizontal = 10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Color(0xFF006D77),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = selectedLocation?.displayLabel() ?: stringResource(R.string.select_nearby_location),
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedLocation != null) Color(0xFF0F172A) else Color(0xFF64748B)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand Location Options",
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(24.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option.displayLabel(),
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0F172A)
                                )
                            )
                        },
                        onClick = {
                            onLocationSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Warning hint with amber warning icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = stringResource(R.string.multiple_locations_hint),
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFD97706)
                )
            )
        }
    }
}

@Composable
private fun ToggleIndicators(
    activeToggle: String, // "IN" or "OUT"
    onToggleSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val isInActive = activeToggle == "IN"
        val isOutActive = activeToggle == "OUT"

        // IN toggle button (48dp height, 12dp radius)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isInActive) Color(0xFF006D77) else Color.White)
                .border(
                    width = if (isInActive) 0.dp else 1.5.dp,
                    color = if (isInActive) Color.Transparent else Color(0xFFCBD5E1),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onToggleSelected("IN") }
                .testTag("toggle_in"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Login,
                    contentDescription = null,
                    tint = if (isInActive) Color.White else Color(0xFF334155),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(R.string.btn_in),
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isInActive) Color.White else Color(0xFF1E293B)
                    )
                )
            }
        }

        // OUT toggle button (48dp height, 12dp radius)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isOutActive) Color(0xFF006D77) else Color.White)
                .border(
                    width = if (isOutActive) 0.dp else 1.5.dp,
                    color = if (isOutActive) Color.Transparent else Color(0xFFCBD5E1),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onToggleSelected("OUT") }
                .testTag("toggle_out"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = if (isOutActive) Color.White else Color(0xFF334155),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(R.string.btn_out),
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOutActive) Color.White else Color(0xFF1E293B)
                    )
                )
            }
        }
    }
}

@Composable
private fun NotesSection(
    notes: String,
    onNotesChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Description,
                contentDescription = null,
                tint = Color(0xFF64748B),
                modifier = Modifier.size(17.dp)
            )
            Text(
                text = stringResource(R.string.label_notes),
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                .background(Color(0xFFFAFAFA))
                .padding(horizontal = 12.dp, vertical = 10.dp)
                .testTag("notes_input")
        ) {
            if (notes.isEmpty()) {
                Text(
                    text = stringResource(R.string.placeholder_notes),
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8)
                    )
                )
            }
            BasicTextField(
                value = notes,
                onValueChange = onNotesChanged,
                textStyle = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    color = Color(0xFF0F172A)
                ),
                cursorBrush = SolidColor(Color(0xFF006D77)),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun StickyBottomBar(
    buttonState: AttendanceButtonState,
    isPosting: Boolean,
    onCaptureClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp),
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Button(
                onClick = onCaptureClick,
                enabled = !isPosting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("capture_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006D77),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF006D77).copy(alpha = 0.6f),
                    disabledContentColor = Color.White.copy(alpha = 0.8f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = if (buttonState == AttendanceButtonState.RETAKE) Icons.Default.Refresh else Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buttonState.label,
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun ToastBanner(
    toast: com.example.data.model.ToastMessage,
    onDismiss: () -> Unit
) {
    val (bg, textColor, borderColor) = when (toast.type) {
        ToastType.SUCCESS -> Triple(ToastSuccessBg, ToastSuccessText, ToastSuccessBorder)
        ToastType.ERROR -> Triple(ToastErrorBg, ToastErrorText, ToastErrorBorder)
        ToastType.INFO -> Triple(ToastInfoBg, ToastInfoText, ToastInfoBorder)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("toast_banner")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = toast.message,
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                ),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss notification",
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingsDialog(
    currentBaseUrl: String,
    currentApiKey: String,
    currentAutoSubmit: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean) -> Unit,
    onTestConnection: (String, String, (Boolean, String) -> Unit) -> Unit
) {
    var isAuthenticated by remember { mutableStateOf(false) }

    // Admin Login state
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf<String?>(null) }

    // Config state
    var urlText by remember { mutableStateOf(currentBaseUrl) }
    var apiKeyText by remember { mutableStateOf(currentApiKey) }
    var isAutoSubmitEnabled by remember { mutableStateOf(currentAutoSubmit) }
    var isTesting by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    if (!isAuthenticated) {
        // Step 1: Admin Authentication Dialog
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Admin Access",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Enter admin credentials to unlock Kiosk settings:",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = TextMuted
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            authError = null
                        },
                        label = { Text("Username", fontFamily = InterFontFamily) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimary,
                            cursorColor = BrandPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            authError = null
                        },
                        label = { Text("Password", fontFamily = InterFontFamily) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimary,
                            cursorColor = BrandPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (authError != null) {
                        Text(
                            text = authError ?: "",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFFDC2626),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (username.trim() == "Darade" && password == "Darade@554") {
                            isAuthenticated = true
                            authError = null
                        } else {
                            authError = "Invalid username or password"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Unlock", fontFamily = InterFontFamily, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", fontFamily = InterFontFamily, color = TextMuted)
                }
            },
            modifier = Modifier.testTag("admin_login_dialog")
        )
    } else {
        // Step 2: Settings Configuration Dialog
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Kiosk Server & API Settings",
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Configure tenant server URL and API credentials:",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = TextMuted
                    )

                    OutlinedTextField(
                        value = urlText,
                        onValueChange = {
                            urlText = it
                            testResult = null
                        },
                        label = { Text("API Base URL", fontFamily = InterFontFamily) },
                        placeholder = { Text("http://192.168.1.x/ElintOm/ or https://attendance.elintom.com/") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimary,
                            cursorColor = BrandPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = apiKeyText,
                        onValueChange = {
                            apiKeyText = it
                            testResult = null
                        },
                        label = { Text("API Key (X-API-KEY)", fontFamily = InterFontFamily) },
                        placeholder = { Text("Enter API key (optional)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimary,
                            cursorColor = BrandPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Auto-Submit vs Manual Trigger Toggle
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Auto-Submit (Kiosk Mode)",
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isAutoSubmitEnabled) "Auto-trigger on face detection" else "Manual trigger (press Capture button)",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    color = if (isAutoSubmitEnabled) Color(0xFF0F766E) else Color(0xFF64748B)
                                )
                            }
                            Switch(
                                checked = isAutoSubmitEnabled,
                                onCheckedChange = { isAutoSubmitEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF006D77)
                                )
                            )
                        }
                    }

                    // Test Status Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                isTesting = true
                                testResult = null
                                onTestConnection(urlText, apiKeyText) { success, msg ->
                                    isTesting = false
                                    testResult = Pair(success, msg)
                                }
                            },
                            enabled = !isTesting && urlText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0F766E)
                            )
                        ) {
                            if (isTesting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Testing...", fontSize = 12.sp, fontFamily = InterFontFamily)
                            } else {
                                Text("Check Status", fontSize = 12.sp, fontFamily = InterFontFamily)
                            }
                        }
                    }

                    // Status display banner
                    testResult?.let { (success, msg) ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (success) Color(0xFFDEF7EC) else Color(0xFFFDE8E8),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = (if (success) "✓ " else "✗ ") + msg,
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = if (success) Color(0xFF03543F) else Color(0xFF9B1C1C),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onSave(urlText, apiKeyText, isAutoSubmitEnabled) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Save", fontFamily = InterFontFamily, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", fontFamily = InterFontFamily, color = TextMuted)
                }
            },
            modifier = Modifier.testTag("settings_dialog")
        )
    }
}
