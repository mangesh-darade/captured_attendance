package com.example.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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

private fun localizeStatusText(status: String, lang: String): String {
    if (lang == "en" || status.isBlank()) return status
    val lower = status.lowercase()
    return when {
        lower.contains("face detected successfully") || lower.contains("successfully") -> {
            if (lang == "mr") "चेहरा यशस्वीपणे ओळखला" else "चेहरा सफलतापूर्वक पहचाना गया"
        }
        lower.contains("no face detected") || lower.contains("no face") -> {
            if (lang == "mr") "चेहरा आढळला नाही. पुन्हा प्रयत्न करा." else "कोई चेहरा नहीं मिला। पुनः प्रयास करें।"
        }
        lower.contains("multiple faces") -> {
            if (lang == "mr") "एकापेक्षा जास्त चेहरे आढळले." else "एक से अधिक चेहरे मिले।"
        }
        lower.contains("hold still") -> {
            if (lang == "mr") "कृपया कॅमेऱ्यासमोर स्थिर उभे राहा" else "कृपया कैमरे के सामने स्थिर रहें"
        }
        lower.contains("face detection ready") || lower == "ready" -> {
            if (lang == "mr") "चेहरा स्कॅनिंग तयार आहे." else "चेहरा स्कैनिंग तैयार है।"
        }
        lower.contains("loading face model") || lower.contains("camera active") -> {
            if (lang == "mr") "कॅमेरा सुरू झाला. मॉडेल लोड होत आहे..." else "कैमरा सक्रिय। मॉडल लोड हो रहा है..."
        }
        lower.contains("scanning face") || lower.contains("matching your face") -> {
            if (lang == "mr") "चेहरा स्कॅन करत आहे..." else "चेहरा स्कैन हो रहा है..."
        }
        lower.contains("submitting") -> {
            if (lang == "mr") "हजेरी नोंदवत आहे..." else "उपस्थिति दर्ज हो रही है..."
        }
        lower.contains("check-in successful") || lower.contains("check in successful") -> {
            if (lang == "mr") "चेक इन यशस्वी झाले!" else "चेक इन सफल रहा!"
        }
        lower.contains("check-out successful") || lower.contains("check out successful") -> {
            if (lang == "mr") "चेक आउट यशस्वी झाले!" else "चेक आउट सफल रहा!"
        }
        lower.contains("already checked in") -> {
            if (lang == "mr") "आधीच चेक इन झाले आहे. चेक आउट करण्यासाठी OUT निवडा." else "पहले से चेक इन हैं। चेक आउट करने के लिए OUT चुनें।"
        }
        lower.contains("already checked out") -> {
            if (lang == "mr") "आधीच चेक आउट झाले आहे. चेक इन करण्यासाठी IN निवडा." else "पहले से चेक आउट हैं। चेक इन करने के लिए IN चुनें।"
        }
        lower.contains("not recognized") -> {
            if (lang == "mr") "चेहरा ओळखला नाही. कृपया कॅमेऱ्याकडे थेट पहा." else "चेहरा पहचाना नहीं गया। कृपया कैमरे की ओर सीधे देखें।"
        }
        lower.contains("attendance marked") -> {
            if (lang == "mr") "हजेरी नोंदवली गेली!" else "उपस्थिति दर्ज की गई!"
        }
        else -> status
    }
}

private fun localizeToastMessage(message: String, lang: String): String {
    if (lang == "en" || message.isBlank()) return message
    val lower = message.lowercase()
    return when {
        lower.contains("check-in successful") || lower.contains("check in successful") || lower.contains("checkin successful") -> {
            if (lang == "mr") "चेक इन यशस्वी झाले!" else "चेक इन सफल रहा!"
        }
        lower.contains("check-out successful") || lower.contains("check out successful") || lower.contains("checkout successful") -> {
            if (lang == "mr") "चेक आउट यशस्वी झाले!" else "चेक आउट सफल रहा!"
        }
        lower.contains("attendance marked") || lower.contains("attendance saved") -> {
            if (lang == "mr") "हजेरी नोंदवली गेली!" else "उपस्थिति दर्ज की गई!"
        }
        lower.contains("already checked in") -> {
            if (lang == "mr") "आधीच चेक इन झाले आहे. चेक आउट करण्यासाठी OUT निवडा." else "पहले से चेक इन हैं। चेक आउट करने के लिए OUT चुनें।"
        }
        lower.contains("already checked out") -> {
            if (lang == "mr") "आधीच चेक आउट झाले आहे. चेक इन करण्यासाठी IN निवडा." else "पहले से चेक आउट हैं। चेक इन करने के लिए IN चुनें।"
        }
        lower.contains("not recognized") -> {
            if (lang == "mr") "चेहरा ओळखला नाही. कृपया कॅमेऱ्याकडे थेट पहा." else "चेहरा पहचाना नहीं गया। कृपया कैमरे की ओर सीधे देखें।"
        }
        lower.contains("no face detected") -> {
            if (lang == "mr") "कॅमेऱ्यात चेहरा आढळला नाही. कृपया कॅमेऱ्याकडे थेट पहा." else "कैमरे में कोई चेहरा नहीं मिला। कृपया कैमरे की ओर सीधे देखें।"
        }
        lower.contains("geofence") || lower.contains("outside") -> {
            if (lang == "mr") "तुम्ही नोंदणीकृत ऑफिस / साईटच्या परिसराबाहेर आहात." else "आप पंजीकृत कार्यालय/साइट की सीमा से बाहर हैं।"
        }
        lower.contains("gps location is not available") || lower.contains("gps is turned off") -> {
            if (lang == "mr") "GPS लोकेशन उपलब्ध नाही. कृपया GPS चालू करा." else "GPS लोकेशन उपलब्ध नहीं है। कृपया GPS चालू करें।"
        }
        lower.contains("acquiring location") -> {
            if (lang == "mr") "लोकेशन शोधत आहे. कृपया थांबा..." else "लोकेशन प्राप्त कर रहे हैं। कृपया प्रतीक्षा करें..."
        }
        lower.contains("log in with your phone number") -> {
            if (lang == "mr") "हजेरी नोंदवण्यापूर्वी कृपया फोन नंबरने लॉगिन करा." else "उपस्थिति दर्ज करने से पहले कृपया फ़ोन नंबर से लॉगिन करें।"
        }
        lower.contains("internet restored") -> {
            if (lang == "mr") "इंटरनेट पुन्हा सुरू झाले." else "इंटरनेट पुनः जुड़ गया।"
        }
        lower.contains("offline attendance synced") -> {
            if (lang == "mr") "ऑफलाईन हजेरी सिंक झाली." else "ऑफ़लाइन उपस्थिति सिंक हो गई।"
        }
        else -> message
    }
}

private fun localizeActivityText(text: String, lang: String): String {
    if (text.isBlank() || text == "--" || lang == "en") return text
    return text
        .replace("Check-out at", "चेक-आउट:")
        .replace("Check-in at", "चेक-इन:")
        .replace("Check out at", "चेक-आउट:")
        .replace("Check in at", "चेक-इन:")
}

private fun faceGuideLook(status: String, captured: Boolean, lang: String): FaceGuideLook {
    if (captured) {
        val chip = when (lang) {
            "mr" -> "कॅप्चर झाले (Captured)"
            "hi" -> "कैप्चर किया (Captured)"
            else -> "Captured"
        }
        val caption = localizeStatusText(status, lang)
        return FaceGuideLook(
            ovalColor = Color(0xFF4ADE80),
            dashed = false,
            frameColor = Color(0xFF4ADE80),
            chipText = chip,
            caption = caption,
            captionColor = Color(0xFF15803D)
        )
    }
    val text = status.lowercase()
    return when {
        text.contains("successfully") || (text.contains("face detected") && !text.contains("no face") && !text.contains("multiple") && !text.contains("hold still")) -> {
            val chip = when (lang) {
                "mr" -> "चेहरा ओळखला"
                "hi" -> "चेहरा पहचाना"
                else -> "Face detected"
            }
            val caption = when (lang) {
                "mr" -> "चेहरा यशस्वीपणे ओळखला"
                "hi" -> "चेहरा सफलतापूर्वक पहचाना गया"
                else -> "Face detected successfully"
            }
            FaceGuideLook(
                ovalColor = Color(0xFF4ADE80),
                dashed = false,
                frameColor = Color(0xFF4ADE80),
                chipText = chip,
                caption = caption,
                captionColor = Color(0xFF15803D)
            )
        }
        text.contains("multiple") -> {
            val chip = when (lang) {
                "mr" -> "एकापेक्षा जास्त चेहरे"
                "hi" -> "एक से अधिक चेहरे"
                else -> "Multiple faces detected"
            }
            val caption = when (lang) {
                "mr" -> "कृपया कॅमेऱ्यासमोर फक्त एकच व्यक्ती उभे राहा"
                "hi" -> "कृपया कैमरे के सामने केवल एक ही व्यक्ति रहें"
                else -> "Multiple faces detected. Please ensure only one person is visible."
            }
            FaceGuideLook(
                ovalColor = Color(0xFFFB7185),
                dashed = false,
                frameColor = Color(0xFFFB7185),
                chipText = chip,
                caption = caption,
                captionColor = Color(0xFFE11D48)
            )
        }
        text.contains("hold still") -> {
            val chip = when (lang) {
                "mr" -> "स्थिर राहा"
                "hi" -> "स्थिर रहें"
                else -> "Hold still"
            }
            val caption = when (lang) {
                "mr" -> "कृपया कॅमेऱ्यासमोर स्थिर उभे राहा"
                "hi" -> "कृपया कैमरे के सामने स्थिर रहें"
                else -> "Face detected, hold still for better accuracy"
            }
            FaceGuideLook(
                ovalColor = Color(0xFFFB7185),
                dashed = false,
                frameColor = Color(0xFFFB7185),
                chipText = chip,
                caption = caption,
                captionColor = Color(0xFFE11D48)
            )
        }
        text.contains("no face") -> {
            val chip = when (lang) {
                "mr" -> "चेहरा शोधत आहे..."
                "hi" -> "चेहरा ढूंढ रहे हैं..."
                else -> "Looking for face..."
            }
            val caption = when (lang) {
                "mr" -> "चेहरा आढळला नाही. पुन्हा प्रयत्न करा."
                "hi" -> "कोई चेहरा नहीं मिला। पुनः प्रयास करें।"
                else -> "No face detected. Tap Retake to try again."
            }
            FaceGuideLook(
                ovalColor = Color.White.copy(alpha = 0.85f),
                dashed = true,
                frameColor = Color(0xFFCBD5E1),
                chipText = chip,
                caption = caption,
                captionColor = Color(0xFFE11D48)
            )
        }
        else -> {
            val chip = when (lang) {
                "mr" -> "चेहरा शोधत आहे..."
                "hi" -> "चेहरा ढूंढ रहे हैं..."
                else -> "Looking for face..."
            }
            FaceGuideLook(
                ovalColor = Color.White.copy(alpha = 0.85f),
                dashed = true,
                frameColor = Color(0xFFCBD5E1),
                chipText = chip,
                caption = localizeStatusText(status, lang),
                captionColor = Color(0xFF64748B)
            )
        }
    }
}

@Composable
fun AttendanceKioskScreen(
    viewModel: AttendanceViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState by viewModel.uiState.collectAsState()
    var showProfile by remember { mutableStateOf(false) }
    var confirmLogout by remember { mutableStateOf(false) }
    var showDiagnosticsDialog by remember { mutableStateOf(false) }

    val currentLanguage = uiState.appLanguage
    val locale = remember(currentLanguage) {
        when (currentLanguage) {
            "mr" -> Locale("mr")
            "hi" -> Locale("hi")
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
                    onProfileClick = if (uiState.deviceToken.isNotBlank()) {
                        { showProfile = true }
                    } else {
                        null
                    }
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
                                onLocationSelected = { viewModel.onLocationSelected(it) }
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
                                    text = stringResource(R.string.status_fetching_location),
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
                                Text(
                                    text = if (uiState.currentLandmark.isNotBlank()) {
                                        stringResource(R.string.label_standing_landmark) + ": " + uiState.currentLandmark
                                    } else {
                                        stringResource(R.string.label_standing_landmark) + ": (${uiState.currentLat}, ${uiState.currentLng})"
                                    },
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
                                isCameraActive = uiState.isCameraActive,
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
            val isLocationReady = uiState.currentLat != null && uiState.currentLng != null && !uiState.isLocationLoading
            StickyBottomBar(
                buttonState = uiState.buttonState,
                isPosting = uiState.isPosting,
                isLocationReady = isLocationReady,
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
                currentLanguage = uiState.appLanguage,
                onLanguageSelected = { viewModel.onLanguageSelected(it) },
                autoSubmit = uiState.isAutoSubmitEnabled,
                onAutoSubmitChange = { viewModel.setAutoSubmit(it) },
                onDismiss = { showProfile = false },
                onLogoutClick = {
                    showProfile = false
                    confirmLogout = true
                }
            )
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
                        Text(stringResource(R.string.btn_logout), fontFamily = InterFontFamily, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmLogout = false }) {
                        Text(stringResource(R.string.btn_cancel), fontFamily = InterFontFamily, color = TextMuted)
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
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("ElintOM Error Logs", logText)
                            clipboard.setPrimaryClip(clip)
                            viewModel.showToast(ToastType.SUCCESS, "Log copied to clipboard!", 2500L)
                        }
                    ) {
                        Text("Copy Log", fontFamily = InterFontFamily, fontWeight = FontWeight.Bold)
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
    onProfileClick: (() -> Unit)? = null
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
            val logoModifier = Modifier
                .height(40.dp)
                .widthIn(max = 148.dp)
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
            val headerName = userName.ifBlank { tenantSiteName.orEmpty() }
            if (headerName.isNotBlank()) {
                Text(
                    text = headerName,
                    fontFamily = InterFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .widthIn(max = 170.dp)
                )
            }
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

    val allowText = when (currentLanguage) {
        "mr" -> "परवानगी द्या"
        "hi" -> "अनुमति दें"
        else -> "Allow"
    }
    val settingsText = when (currentLanguage) {
        "mr" -> "सेटिंग्ज"
        "hi" -> "सेटिंग्स"
        else -> "Settings"
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        if (!cameraAllowed) {
            val msg = when (currentLanguage) {
                "mr" -> "कॅमेरा परवानगी बंद आहे. हजेरीसाठी कॅमेरा आवश्यक आहे."
                "hi" -> "कैमरा अनुमति बंद है। उपस्थिति के लिए कैमरा आवश्यक है।"
                else -> "Camera access is off. Face attendance needs the camera."
            }
            StatusRow(
                message = msg,
                action = allowText,
                onAction = onAllowCamera,
                secondaryAction = settingsText,
                onSecondary = onOpenAppSettings
            )
        }
        if (!locationAllowed) {
            val msg = when (currentLanguage) {
                "mr" -> "लोकेशन परवानगी बंद आहे. हजेरीसाठी GPS आवश्यक आहे."
                "hi" -> "लोकेशन अनुमति बंद है। उपस्थिति के लिए GPS आवश्यक है।"
                else -> "Location access is off. Attendance needs GPS."
            }
            StatusRow(
                message = msg,
                action = allowText,
                onAction = onAllowLocation,
                secondaryAction = settingsText,
                onSecondary = onOpenAppSettings
            )
        } else if (!gpsEnabled) {
            val msg = when (currentLanguage) {
                "mr" -> "GPS बंद आहे. हजेरी नोंदवण्यासाठी लोकेशन चालू करा."
                "hi" -> "GPS बंद है। उपस्थिति दर्ज करने के लिए लोकेशन चालू करें।"
                else -> "GPS is turned off. Turn on location to mark attendance."
            }
            val turnOnText = when (currentLanguage) {
                "mr" -> "GPS चालू करा"
                "hi" -> "GPS चालू करें"
                else -> "Turn on GPS"
            }
            StatusRow(
                message = msg,
                action = turnOnText,
                onAction = onOpenGpsSettings
            )
        }
        if (!isOnline) {
            val msg = if (waitingToSend) {
                when (currentLanguage) {
                    "mr" -> "इंटरनेट नाही. हजेरी या फोनवर सेव्ह केली आहे आणि इंटरनेट आल्यावर सिंक होईल."
                    "hi" -> "इंटरनेट नहीं है। उपस्थिति इस फ़ोन में सहेजी गई है और इंटरनेट आने पर सिंक हो जाएगी।"
                    else -> "No internet. Check-in and check-out are saved on this phone and will sync when internet is back."
                }
            } else {
                when (currentLanguage) {
                    "mr" -> "इंटरनेट कनेक्शन नाही."
                    "hi" -> "इंटरनेट कनेक्शन नहीं है।"
                    else -> "No internet connection."
                }
            }
            StatusRow(
                message = msg,
                action = null,
                onAction = null
            )
        } else if (waitingToSend) {
            val msg = when (currentLanguage) {
                "mr" -> "इंटरनेट पुन्हा सुरू झाले. सेव्ह केलेली हजेरी पाठवत आहे."
                "hi" -> "इंटरनेट पुनः जुड़ गया। सहेजी गई उपस्थिति भेज रहे हैं।"
                else -> "Internet is back. Sending saved attendance."
            }
            StatusRow(
                message = msg,
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
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    autoSubmit: Boolean,
    onAutoSubmitChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onLogoutClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_close), fontFamily = InterFontFamily, color = TextMuted)
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
                            text = stringResource(R.string.label_language),
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
                                "en" to stringResource(R.string.lang_en),
                                "mr" to stringResource(R.string.lang_mr),
                                "hi" to stringResource(R.string.lang_hi)
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
                                text = stringResource(R.string.label_auto_submit),
                                fontFamily = InterFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (autoSubmit) "Marks attendance when your face is ready" else "Press Capture to mark attendance",
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
                    Text(stringResource(R.string.btn_logout), fontFamily = InterFontFamily, color = Color.White)
                }
            }
        }
    )
}

@Composable
private fun LocationPickerSection(
    options: List<com.example.data.model.LocationOption>,
    selectedLocation: com.example.data.model.LocationOption?,
    onLocationSelected: (com.example.data.model.LocationOption) -> Unit
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
    isLocationReady: Boolean,
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
                        text = stringResource(R.string.status_fetching_location),
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
                        AttendanceButtonState.CAPTURE -> stringResource(R.string.btn_capture)
                        AttendanceButtonState.SCANNING -> stringResource(R.string.btn_scanning)
                        AttendanceButtonState.SUBMITTING -> stringResource(R.string.btn_submitting)
                        AttendanceButtonState.RETAKE -> stringResource(R.string.btn_retake)
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
