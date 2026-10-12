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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 2) Hidden-by-default dropdown "Select nearby location"
                if (uiState.showLocationPicker) {
                    LocationPickerSection(
                        options = uiState.locationOptions,
                        selectedLocation = uiState.selectedLocation,
                        onLocationSelected = { viewModel.onLocationSelected(it) }
                    )
                }

                // 3) Two equal toggle buttons IN / OUT (38dp, interactive toggles)
                ToggleIndicators(
                    activeToggle = uiState.activeToggle,
                    onToggleSelected = { viewModel.onToggleClicked(it) }
                )

                // 4) "Last activity: --" 13sp #475569
                Text(
                    text = "${stringResource(R.string.label_last_activity_prefix)}${uiState.lastActivityText}",
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextMuted
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("last_activity_text")
                )

                // 5) "Notes" label 12sp 600 #475569 + multiline field min 78dp
                NotesSection(
                    notes = uiState.notes,
                    onNotesChanged = { viewModel.onNotesChanged(it) }
                )

                // 6) Front camera preview 16:9, radius 12, border #DCE5F1, bg #0B1220
                CameraPreviewView(
                    isCameraActive = uiState.isCameraActive,
                    onCameraStarted = { viewModel.onCameraStarted() },
                    onCameraFailed = { viewModel.onCameraFailed(it) },
                    onFrameCaptured = { base64 ->
                        webViewManager.detectFrame(base64)
                    }
                )

                // 7) Centered status text 13sp #6B7280
                Text(
                    text = uiState.statusText,
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextStatus,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("status_text")
                )

                // 8) "Powered by" + small ElintOm icon, 12sp, centered
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.powered_by),
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextStatus
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Image(
                        painter = painterResource(R.drawable.ic_elintom_small),
                        contentDescription = "ElintOm",
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
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
                onDismiss = { viewModel.dismissSettingsDialog() },
                onSave = { url, key -> viewModel.updateSettings(url, key) },
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HeaderBackground)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: ElintOm logo (34dp high) with long-click to open tenant config
        Box(
            modifier = Modifier
                .height(34.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { onLogoLongClick() }
                    )
                }
                .testTag("header_logo")
        ) {
            Image(
                painter = painterResource(R.drawable.ic_elintom_logo),
                contentDescription = "ElintOm Logo",
                modifier = Modifier.height(34.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Right: Tenant logo (from server) or tenant site name (12sp, 600, #111827, ellipsis)
        Box(
            modifier = Modifier
                .heightIn(max = 34.dp)
                .widthIn(max = 180.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (!tenantLogoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(tenantLogoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Tenant Logo",
                    modifier = Modifier.height(34.dp),
                    contentScale = ContentScale.Fit
                )
            } else if (!tenantSiteName.isNullOrBlank()) {
                Text(
                    text = tenantSiteName,
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, BorderDefault, RoundedCornerShape(10.dp))
                .background(Color.White)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedLocation?.displayLabel() ?: stringResource(R.string.select_nearby_location),
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (selectedLocation != null) TextPrimary else TextMuted
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand Location Options",
                    tint = TextMuted
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
                                    color = TextPrimary
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

        Spacer(modifier = Modifier.height(4.dp))

        // Hint: 12sp #B45309 "Multiple nearby locations found. Please select one location."
        Text(
            text = stringResource(R.string.multiple_locations_hint),
            style = TextStyle(
                fontFamily = InterFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = TextWarning
            )
        )
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

        // IN toggle button
        Box(
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isInActive) BrandActiveTint else Color.Transparent)
                .border(
                    width = 1.dp,
                    color = if (isInActive) BrandPrimary else BorderToggle,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onToggleSelected("IN") }
                .testTag("toggle_in"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.btn_in),
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isInActive) BrandPrimary else TextSlate
                )
            )
        }

        // OUT toggle button
        Box(
            modifier = Modifier
                .weight(1f)
                .height(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isOutActive) BrandActiveTint else Color.Transparent)
                .border(
                    width = 1.dp,
                    color = if (isOutActive) BrandPrimary else BorderToggle,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { onToggleSelected("OUT") }
                .testTag("toggle_out"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.btn_out),
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOutActive) BrandPrimary else TextSlate
                )
            )
        }
    }
}

@Composable
private fun NotesSection(
    notes: String,
    onNotesChanged: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.label_notes),
            style = TextStyle(
                fontFamily = InterFontFamily,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 78.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, BorderDefault, RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(10.dp)
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
                    color = TextPrimary
                ),
                cursorBrush = SolidColor(BrandPrimary),
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
            .shadow(elevation = 6.dp),
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = BorderBottomBar)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Button(
                onClick = onCaptureClick,
                enabled = !isPosting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("capture_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = BrandPrimary.copy(alpha = 0.6f),
                    disabledContentColor = Color.White.copy(alpha = 0.8f)
                )
            ) {
                Text(
                    text = buttonState.label,
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontSize = 15.sp,
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
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
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
                    onClick = { onSave(urlText, apiKeyText) },
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
