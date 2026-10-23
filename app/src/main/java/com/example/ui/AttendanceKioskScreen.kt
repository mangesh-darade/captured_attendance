package com.example.ui

import com.example.lang.LangManager
import com.example.lang.AppStrings
import android.Manifest
import android.graphics.Bitmap
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.layout.PaddingValues
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.data.model.AttendanceRecordItem
import com.example.data.model.TodayAttendanceSummary
import com.example.data.model.EnrollmentUserItem
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import android.content.Intent
import android.content.res.Configuration
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import com.example.data.datastore.AttendancePreferencesRepository
import com.example.data.model.AttendanceButtonState
import com.example.data.model.ToastType
import com.example.face.FaceDetectionBridge
import com.example.face.HiddenFaceWebViewManager
import com.example.ui.theme.BorderBottomBar
import com.example.ui.theme.BorderDefault
import com.example.ui.theme.BorderToggle
import com.example.ui.theme.BrandActiveTint
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryDark
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

private data class FaceGuideLook(
    val ovalColor: Color,
    val dashed: Boolean,
    val frameColor: Color,
    val chipText: String,
    val caption: String,
    val captionColor: Color
)

private fun localizeStatusText(status: String, lang: String): String =
    LangManager.getStrings(lang).localizeStatus(status)

private fun localizeToastMessage(message: String, lang: String): String =
    LangManager.getStrings(lang).localizeToast(message)

private fun localizeActivityText(text: String, lang: String): String =
    LangManager.getStrings(lang).localizeActivity(text)

private fun faceGuideLook(status: String, captured: Boolean, lang: String): FaceGuideLook {
    val s = LangManager.getStrings(lang)
    if (captured) {
        return FaceGuideLook(
            ovalColor = Color(0xFF4ADE80),
            dashed = false,
            frameColor = Color(0xFF4ADE80),
            chipText = s.capturedChip,
            caption = s.localizeStatus(status),
            captionColor = Color(0xFF15803D)
        )
    }
    val text = status.lowercase()
    return when {
        text.contains("successfully") || (text.contains("face detected") && !text.contains("no face") && !text.contains("multiple") && !text.contains("hold still")) -> {
            FaceGuideLook(
                ovalColor = Color(0xFF4ADE80),
                dashed = false,
                frameColor = Color(0xFF4ADE80),
                chipText = s.faceDetectedChip,
                caption = s.faceDetectedSuccessCaption,
                captionColor = Color(0xFF15803D)
            )
        }
        text.contains("multiple") -> {
            FaceGuideLook(
                ovalColor = Color(0xFFFB7185),
                dashed = false,
                frameColor = Color(0xFFFB7185),
                chipText = s.multipleFacesChip,
                caption = s.multipleFacesCaption,
                captionColor = Color(0xFFE11D48)
            )
        }
        text.contains("hold still") -> {
            FaceGuideLook(
                ovalColor = Color(0xFFFB7185),
                dashed = false,
                frameColor = Color(0xFFFB7185),
                chipText = s.holdStillChip,
                caption = s.holdStillCaption,
                captionColor = Color(0xFFE11D48)
            )
        }
        text.contains("no face") -> {
            FaceGuideLook(
                ovalColor = Color.White.copy(alpha = 0.85f),
                dashed = true,
                frameColor = Color(0xFFCBD5E1),
                chipText = s.lookingForFaceChip,
                caption = s.noFaceCaption,
                captionColor = Color(0xFFE11D48)
            )
        }
        else -> {
            FaceGuideLook(
                ovalColor = Color.White.copy(alpha = 0.85f),
                dashed = true,
                frameColor = Color(0xFFCBD5E1),
                chipText = s.lookingForFaceChip,
                caption = s.localizeStatus(status),
                captionColor = Color(0xFF64748B)
            )
        }
    }
}


enum class KioskBottomTab {
    HOME,
    CAPTURE,
    SETTINGS
}

@Composable
fun AttendanceKioskScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    var currentTab by rememberSaveable { mutableStateOf(KioskBottomTab.HOME) }
    var showProfile by remember { mutableStateOf(false) }
    var confirmLogout by remember { mutableStateOf(false) }
    var showDiagnosticsDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = !uiState.showFaceEnrollment && currentTab != KioskBottomTab.HOME) {
        currentTab = KioskBottomTab.HOME
    }

    val currentLanguage = uiState.appLanguage
    val s = LangManager.getStrings(currentLanguage)
    val locale = remember(currentLanguage) {
        when (currentLanguage) {
            "mr" -> Locale("mr")
            "hi" -> Locale("hi")
            "gu" -> Locale("gu")
            else -> Locale("en")
        }
    }
    val baseConfiguration = LocalConfiguration.current
    val configuration = remember(locale, baseConfiguration) {
        Configuration(baseConfiguration).apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
    }
    val baseContext = LocalContext.current
    val localizedContext = remember(configuration, baseContext) {
        baseContext.createConfigurationContext(configuration)
    }

    // Permissions launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (cameraGranted) {
            viewModel.onCameraStarted()
        } else {
            viewModel.onCameraFailed("Camera permission denied.")
        }

        if (fineGranted) {
            viewModel.triggerLocationAndDerive()
        }
        reportDeviceAccess(context, viewModel)
    }

    LaunchedEffect(Unit) {
        val hasCam = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val hasFine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasCam || !hasFine) {
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
        reportDeviceAccess(context, viewModel)
    }

    val areaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(uiState.areaTracking) {
        if (!uiState.areaTracking) return@LaunchedEffect
        val needed = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            needed.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= 29 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            needed.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (needed.isNotEmpty()) {
            areaPermissionLauncher.launch(needed.toTypedArray())
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                reportDeviceAccess(context, viewModel)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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

    CompositionLocalProvider(
        LocalConfiguration provides configuration,
        LocalContext provides localizedContext
    ) {
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
                    userName = uiState.loggedInUserName,
                    onLogoLongClick = { viewModel.onSettingsClicked() },
                    onTodayAttendanceClick = if (uiState.deviceToken.isNotBlank()) {
                        { viewModel.openTodayAttendance() }
                    } else null,
                    onProfileClick = if (uiState.deviceToken.isNotBlank()) {
                        { currentTab = KioskBottomTab.SETTINGS }
                    } else {
                        null
                    },
                    showBackButton = (currentTab == KioskBottomTab.CAPTURE),
                    onBackClick = { currentTab = KioskBottomTab.HOME }
                )

                val enrollmentUser = uiState.selectedEnrollmentUser
                if (uiState.showFaceEnrollment && enrollmentUser != null) {
                    FaceEnrollmentScannerView(
                        user = enrollmentUser,
                        isAdmin = uiState.isAdmin,
                        scanCount = uiState.enrollmentScanCount,
                        facePhoto = uiState.enrollmentFacePhoto,
                        isRegistering = uiState.isRegisteringFace,
                        successMsg = uiState.enrollmentSuccessMsg,
                        errorMsg = uiState.enrollmentErrorMsg,
                        isCameraActive = uiState.isCameraActive,
                        faceBridge = faceBridge,
                        webViewManager = webViewManager,
                        viewModel = viewModel,
                        onBack = {
                            if (uiState.isAdmin) {
                                viewModel.selectEnrollmentUser(null)
                            } else {
                                viewModel.closeFaceEnrollment()
                            }
                        },
                        onClose = { viewModel.closeFaceEnrollment() },
                        onRetake = { viewModel.retakeEnrollmentScan() },
                        onRegister = { viewModel.submitFaceRegistration() }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (currentTab) {
                            KioskBottomTab.HOME -> {
                                KioskHomeScreen(
                                    uiState = uiState,
                                    currentLanguage = currentLanguage,
                                    onNavigateToCapture = { currentTab = KioskBottomTab.CAPTURE },
                                    onOpenHistory = { viewModel.openTodayAttendance() },
                                    onOpenFaceEnrollment = { viewModel.openFaceEnrollment() },
                                    onOpenSettings = { currentTab = KioskBottomTab.SETTINGS }
                                )
                            }
                            KioskBottomTab.SETTINGS -> {
                                KioskSettingsScreen(
                                    uiState = uiState,
                                    currentLanguage = currentLanguage,
                                    onLanguageSelected = { viewModel.onLanguageSelected(it) },
                                    onAutoSubmitChange = { viewModel.setAutoSubmit(it) },
                                    onFaceEnrollmentClick = { viewModel.openFaceEnrollment() },
                                    onSettingsClicked = { viewModel.onSettingsClicked() },
                                    onViewLogs = { showDiagnosticsDialog = true },
                                    onLogoutClick = { confirmLogout = true }
                                )
                            }
                            KioskBottomTab.CAPTURE -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    val scrollState = rememberScrollState()
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .verticalScroll(scrollState)
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                AccessStatusCard(
                    cameraAllowed = uiState.cameraAllowed,
                    locationAllowed = uiState.locationAllowed,
                    gpsEnabled = uiState.gpsEnabled,
                    isOnline = uiState.isOnline,
                    waitingToSend = uiState.waitingToSend,
                    currentLanguage = currentLanguage,
                    onAllowCamera = {
                        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                    },
                    onAllowLocation = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    onOpenGpsSettings = {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    },
                    onOpenAppSettings = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                    }
                )

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
                        if (uiState.locationOptions.size > 1 || uiState.showLocationPicker) {
                            LocationPickerSection(
                                options = uiState.locationOptions,
                                selectedLocation = uiState.selectedLocation,
                                onLocationSelected = { viewModel.onLocationSelected(it) },
                                s = s
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (uiState.isLocationLoading || uiState.currentLat == null) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(13.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF006D77)
                                )
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = "Location",
                                    tint = Color(0xFF006D77),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = s.fetchingLocation,
                                    style = TextStyle(
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF006D77)
                                    )
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = "Location",
                                    tint = Color(0xFF006D77),
                                    modifier = Modifier.size(16.dp)
                                )
                                val siteName = uiState.selectedLocation?.pureName()?.takeIf { it.isNotBlank() }
                                val landmarkText = uiState.currentLandmark.ifBlank {
                                    if (uiState.currentLat != null) "(${uiState.currentLat}, ${uiState.currentLng})" else ""
                                }
                                val locationDisplayText = when {
                                    siteName != null && landmarkText.isNotBlank() -> {
                                        s.standingLandmark + ": " + siteName + " (" + landmarkText + ")"
                                    }
                                    siteName != null -> {
                                        s.standingLandmark + ": " + siteName
                                    }
                                    landmarkText.isNotBlank() -> {
                                        s.standingLandmark + ": " + landmarkText
                                    }
                                    else -> s.standingLandmark
                                }
                                Text(
                                    text = locationDisplayText,
                                    style = TextStyle(
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF006D77)
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        ToggleIndicators(
                            activeToggle = uiState.activeToggle,
                            onToggleSelected = { viewModel.onToggleClicked(it) },
                            s = s
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
                                text = s.lastActivityPrefix,
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Normal
                                )
                            )
                            Text(
                                text = localizeActivityText(uiState.lastActivityText.ifBlank { "--" }, currentLanguage),
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
                        s = s,
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
                        val captured = uiState.capturedBitmap
                        val faceLook = faceGuideLook(uiState.statusText, captured != null, currentLanguage)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .border(2.dp, faceLook.frameColor, RoundedCornerShape(20.dp))
                        ) {
                            CameraPreviewView(
                                isCameraActive = uiState.isCameraActive && currentTab == KioskBottomTab.CAPTURE,
                                onCameraStarted = { viewModel.onCameraStarted() },
                                onCameraFailed = { viewModel.onCameraFailed(it) },
                                canAcceptFrame = { faceBridge.canAcceptFrame() },
                                onFrameCaptured = { base64, bitmap ->
                                    viewModel.onLatestFrame(bitmap)
                                    if (faceBridge.canAcceptFrame()) {
                                        faceBridge.markFrameSent()
                                        webViewManager.detectFrame(base64)
                                    }
                                }
                            )

                            if (captured != null) {
                                Image(
                                    bitmap = captured.asImageBitmap(),
                                    contentDescription = "Captured Face",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize()
                                )
                            } else {
                                Canvas(modifier = Modifier.matchParentSize()) {
                                    val ovalWidth = size.width * 0.46f
                                    val ovalHeight = size.height * 0.72f
                                    drawOval(
                                        color = faceLook.ovalColor,
                                        topLeft = Offset(
                                            (size.width - ovalWidth) / 2f,
                                            (size.height - ovalHeight) / 2f
                                        ),
                                        size = Size(ovalWidth, ovalHeight),
                                        style = Stroke(
                                            width = 2.dp.toPx(),
                                            pathEffect = if (faceLook.dashed) {
                                                PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                                            } else {
                                                null
                                            }
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xCC111827),
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 10.dp)
                            ) {
                                Text(
                                    text = faceLook.chipText,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = InterFontFamily,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Text(
                            text = faceLook.caption,
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = faceLook.captionColor,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("status_text")
                        )

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
                                text = s.poweredBy,
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

                                    val isLocationReady = uiState.currentLat != null && uiState.currentLng != null && !uiState.isLocationLoading
                                    StickyBottomBar(
                                        buttonState = uiState.buttonState,
                                        isPosting = uiState.isPosting,
                                        isLocationReady = isLocationReady,
                                        onCaptureClick = { viewModel.onCaptureButtonClicked() },
                                        s = s
                                    )
                                }
                            }
                        }
                    }

                    // 3-Tab Bottom Navigation Bar (Home, Capture, Settings) - hidden on Capture tab
                    if (currentTab != KioskBottomTab.CAPTURE) {
                        KioskBottomNavigationBar(
                            selectedTab = currentTab,
                            onTabSelected = { currentTab = it },
                            currentLanguage = currentLanguage
                        )
                    }
                }
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
                    currentLanguage = currentLanguage,
                    onDismiss = { viewModel.dismissToast() }
                )
            }
        }

        // Hidden Settings Dialog for Tenant Base URL & API Key configuration
        if (uiState.showSettingsDialog) {
            SettingsDialog(
                currentBaseUrl = uiState.masterUrl,
                currentAutoSubmit = uiState.isAutoSubmitEnabled,
                onDismiss = { viewModel.dismissSettingsDialog() },
                onSave = { url, autoSubmit -> viewModel.updateSettings(url, autoSubmit) },
                onTestConnection = { url, cb -> viewModel.testConnection(url, cb) },
                onViewLogs = {
                    viewModel.dismissSettingsDialog()
                    showDiagnosticsDialog = true
                }
            )
        }

        if (showProfile && uiState.deviceToken.isNotBlank()) {
            ProfileDialog(
                userName = uiState.loggedInUserName,
                isAdmin = uiState.isAdmin,
                currentLanguage = uiState.appLanguage,
                onLanguageSelected = { viewModel.onLanguageSelected(it) },
                autoSubmit = uiState.isAutoSubmitEnabled,
                onAutoSubmitChange = { viewModel.setAutoSubmit(it) },
                onFaceEnrollmentClick = {
                    showProfile = false
                    viewModel.openFaceEnrollment()
                },
                onDismiss = { showProfile = false },
                onLogoutClick = {
                    showProfile = false
                    confirmLogout = true
                }
            )
        }

        if (uiState.showTodayAttendance) {
            TodayAttendanceDialog(
                summary = uiState.todaySummary,
                records = uiState.todayRecords,
                isLoading = uiState.isTodayAttendanceLoading,
                errorMessage = uiState.todayAttendanceError,
                onRefresh = { viewModel.fetchTodayAttendance() },
                onDismiss = { viewModel.closeTodayAttendance() }
            )
        }

        if (uiState.showFaceEnrollment && uiState.selectedEnrollmentUser == null) {
            if (uiState.isAdmin) {
                EnrollmentUserListDialog(
                    users = uiState.enrollmentUsers,
                    isLoading = uiState.isEnrollmentLoading,
                    errorMsg = uiState.enrollmentErrorMsg,
                    onSelectUser = { viewModel.selectEnrollmentUser(it) },
                    onRefreshUsers = { viewModel.fetchEnrollmentUsers() },
                    onDismiss = { viewModel.closeFaceEnrollment() }
                )
            } else {
                if (uiState.isEnrollmentLoading) {
                    androidx.compose.ui.window.Dialog(onDismissRequest = { viewModel.closeFaceEnrollment() }) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            shadowElevation = 6.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                CircularProgressIndicator(color = BrandPrimary, modifier = Modifier.size(24.dp))
                                Text("Preparing camera...", fontFamily = InterFontFamily, fontSize = 14.sp, color = TextPrimary)
                            }
                        }
                    }
                } else if (uiState.enrollmentErrorMsg != null) {
                    AlertDialog(
                        onDismissRequest = { viewModel.closeFaceEnrollment() },
                        title = { Text("Face Enrollment", fontFamily = InterFontFamily, fontWeight = FontWeight.Bold) },
                        text = { Text(uiState.enrollmentErrorMsg ?: "Could not load user profile", fontFamily = InterFontFamily) },
                        confirmButton = {
                            Button(onClick = { viewModel.closeFaceEnrollment() }) {
                                Text("OK", fontFamily = InterFontFamily)
                            }
                        }
                    )
                }
            }
        }

        if (confirmLogout) {
            AlertDialog(
                onDismissRequest = { confirmLogout = false },
                title = {
                    Text(
                        text = "Log out?",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to log out, ${uiState.loggedInUserName.ifBlank { "this user" }}?",
                        fontFamily = InterFontFamily,
                        color = TextMuted
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            confirmLogout = false
                            viewModel.logout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text(s.logoutButton, fontFamily = InterFontFamily, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmLogout = false }) {
                        Text(s.cancelButton, fontFamily = InterFontFamily, color = TextMuted)
                    }
                }
            )
        }

        if (uiState.isBootstrapping && uiState.deviceToken.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7FAFB)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 2.5.dp,
                    color = BrandPrimary
                )
            }
        } else if (uiState.deviceToken.isBlank()) {
            MerchantCodeScreen(
                androidCode = uiState.androidCode,
                merchantCode = uiState.companyCode,
                shopName = uiState.tenantSiteName.orEmpty(),
                tenantLogoUrl = uiState.tenantLogoUrl,
                setupComplete = uiState.setupComplete,
                isSettingUp = uiState.isSettingUp,
                setupError = uiState.setupError,
                savedPhone = uiState.savedPhone,
                isLoggingIn = uiState.isLoggingIn,
                loginError = uiState.loginError,
                isOnline = uiState.isOnline,
                onOpenSettings = { viewModel.onSettingsClicked() },
                onSetup = { code -> viewModel.setupWithAndroidCode("", code) },
                onLogin = { phone, password -> viewModel.login(phone, password) },
                onViewLogs = { showDiagnosticsDialog = true }
            )
        }

        if (showDiagnosticsDialog) {
            var logText by remember { mutableStateOf(viewModel.getDiagnosticLogs()) }
            val logPath = remember { viewModel.getDiagnosticLogPath() }

            AlertDialog(
                onDismissRequest = { showDiagnosticsDialog = false },
                title = {
                    Text(
                        text = "App Error & Diagnostic Logs",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                    ) {
                        Text(
                            text = "Log File: $logPath",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B),
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f, fill = false),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F172A),
                            border = BorderStroke(1.dp, Color(0xFF334155))
                        ) {
                            val scrollState = rememberScrollState()
                            Text(
                                text = logText.ifBlank { "No errors recorded yet." },
                                color = Color(0xFFE2E8F0),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .verticalScroll(scrollState)
                            )
                        }
                    }
                },
                confirmButton = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        var isSyncing by remember { mutableStateOf(false) }
                        TextButton(
                            onClick = {
                                if (isSyncing) return@TextButton
                                isSyncing = true
                                viewModel.syncLogsToServer { success, msg ->
                                    isSyncing = false
                                    viewModel.showToast(
                                        if (success) ToastType.SUCCESS else ToastType.ERROR,
                                        msg,
                                        3500L
                                    )
                                }
                            }
                        ) {
                            Text(
                                text = if (isSyncing) "Syncing..." else "Sync to Web",
                                color = Color(0xFF2563EB),
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("ElintOM Error Logs", logText)
                                clipboard.setPrimaryClip(clip)
                                viewModel.showToast(ToastType.SUCCESS, "Log copied to clipboard!", 2500L)
                            }
                        ) {
                            Text("Copy", fontFamily = InterFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    Row {
                        TextButton(
                            onClick = {
                                viewModel.clearDiagnosticLogs()
                                logText = viewModel.getDiagnosticLogs()
                            }
                        ) {
                            Text("Clear", color = Color(0xFFDC2626), fontFamily = InterFontFamily)
                        }
                        TextButton(onClick = { showDiagnosticsDialog = false }) {
                            Text("Close", fontFamily = InterFontFamily)
                        }
                    }
                }
            )
        }
    }
}
}

@Composable
private fun HeaderBar(
    tenantLogoUrl: String?,
    tenantSiteName: String?,
    userName: String,
    onLogoLongClick: () -> Unit,
    onTodayAttendanceClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFE2F6F8),
                        Color(0xFFC7EFF4)
                    )
                )
            )
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
        // Decorative soft teal curves in top-right, kept inside the bar
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val w = size.width
            val h = size.height
            drawCircle(
                color = Color(0x404DD0E1),
                radius = h * 1.35f,
                center = Offset(w - 12.dp.toPx(), h * 0.22f)
            )
            drawCircle(
                color = Color(0x3326A69A),
                radius = h * 0.95f,
                center = Offset(w - 52.dp.toPx(), h * 0.08f)
            )
            drawCircle(
                color = Color(0x2800838F),
                radius = h * 0.55f,
                center = Offset(w - 20.dp.toPx(), h * 0.42f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton && onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF006D77),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
            val logoModifier = Modifier
                .height(40.dp)
                .widthIn(max = if (showBackButton) 120.dp else 148.dp)
            if (!tenantLogoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(tenantLogoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = tenantSiteName ?: "Site logo",
                    modifier = logoModifier,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.CenterStart,
                    error = painterResource(R.drawable.ic_tenant_full_logo),
                    fallback = painterResource(R.drawable.ic_tenant_full_logo)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_tenant_full_logo),
                    contentDescription = tenantSiteName ?: "Site logo",
                    modifier = logoModifier,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.CenterStart
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (onProfileClick != null) {
                ProfileAvatar(
                    name = userName,
                    onClick = onProfileClick,
                    size = 36.dp
                )
            }
        }
    }
}

private fun reportDeviceAccess(context: android.content.Context, viewModel: AttendanceViewModel) {
    val cameraAllowed = ContextCompat.checkSelfPermission(
        context, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
    val locationAllowed = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val gpsEnabled = try {
        val manager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    } catch (_: Exception) {
        false
    }
    viewModel.setDeviceAccess(cameraAllowed, locationAllowed, gpsEnabled)
}

@Composable
private fun AccessStatusCard(
    cameraAllowed: Boolean,
    locationAllowed: Boolean,
    gpsEnabled: Boolean,
    isOnline: Boolean,
    waitingToSend: Boolean,
    currentLanguage: String = "en",
    onAllowCamera: () -> Unit,
    onAllowLocation: () -> Unit,
    onOpenGpsSettings: () -> Unit,
    onOpenAppSettings: () -> Unit
) {
    if (cameraAllowed && locationAllowed && gpsEnabled && isOnline && !waitingToSend) return

    val s = LangManager.getStrings(currentLanguage)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        if (!cameraAllowed) {
            StatusRow(
                message = s.cameraPermissionOff,
                action = s.allow,
                onAction = onAllowCamera,
                secondaryAction = s.settings,
                onSecondary = onOpenAppSettings
            )
        }
        if (!locationAllowed) {
            StatusRow(
                message = s.locationPermissionOff,
                action = s.allow,
                onAction = onAllowLocation,
                secondaryAction = s.settings,
                onSecondary = onOpenAppSettings
            )
        } else if (!gpsEnabled) {
            StatusRow(
                message = s.gpsTurnedOff,
                action = s.turnOnGps,
                onAction = onOpenGpsSettings
            )
        }
        if (!isOnline) {
            val msg = if (waitingToSend) s.noInternetWaiting else s.noInternet
            StatusRow(
                message = msg,
                action = null,
                onAction = null
            )
        } else if (waitingToSend) {
            StatusRow(
                message = s.internetRestoredSending,
                action = null,
                onAction = null
            )
        }
    }
}

@Composable
private fun StatusRow(
    message: String,
    action: String?,
    onAction: (() -> Unit)?,
    secondaryAction: String? = null,
    onSecondary: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF7ED),
        border = BorderStroke(1.dp, Color(0xFFFDBA74)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = message,
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                color = Color(0xFF9A3412)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (action != null && onAction != null) {
                    Button(
                        onClick = onAction,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text(action, fontFamily = InterFontFamily, color = Color.White)
                    }
                }
                if (secondaryAction != null && onSecondary != null) {
                    TextButton(onClick = onSecondary) {
                        Text(secondaryAction, fontFamily = InterFontFamily, color = BrandPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    name: String,
    size: androidx.compose.ui.unit.Dp,
    onClick: (() -> Unit)? = null
) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(BrandPrimary)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = if (size < 48.dp) 16.sp else 28.sp
        )
    }
}

@Composable
private fun ProfileDialog(
    userName: String,
    isAdmin: Boolean = false,
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    autoSubmit: Boolean,
    onAutoSubmitChange: (Boolean) -> Unit,
    onFaceEnrollmentClick: () -> Unit = {},
    onDismiss: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val s = LangManager.getStrings(currentLanguage)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(s.closeButton, fontFamily = InterFontFamily, color = TextMuted)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileAvatar(name = userName, size = 72.dp)
                Text(
                    text = userName.ifBlank { "Signed in" },
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Signed in",
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    color = TextMuted
                )

                // Language selection card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = s.languageLabel,
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "en" to "English",
                                "mr" to "मराठी",
                                "hi" to "हिंदी",
                                "gu" to "ગુજરાતી"
                            ).forEach { (code, name) ->
                                val selected = currentLanguage == code
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) Color(0xFF006D77) else Color.White)
                                        .border(
                                            1.dp,
                                            if (selected) Color(0xFF006D77) else Color(0xFFCBD5E1),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { onLanguageSelected(code) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name,
                                        fontFamily = InterFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selected) Color.White else Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                }

                if (isAdmin) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
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
                                    text = s.autoSubmitLabel,
                                    fontFamily = InterFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (autoSubmit) s.autoSubmitReady else s.autoSubmitManual,
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                            Switch(
                                checked = autoSubmit,
                                onCheckedChange = onAutoSubmitChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = BrandPrimary
                                )
                            )
                        }
                    }
                }

                // Face Enrollment button (Available for both Admin and Regular Employee)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFaceEnrollmentClick() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Face Enrollment",
                                fontFamily = InterFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isAdmin) "Manage employee face biometrics" else "Register your face biometric",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
                Button(
                    onClick = onLogoutClick,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(s.logoutButton, fontFamily = InterFontFamily, color = Color.White)
                }
            }
        }
    )
}

@Composable
private fun TodayAttendanceDialog(
    summary: TodayAttendanceSummary?,
    records: List<AttendanceRecordItem>,
    isLoading: Boolean,
    errorMessage: String?,
    onRefresh: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredRecords = remember(records, searchQuery) {
        if (searchQuery.isBlank()) records
        else records.filter {
            it.userName.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontFamily = InterFontFamily, color = TextMuted)
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatListBulleted,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Today's Attendance",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Punches & summary",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
                IconButton(
                    onClick = onRefresh,
                    enabled = !isLoading,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = if (isLoading) TextMuted else BrandPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1) Summary chips card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Present
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${summary?.totalPresent ?: records.size}",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Present",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp)
                                .background(Color(0xFFCBD5E1))
                        )
                        // In
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${summary?.totalIn ?: 0}",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF15803D)
                            )
                            Text(
                                text = "In",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp)
                                .background(Color(0xFFCBD5E1))
                        )
                        // Out
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${summary?.totalOut ?: 0}",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFFB45309)
                            )
                            Text(
                                text = "Out",
                                fontFamily = InterFontFamily,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // 2) Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search employee...",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // 3) Content Body: Loading / Error / Empty / List
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = BrandPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    errorMessage != null -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ToastErrorBg,
                            border = BorderStroke(1.dp, ToastErrorBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = errorMessage,
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    color = ToastErrorText,
                                    textAlign = TextAlign.Center
                                )
                                OutlinedButton(
                                    onClick = onRefresh,
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ToastErrorText)
                                ) {
                                    Text("Retry", fontFamily = InterFontFamily, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    filteredRecords.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) "No employees matching \"$searchQuery\"" else "No attendance punches found for today yet.",
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredRecords) { record ->
                                AttendanceRecordRow(record)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun AttendanceRecordRow(record: AttendanceRecordItem) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFAFAFA),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Employee Initials Avatar
            val name = record.userName.ifBlank { "User ${record.userId}" }
            val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "E"
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F2FE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0369A1)
                )
            }

            // Name and Punch Times
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (record.inTime.isNotBlank() && record.inTime != "--:--") {
                        Text(
                            text = "In: ${record.inTime}",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF15803D)
                        )
                    }
                    if (!record.outTime.isNullOrBlank()) {
                        Text(
                            text = "Out: ${record.outTime}",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFB45309)
                        )
                    }
                    if (!record.location.isNullOrBlank()) {
                        Text(
                            text = "(${record.location})",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            // Status Pill
            val isPunchedIn = record.isIn
            val statusBg = if (isPunchedIn) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
            val statusColor = if (isPunchedIn) Color(0xFF166534) else Color(0xFF92400E)
            val statusText = if (isPunchedIn) "IN" else "OUT"

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(statusBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = statusText,
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
private fun EnrollmentUserListDialog(
    users: List<EnrollmentUserItem>,
    isLoading: Boolean,
      errorMsg: String?,
    onSelectUser: (EnrollmentUserItem) -> Unit,
    onRefreshUsers: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredUsers = remember(users, searchQuery) {
        if (searchQuery.isBlank()) users
        else users.filter {
            it.name.contains(searchQuery.trim(), ignoreCase = true) ||
                    (it.phone ?: "").contains(searchQuery.trim())
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontFamily = InterFontFamily, color = TextMuted)
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Face Enrollment",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Select employee to register camera scan",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
                IconButton(
                    onClick = onRefreshUsers,
                    enabled = !isLoading,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = if (isLoading) TextMuted else BrandPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 220.dp, max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search by name or phone...",
                            fontFamily = InterFontFamily,
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = BrandPrimary, modifier = Modifier.size(32.dp))
                        }
                    }
                    errorMsg != null -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ToastErrorBg,
                            border = BorderStroke(1.dp, ToastErrorBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = errorMsg,
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    color = ToastErrorText,
                                    textAlign = TextAlign.Center
                                )
                                OutlinedButton(onClick = onRefreshUsers) {
                                    Text("Retry", fontFamily = InterFontFamily, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    filteredUsers.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) "No employees matching \"$searchQuery\"" else "No employees found",
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredUsers) { user ->
                                EnrollmentUserRow(user = user, onSelect = { onSelectUser(user) })
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun FaceEnrollmentScannerView(
    user: EnrollmentUserItem,
    isAdmin: Boolean = false,
    scanCount: Int,
    facePhoto: Bitmap?,
    isRegistering: Boolean,
    successMsg: String?,
    errorMsg: String?,
    isCameraActive: Boolean,
    faceBridge: FaceDetectionBridge,
    webViewManager: HiddenFaceWebViewManager,
    viewModel: AttendanceViewModel,
    onBack: () -> Unit,
    onClose: () -> Unit,
    onRetake: () -> Unit,
    onRegister: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Top Bar inside Scanner view
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF1F5F9),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Column {
                        Text(
                            text = "Register: ${user.name}",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (!user.phone.isNullOrBlank()) "Phone: ${user.phone}" else (user.group ?: "Employee"),
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted
                    )
                }
            }
        }

        // 2. Live Camera Preview with Oval Guide
        val isComplete = scanCount >= 4
        val frameColor = if (isComplete) Color(0xFF16A34A) else if (scanCount > 0) Color(0xFF0284C7) else Color(0xFF94A3B8)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(2.5.dp, frameColor, RoundedCornerShape(20.dp))
        ) {
            CameraPreviewView(
                isCameraActive = isCameraActive,
                onCameraStarted = { viewModel.onCameraStarted() },
                onCameraFailed = { viewModel.onCameraFailed(it) },
                canAcceptFrame = { faceBridge.canAcceptFrame() },
                onFrameCaptured = { base64, bitmap ->
                    viewModel.onLatestFrame(bitmap)
                    if (faceBridge.canAcceptFrame()) {
                        faceBridge.markFrameSent()
                        webViewManager.detectFrame(base64)
                    }
                }
            )

            // Oval Guide overlay
            Canvas(modifier = Modifier.matchParentSize()) {
                val ovalWidth = size.width * 0.48f
                val ovalHeight = size.height * 0.72f
                drawOval(
                    color = frameColor,
                    topLeft = Offset(
                        (size.width - ovalWidth) / 2f,
                        (size.height - ovalHeight) / 2f
                    ),
                    size = Size(ovalWidth, ovalHeight),
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        pathEffect = if (!isComplete) PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f) else null
                    )
                )
            }

            // Top Status Pill inside camera
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xCC000000))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isComplete) "✓ 4/4 Biometric Scans Ready" else "Align Face Inside Oval Guide",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isComplete) Color(0xFF4ADE80) else Color.White
                )
            }

            // Bottom Progress Pill inside camera
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isComplete) Color(0xFF16A34A) else Color(0xDD0F172A))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Samples Captured: $scanCount / 4",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // 3. Progress Bar
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Face Scan Progress",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "${(scanCount * 25)}%",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isComplete) Color(0xFF16A34A) else BrandPrimary
                )
            }
            LinearProgressIndicator(
                progress = { (scanCount / 4f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isComplete) Color(0xFF16A34A) else BrandPrimary,
                trackColor = Color(0xFFE2E8F0)
            )
        }

        // 4. Feedback Messages: Success, Error, Registering
        if (successMsg != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ToastSuccessBg,
                border = BorderStroke(1.dp, ToastSuccessBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "✓ $successMsg",
                        fontFamily = InterFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ToastSuccessText,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (isAdmin) {
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Enroll Another", fontFamily = InterFontFamily, fontSize = 12.sp)
                            }
                        }
                        Button(
                            onClick = onClose,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Done", fontFamily = InterFontFamily, color = Color.White)
                        }
                    }
                }
            }
        }

        if (errorMsg != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ToastErrorBg,
                border = BorderStroke(1.dp, ToastErrorBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMsg,
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = ToastErrorText,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        if (isRegistering) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BrandPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Saving biometric data to server...",
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandPrimary
                )
            }
        }

        // 5. Actions Bar
        if (successMsg == null && !isRegistering) {
            if (isComplete) {
                Button(
                    onClick = onRegister,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Register Face",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }

                OutlinedButton(
                    onClick = onRetake,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Retake Scan",
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = BrandPrimary,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Please look at the camera. Collecting sample ${scanCount + 1} of 4...",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = TextSlate
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnrollmentUserRow(user: EnrollmentUserItem, onSelect: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFAFAFA),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val initial = user.name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (user.hasFaceEnrolled) Color(0xFFDCFCE7) else Color(0xFFE0F2FE)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (user.hasFaceEnrolled) Color(0xFF166534) else Color(0xFF0369A1)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!user.phone.isNullOrBlank()) {
                    Text(
                        text = user.phone,
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            val badgeBg = if (user.hasFaceEnrolled) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
            val badgeColor = if (user.hasFaceEnrolled) Color(0xFF166534) else Color(0xFF92400E)
            val badgeText = if (user.hasFaceEnrolled) "Enrolled" else "Pending"

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badgeText,
                    fontFamily = InterFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                )
            }
        }
    }
}

@Composable
private fun LocationPickerSection(
    options: List<com.example.data.model.LocationOption>,
    selectedLocation: com.example.data.model.LocationOption?,
    onLocationSelected: (com.example.data.model.LocationOption) -> Unit,
    s: AppStrings
) {
    if (options.isEmpty()) return

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
                        text = selectedLocation?.displayLabel() ?: s.selectLocation,
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

        if (options.size > 1) {
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
                    text = s.multipleLocationsHint,
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
}

@Composable
private fun ToggleIndicators(
    activeToggle: String, // "IN" or "OUT"
    onToggleSelected: (String) -> Unit,
    s: AppStrings
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
                    text = s.inButton,
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
                    text = s.outButton,
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
    s: AppStrings,
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
                text = s.notesLabel,
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
                    text = s.notesPlaceholder,
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
    isLocationReady: Boolean,
    onCaptureClick: () -> Unit,
    s: AppStrings
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
            val isEnabled = !isPosting && isLocationReady
            Button(
                onClick = onCaptureClick,
                enabled = isEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("capture_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF006D77),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF006D77).copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.75f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (!isLocationReady && !isPosting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = s.fetchingLocation,
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                } else {
                    Icon(
                        imageVector = if (buttonState == AttendanceButtonState.RETAKE) Icons.Default.Refresh else Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val buttonLabel = when (buttonState) {
                        AttendanceButtonState.CAPTURE -> s.captureButton
                        AttendanceButtonState.SCANNING -> s.scanningButton
                        AttendanceButtonState.SUBMITTING -> s.submittingButton
                        AttendanceButtonState.RETAKE -> s.retakeButton
                    }
                    Text(
                        text = buttonLabel,
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
}

@Composable
private fun ToastBanner(
    toast: com.example.data.model.ToastMessage,
    currentLanguage: String = "en",
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
                text = localizeToastMessage(toast.message, currentLanguage),
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
private fun MerchantCodeScreen(
    androidCode: String,
    merchantCode: String,
    shopName: String,
    tenantLogoUrl: String? = null,
    setupComplete: Boolean,
    isSettingUp: Boolean,
    setupError: String?,
    savedPhone: String,
    isLoggingIn: Boolean,
    loginError: String?,
    isOnline: Boolean,
    onOpenSettings: () -> Unit,
    onSetup: (String) -> Unit,
    onLogin: (String, String) -> Unit,
    onViewLogs: (() -> Unit)? = null
) {
    var codeText by remember { mutableStateOf(androidCode) }
    var usernameText by remember { mutableStateOf(savedPhone) }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val linked = setupComplete && setupError.isNullOrBlank() && androidCode.isNotBlank()
    val scrollState = rememberScrollState()

    LaunchedEffect(codeText) {
        val code = codeText.trim()
        if (!code.matches(Regex("^[A-Za-z0-9]{4,64}$"))) {
            return@LaunchedEffect
        }
        if (setupComplete && code.equals(androidCode.trim(), ignoreCase = true)) {
            return@LaunchedEffect
        }
        delay(500)
        onSetup(code)
    }

    LaunchedEffect(androidCode) {
        if (codeText.isBlank() && androidCode.isNotBlank()) {
            codeText = androidCode
        }
    }
    LaunchedEffect(savedPhone) {
        if (usernameText.isBlank() && savedPhone.isNotBlank()) {
            usernameText = savedPhone
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFB))
    ) {
        // Decorative curved ambient shapes matching reference UI
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Top-left soft teal curved circle
            drawCircle(
                color = Color(0xFFD6F2F4).copy(alpha = 0.55f),
                radius = size.width * 0.55f,
                center = Offset(x = 0f, y = 0f)
            )
            // Bottom-right soft teal curved circle
            drawCircle(
                color = Color(0xFFDCF4F6).copy(alpha = 0.5f),
                radius = size.width * 0.7f,
                center = Offset(x = size.width * 1.05f, y = size.height * 0.95f)
            )
        }

        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.7f, fill = false))
            Spacer(modifier = Modifier.height(48.dp))

            // Logo from Web App inside pure white background card
            Surface(
                modifier = Modifier
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val logoModifier = Modifier
                        .height(72.dp)
                        .widthIn(max = 220.dp)

                    if (!tenantLogoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(tenantLogoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = shopName.ifBlank { "ElintOM Logo" },
                            modifier = logoModifier,
                            contentScale = ContentScale.Fit,
                            alignment = Alignment.Center,
                            error = painterResource(R.drawable.ic_tenant_full_logo),
                            fallback = painterResource(R.drawable.ic_tenant_full_logo)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.ic_tenant_full_logo),
                            contentDescription = shopName.ifBlank { "ElintOM Logo" },
                            modifier = logoModifier,
                            contentScale = ContentScale.Fit,
                            alignment = Alignment.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (linked) {
                // Professional Elevated Login Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Username",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Color(0xFF475569)
                            )
                            OutlinedTextField(
                                value = usernameText,
                                onValueChange = { usernameText = it },
                                placeholder = { Text("Username or phone", fontSize = 14.sp, color = Color(0xFF94A3B8)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFCBD5E1),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Password",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Color(0xFF475569)
                            )
                            OutlinedTextField(
                                value = passwordText,
                                onValueChange = { passwordText = it },
                                placeholder = { Text("Password", fontSize = 14.sp, color = Color(0xFF94A3B8)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFCBD5E1),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (!loginError.isNullOrBlank()) {
                            Surface(
                                color = Color(0xFFFEF2F2),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = loginError,
                                        fontFamily = InterFontFamily,
                                        fontSize = 13.sp,
                                        color = Color(0xFFB91C1C),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rounded Pill Button (matching reference)
                        Button(
                            onClick = { onLogin(usernameText, passwordText) },
                            enabled = !isLoggingIn,
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandPrimary,
                                disabledContainerColor = BrandPrimary.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            if (isLoggingIn) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Log in",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                // Setup Card for Merchant Code
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Merchant Code",
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Color(0xFF475569)
                            )
                            OutlinedTextField(
                                value = codeText,
                                onValueChange = { codeText = it.uppercase() },
                                placeholder = { Text("e.g. LH02", fontSize = 14.sp, color = Color(0xFF94A3B8)) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFCBD5E1),
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color(0xFFF8FAFC),
                                    unfocusedContainerColor = Color(0xFFF8FAFC)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (!isOnline) {
                            Text(
                                text = "No internet connection. Setup will work when internet is back.",
                                fontFamily = InterFontFamily,
                                fontSize = 12.sp,
                                color = Color(0xFFDC2626)
                            )
                        }
                        if (!setupError.isNullOrBlank()) {
                            Surface(
                                color = Color(0xFFFEF2F2),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = setupError,
                                        fontFamily = InterFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFB91C1C)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = { onViewLogs?.invoke() },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "View Error Logs / तपशील पहा",
                                                fontFamily = InterFontFamily,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFFB91C1C)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (isSettingUp) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = BrandPrimary,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Connecting to merchant...",
                                    fontFamily = InterFontFamily,
                                    fontSize = 13.sp,
                                    color = BrandPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(48.dp))

            // Powered by ElintOM Footer matching reference
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 28.dp)
            ) {
                Text(
                    text = "Powered by ",
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "ElintOM",
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
            }
        }
    }
}

@Composable
private fun SettingsDialog(
    currentBaseUrl: String,
    currentAutoSubmit: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, Boolean) -> Unit,
    onTestConnection: (String, (Boolean, String) -> Unit) -> Unit,
    onViewLogs: (() -> Unit)? = null
) {
    var isAuthenticated by remember { mutableStateOf(false) }

    // Admin Login state
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf<String?>(null) }

    // Config state
    var urlText by remember { mutableStateOf(currentBaseUrl) }
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
                    text = "Server settings",
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
                        text = "This server holds the merchant list. The Android code then loads that merchant's shop URL.",
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
                        label = { Text("Merchant directory URL", fontFamily = InterFontFamily) },
                        placeholder = { Text(AttendancePreferencesRepository.DEFAULT_BASE_URL, fontFamily = InterFontFamily) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimary,
                            cursorColor = BrandPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { urlText = AttendancePreferencesRepository.DEFAULT_BASE_URL }) {
                            Text("Set Default URL", fontFamily = InterFontFamily, fontSize = 11.5.sp, color = BrandPrimary)
                        }
                    }

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
                                onTestConnection(urlText) { success, msg ->
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

                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { onViewLogs?.invoke() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "View Error Logs / त्रुटी तपशील",
                            fontSize = 12.sp,
                            fontFamily = InterFontFamily,
                            color = Color(0xFF334155)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onSave(urlText, isAutoSubmitEnabled) },
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

@Composable
private fun KioskBottomNavigationBar(
    selectedTab: KioskBottomTab,
    onTabSelected: (KioskBottomTab) -> Unit,
    currentLanguage: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp),
        color = Color.White,
        shadowElevation = 10.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Home Tab
            val isHome = selectedTab == KioskBottomTab.HOME
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(KioskBottomTab.HOME) }
                    .padding(vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isHome) Color(0xFFE6FFFA) else Color.Transparent)
                        .then(if (isHome) Modifier.border(1.5.dp, Color(0xFF80DFDF), CircleShape) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (isHome) Color(0xFF006D77) else Color(0xFF64748B),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (currentLanguage) {
                        "mr" -> "मुख्य"
                        "hi" -> "होम"
                        "gu" -> "હોમ"
                        else -> "Home"
                    },
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = if (isHome) FontWeight.Bold else FontWeight.Medium,
                    color = if (isHome) Color(0xFF006D77) else Color(0xFF64748B)
                )
            }

            // 2. Capture Attendance Tab (Center round prominent action button)
            val isCapture = selectedTab == KioskBottomTab.CAPTURE
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1.2f)
                    .clickable { onTabSelected(KioskBottomTab.CAPTURE) }
                    .padding(vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = if (isCapture) listOf(Color(0xFF006D77), Color(0xFF0A9396))
                                else listOf(Color(0xFF0F766E), Color(0xFF14B8A6))
                            )
                        )
                        .then(if (isCapture) Modifier.border(2.5.dp, Color(0xFF83C5BE), CircleShape) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Capture Attendance",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (currentLanguage) {
                        "mr" -> "अटेंडन्स"
                        "hi" -> "अटेंडेंस"
                        "gu" -> "હાજરી"
                        else -> "Capture"
                    },
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCapture) Color(0xFF006D77) else Color(0xFF64748B)
                )
            }

            // 3. Settings Tab
            val isSettings = selectedTab == KioskBottomTab.SETTINGS
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabSelected(KioskBottomTab.SETTINGS) }
                    .padding(vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (isSettings) Color(0xFFE6FFFA) else Color.Transparent)
                        .then(if (isSettings) Modifier.border(1.5.dp, Color(0xFF80DFDF), CircleShape) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = if (isSettings) Color(0xFF006D77) else Color(0xFF64748B),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (currentLanguage) {
                        "mr" -> "सेटिंग्ज"
                        "hi" -> "सेटिंग्स"
                        "gu" -> "સેટિંગ્સ"
                        else -> "Settings"
                    },
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = if (isSettings) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSettings) Color(0xFF006D77) else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun KioskHomeScreen(
    uiState: AttendanceUiState,
    currentLanguage: String,
    onNavigateToCapture: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenFaceEnrollment: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Hero User Greeting Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorder),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfileAvatar(
                        name = uiState.loggedInUserName,
                        size = 50.dp
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello, ${uiState.loggedInUserName.ifBlank { "User" }}",
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (uiState.isAdmin) Color(0xFFE0F2FE) else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = if (uiState.isAdmin) "Admin" else "Employee",
                                    fontFamily = InterFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (uiState.isAdmin) Color(0xFF0369A1) else Color(0xFF475569),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            val displayPhone = uiState.savedPhone.trim()
                            val isDuplicate = displayPhone.equals("admin", ignoreCase = true) ||
                                displayPhone.equals(uiState.loggedInUserName.trim(), ignoreCase = true) ||
                                displayPhone.equals("employee", ignoreCase = true)
                            if (displayPhone.isNotBlank() && !isDuplicate) {
                                Text(
                                    text = displayPhone,
                                    fontFamily = InterFontFamily,
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Today's Attendance Status Banner
        val isCheckedIn = uiState.lastActivityText.contains("check-in", ignoreCase = true) || uiState.lastActivityText.contains("in", ignoreCase = true)
        val hasActivity = uiState.lastActivityText.isNotBlank() && uiState.lastActivityText != "--"

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isCheckedIn) Color(0xFFF0FDF4) else if (hasActivity) Color(0xFFF0F9FF) else Color(0xFFFFFBEB),
            border = BorderStroke(
                1.dp,
                if (isCheckedIn) Color(0xFFBBF7D0) else if (hasActivity) Color(0xFFBAE6FD) else Color(0xFFFDE68A)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TODAY'S STATUS",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 0.5.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCheckedIn) Color(0xFF15803D) else if (hasActivity) Color(0xFF0284C7) else Color(0xFFD97706)
                    ) {
                        Text(
                            text = if (isCheckedIn) "✓ CHECKED IN" else if (hasActivity) "CHECKED OUT" else "NOT CHECKED IN",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = if (hasActivity) {
                        localizeActivityText(uiState.lastActivityText, currentLanguage)
                    } else {
                        "You haven't marked attendance today yet."
                    },
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                )

                if (uiState.currentLandmark.isNotBlank() || uiState.selectedLocation != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Color(0xFF0F766E),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = uiState.selectedLocation?.pureName() ?: uiState.currentLandmark,
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFF0F766E),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // 3. Quick Services Grid (2 Columns)
        Text(
            text = "Quick Actions",
            fontFamily = InterFontFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Today's History
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenHistory() }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF0369A1),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Today's Log",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "View in/out timestamps",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            // Card 2: Face Biometric Registration
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenFaceEnrollment() }
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFDCFCE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Face Biometric",
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Register / update scan",
                        fontFamily = InterFontFamily,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun KioskSettingsScreen(
    uiState: AttendanceUiState,
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onAutoSubmitChange: (Boolean) -> Unit,
    onFaceEnrollmentClick: () -> Unit,
    onSettingsClicked: () -> Unit,
    onViewLogs: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val s = LangManager.getStrings(currentLanguage)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Account Info Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorder),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileAvatar(name = uiState.loggedInUserName, size = 48.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.loggedInUserName.ifBlank { "User" },
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = if (uiState.isAdmin) "Administrator account" else "Employee account",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                    if (uiState.tenantSiteName != null) {
                        Text(
                            text = "Site: ${uiState.tenantSiteName}",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            color = BrandPrimary
                        )
                    }
                }
            }
        }

        // 2. Face Enrollment Tile
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF0FDF4),
            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onFaceEnrollmentClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Color(0xFF15803D),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Face Biometric Enrollment",
                        fontFamily = InterFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (uiState.isAdmin) "Manage and register employee faces" else "Register your face for attendance scan",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF15803D),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 3. Language Selector Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = s.languageLabel,
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                val languages = listOf(
                    "en" to "English",
                    "mr" to "मराठी (Marathi)",
                    "hi" to "हिंदी (Hindi)",
                    "gu" to "ગુજરાતી (Gujarati)"
                )

                languages.forEach { (code, name) ->
                    val selected = currentLanguage.equals(code, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) Color(0xFFE6FFFA) else Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, if (selected) BrandPrimary else Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(code) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = name,
                                fontFamily = InterFontFamily,
                                fontSize = 13.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) BrandPrimary else TextPrimary
                            )
                            if (selected) {
                                Text(
                                    text = "✓",
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = BrandPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Auto Submit Switch (Admin Only)
        if (uiState.isAdmin) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = s.autoSubmitLabel,
                            fontFamily = InterFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (uiState.isAutoSubmitEnabled) s.autoSubmitReady else s.autoSubmitManual,
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    Switch(
                        checked = uiState.isAutoSubmitEnabled,
                        onCheckedChange = onAutoSubmitChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandPrimary
                        )
                    )
                }
            }
        }

        // 5. Diagnostics & Server Logs
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "System Diagnostics",
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                OutlinedButton(
                    onClick = onViewLogs,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "View App Error & Connection Logs",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF334155)
                    )
                }
                if (uiState.isAdmin) {
                    OutlinedButton(
                        onClick = onSettingsClicked,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Configure Server URL",
                            fontFamily = InterFontFamily,
                            fontSize = 12.sp,
                            color = BrandPrimary
                        )
                    }
                }
            }
        }

        // 6. Logout Button
        Button(
            onClick = onLogoutClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = s.logoutButton,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
