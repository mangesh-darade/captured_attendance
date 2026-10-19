package com.example.ui

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datastore.AttendancePreferencesRepository
import com.example.data.datastore.CachedAttendanceData
import com.example.data.model.AttendanceButtonState
import com.example.data.model.DeriveLocationPreviewResponse
import com.example.data.model.LocationOption
import com.example.data.model.sortedByDistance
import com.example.data.model.StoreAttendanceResponse
import com.example.data.model.ToastMessage
import com.example.data.model.ToastType
import com.example.data.network.NetworkClientManager
import com.example.face.FaceDetectionResult
import com.example.location.AreaTrackService
import com.example.location.AreaTrackStore
import com.example.location.AreaWatch
import com.example.location.LocationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import android.graphics.Bitmap
import android.media.MediaActionSound
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class AttendanceUiState(
    val buttonState: AttendanceButtonState = AttendanceButtonState.CAPTURE,
    val statusText: String = "Camera active. Loading face model...",
    val activeToggle: String = "IN", // "IN" or "OUT"
    val lastActivityText: String = "--",
    val notes: String = "",
    val locationOptions: List<LocationOption> = emptyList(),
    val selectedLocation: LocationOption? = null,
    val locationChosenManually: Boolean = false,
    val showLocationPicker: Boolean = false,
    val toast: ToastMessage? = null,
    val tenantLogoUrl: String? = null,
    val tenantSiteName: String? = null,
    val baseUrl: String = AttendancePreferencesRepository.DEFAULT_BASE_URL,
    val masterUrl: String = AttendancePreferencesRepository.DEFAULT_BASE_URL,
    val companyCode: String = "",
    val androidCode: String = "",
    val setupComplete: Boolean = false,
    val deviceToken: String = "",
    val loggedInUserName: String = "",
    val savedPhone: String = "",
    val isSettingUp: Boolean = false,
    val setupError: String? = null,
    val isLoggingIn: Boolean = false,
    val loginError: String? = null,
    val apiKey: String = "",
    val isCameraActive: Boolean = true,
    val isCameraFailed: Boolean = false,
    val isModelReady: Boolean = false,
    val currentLat: Double? = null,
    val currentLng: Double? = null,
    val currentLandmark: String = "",
    val isLocationLoading: Boolean = true,
    val isPosting: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val capturedBitmap: Bitmap? = null,
    val isAutoSubmitEnabled: Boolean = false,
    val isOnline: Boolean = true,
    val cameraAllowed: Boolean = true,
    val locationAllowed: Boolean = true,
    val gpsEnabled: Boolean = true,
    val waitingToSend: Boolean = false,
    val areaTracking: Boolean = false,
    val isBootstrapping: Boolean = true
)

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    private val shutterSound = MediaActionSound()

    val networkClientManager = NetworkClientManager(application)
    val preferencesRepository = AttendancePreferencesRepository(application)
    val locationHelper = LocationHelper(application, networkClientManager.nominatimService)

    private val _uiState = MutableStateFlow(
        AttendanceUiState(
            baseUrl = preferencesRepository.getFastBaseUrl(),
            masterUrl = preferencesRepository.getFastBaseUrl(),
            companyCode = preferencesRepository.getFastCompanyCode(),
            androidCode = preferencesRepository.getFastAndroidCode(),
            setupComplete = preferencesRepository.getFastSetupComplete(),
            deviceToken = preferencesRepository.getFastDeviceToken(),
            loggedInUserName = preferencesRepository.getFastUserName(),
            savedPhone = preferencesRepository.getFastSavedPhone(),
            isBootstrapping = true
        )
    )
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    private val descriptorBuffer = Collections.synchronizedList(mutableListOf<List<Float>>())
    private var consecutiveGoodTicks = 0
    private var consecutiveNoFaceTicks = 0
    private var lastSubmitTime = 0L
    private var lastDeriveWithDescriptorTime = 0L

    private var cachedAttendance: CachedAttendanceData? = null
    private val offlineMutex = Mutex()
    private var offlineSyncRunning = false
    private var cachedSites: List<CachedSite> = emptyList()
    private var deriveDebounceJob: Job? = null
    private var toastDismissJob: Job? = null
    private var scanningTimeoutJob: Job? = null
    private var resetCapturedJob: Job? = null
    private var latestBitmap: Bitmap? = null
    private var lastAccurateFixAt = 0L
    private val connectivityManager =
        application.getSystemService(ConnectivityManager::class.java)
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            viewModelScope.launch {
                val wasOffline = !_uiState.value.isOnline
                _uiState.update { it.copy(isOnline = true) }
                if (wasOffline) {
                    showToast(ToastType.INFO, "Internet restored.", 4000L)
                    if (_uiState.value.deviceToken.isNotBlank()) {
                        triggerLocationAndDerive()
                        sendPendingPunch()
                    }
                }
            }
        }

        override fun onLost(network: Network) {
            viewModelScope.launch {
                _uiState.update { it.copy(isOnline = networkClientManager.isOnline()) }
            }
        }
    }

    private data class CachedSite(val name: String, val lat: Double, val lng: Double, val radiusM: Int)

    fun onLatestFrame(bitmap: Bitmap) {
        val previous = latestBitmap
        latestBitmap = bitmap
        val frozen = _uiState.value.capturedBitmap
        if (previous != null && previous !== bitmap && previous !== frozen && !previous.isRecycled) {
            previous.recycle()
        }
    }

    init {
        shutterSound.load(MediaActionSound.SHUTTER_CLICK)
        networkClientManager.currentDeviceToken = preferencesRepository.getFastDeviceToken()
        _uiState.update { it.copy(isOnline = networkClientManager.isOnline()) }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        loadPreferencesAndBootstrap()
    }

    override fun onCleared() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        shutterSound.release()
        super.onCleared()
    }

    fun setDeviceAccess(cameraAllowed: Boolean, locationAllowed: Boolean, gpsEnabled: Boolean) {
        _uiState.update {
            it.copy(
                cameraAllowed = cameraAllowed,
                locationAllowed = locationAllowed,
                gpsEnabled = gpsEnabled,
                isCameraFailed = if (!cameraAllowed) true else it.isCameraFailed,
                isLocationLoading = if (!locationAllowed || !gpsEnabled) false else it.isLocationLoading
            )
        }
    }

    private fun playCaptureSound() {
        try {
            shutterSound.play(MediaActionSound.SHUTTER_CLICK)
        } catch (_: Exception) {
        }
    }

    private fun loadPreferencesAndBootstrap() {
        viewModelScope.launch {
            val base = preferencesRepository.tenantBaseUrlFlow.first()
            val masterUrl = preferencesRepository.masterUrlFlow.first()
            val companyCode = preferencesRepository.companyCodeFlow.first()
            val androidCode = preferencesRepository.androidCodeFlow.first()
            val setupComplete = preferencesRepository.setupCompleteFlow.first()
            val deviceToken = preferencesRepository.deviceTokenFlow.first()
            val userName = preferencesRepository.deviceUserNameFlow.first()
            val savedPhone = preferencesRepository.lastPhoneFlow.first()
            val autoSubmit = preferencesRepository.autoSubmitFlow.first()
            preferencesRepository.syncFastPrefs(
                baseUrl = base,
                companyCode = companyCode,
                androidCode = androidCode,
                setupComplete = setupComplete,
                deviceToken = deviceToken,
                userName = userName,
                phone = savedPhone
            )
            networkClientManager.currentDeviceToken = deviceToken
            _uiState.update {
                it.copy(
                    baseUrl = base,
                    masterUrl = masterUrl,
                    companyCode = companyCode,
                    androidCode = androidCode,
                    setupComplete = setupComplete,
                    deviceToken = deviceToken,
                    loggedInUserName = userName,
                    savedPhone = savedPhone,
                    isAutoSubmitEnabled = autoSubmit,
                    isBootstrapping = false
                )
            }

            // Restore cached last activity if cached date == today
            val cached = preferencesRepository.cachedAttendanceFlow.first()
            cachedAttendance = cached
            if (cached.isToday()) {
                val formatted = formatLastActivity(
                    lastType = cached.lastType,
                    lastAt = cached.lastAt,
                    selectedOutCheckinAt = null,
                    selectedInCheckoutAt = null
                )
                val initialToggle = cached.nextAction ?: if (cached.lastType == "check_in") "OUT" else "IN"
                _uiState.update {
                    it.copy(
                        lastActivityText = formatted.text,
                        activeToggle = formatted.toggle ?: initialToggle
                    )
                }
            }

            // Fetch branding from webapp whenever base is available
            if (base.isNotBlank()) {
                fetchCapturedPage(base)
            }
            if (deviceToken.isNotBlank()) {
                cachedSites = parseSites(preferencesRepository.readCachedSites())
                val queued = preferencesRepository.readOfflineQueue()
                val hasQueue = queued.isNotBlank() && queued != "[]"
                _uiState.update { it.copy(waitingToSend = hasQueue || it.waitingToSend) }
                if (networkClientManager.isOnline()) {
                    refreshSites(base)
                    sendPendingPunch()
                }
                if (cached.isToday() && cached.lastType.equals("check_in", ignoreCase = true)) {
                    AreaTrackService.resume(getApplication())
                    if (AreaTrackStore.read(getApplication()) != null) {
                        _uiState.update { it.copy(areaTracking = true) }
                    }
                }
            }
        }
    }

    fun onSettingsClicked() {
        _uiState.update { it.copy(showSettingsDialog = true) }
    }

    fun dismissSettingsDialog() {
        _uiState.update { it.copy(showSettingsDialog = false) }
    }

    fun setAutoSubmit(enabled: Boolean) {
        _uiState.update { it.copy(isAutoSubmitEnabled = enabled) }
        viewModelScope.launch {
            preferencesRepository.setAutoSubmit(enabled)
        }
    }

    fun updateSettings(newUrl: String, autoSubmit: Boolean = false) {
        viewModelScope.launch {
            val sanitized = networkClientManager.baseUrlFromCompanyCode(newUrl)
            val urlChanged = sanitized != networkClientManager.sanitizeBaseUrl(_uiState.value.masterUrl)
            preferencesRepository.saveMasterUrl(sanitized, autoSubmit)
            if (urlChanged) {
                preferencesRepository.clearMerchantSetup()
                networkClientManager.currentDeviceToken = ""
            } else {
                preferencesRepository.setAutoSubmit(autoSubmit)
            }
            _uiState.update {
                it.copy(
                    masterUrl = sanitized,
                    baseUrl = if (urlChanged) sanitized else it.baseUrl,
                    setupComplete = if (urlChanged) false else it.setupComplete,
                    androidCode = if (urlChanged) "" else it.androidCode,
                    companyCode = if (urlChanged) "" else it.companyCode,
                    apiKey = "",
                    deviceToken = if (urlChanged) "" else it.deviceToken,
                    loggedInUserName = if (urlChanged) "" else it.loggedInUserName,
                    isAutoSubmitEnabled = autoSubmit,
                    setupError = if (urlChanged) null else it.setupError,
                    loginError = if (urlChanged) null else it.loginError,
                    showSettingsDialog = false
                )
            }
            if (!urlChanged && _uiState.value.deviceToken.isNotBlank()) {
                fetchCapturedPage(_uiState.value.baseUrl, force = true)
                triggerLocationAndDerive()
            }
        }
    }

    fun setupWithAndroidCode(serverUrl: String, androidCode: String) {
        viewModelScope.launch {
            val code = androidCode.trim().uppercase()
            if (!code.matches(Regex("^[A-Za-z0-9]{4,64}$"))) {
                _uiState.update { it.copy(setupError = "Android unique code must be 4 to 64 letters or numbers.") }
                return@launch
            }
            val typed = networkClientManager.sanitizeBaseUrl(serverUrl)
            val master = typed.ifEmpty {
                networkClientManager.sanitizeBaseUrl(_uiState.value.masterUrl)
            }
            if (master.isEmpty()) {
                _uiState.update { it.copy(setupError = "Cannot reach the merchant directory.") }
                return@launch
            }
            _uiState.update { it.copy(isSettingUp = true, setupError = null) }
            if (typed.isNotEmpty()) {
                preferencesRepository.saveMasterUrl(typed, _uiState.value.isAutoSubmitEnabled)
            }
            try {
                val response = networkClientManager.attendanceService.setup(
                    url = "${master}attendance_api/setup",
                    androidCode = code
                )
                val body = response.body()
                val merchantUrl = body?.url?.trim().orEmpty()
                if (response.isSuccessful && body?.status.equals("success", ignoreCase = true) && merchantUrl.isNotEmpty()) {
                    val base = networkClientManager.sanitizeBaseUrl(merchantUrl)
                    val companyCode = body?.code?.trim().orEmpty()
                    preferencesRepository.saveMasterUrl(base, false)
                    preferencesRepository.saveMerchantSetup(base, code, companyCode)
                    preferencesRepository.setAutoSubmit(false)
                    _uiState.update {
                        it.copy(
                            isSettingUp = false,
                            setupError = null,
                            setupComplete = true,
                            androidCode = code,
                            masterUrl = base,
                            baseUrl = base,
                            companyCode = companyCode,
                            isAutoSubmitEnabled = false,
                            tenantSiteName = body?.name?.trim()?.takeIf { name -> name.isNotEmpty() } ?: it.tenantSiteName
                        )
                    }
                    fetchCapturedPage(base)
                } else {
                    val errorBodyMsg = try {
                        val err = response.errorBody()?.string()
                        if (!err.isNullOrBlank()) {
                            val json = JSONObject(err)
                            json.optString("message")
                        } else null
                    } catch (e: Exception) { null }
                    val message = body?.message ?: errorBodyMsg ?: "Android code was not found (HTTP ${response.code()})"
                    preferencesRepository.setSetupIncomplete()
                    _uiState.update {
                        it.copy(
                            isSettingUp = false,
                            setupComplete = false,
                            setupError = message
                        )
                    }
                }
            } catch (e: Exception) {
                val raw = e.localizedMessage.orEmpty()
                val message = if (raw.contains("failed to connect", ignoreCase = true) || raw.contains("timeout", ignoreCase = true)) {
                    "Server did not respond. Check the server URL."
                } else {
                    raw.ifBlank { "Cannot reach the merchant directory." }
                }
                _uiState.update {
                    it.copy(
                        isSettingUp = false,
                        setupError = message
                    )
                }
            }
        }
    }

    fun clearSetup() {
        viewModelScope.launch {
            preferencesRepository.clearMerchantSetup()
            networkClientManager.currentDeviceToken = ""
            val master = networkClientManager.sanitizeBaseUrl(_uiState.value.masterUrl)
            _uiState.update {
                it.copy(
                    setupComplete = false,
                    androidCode = "",
                    companyCode = "",
                    baseUrl = master,
                    deviceToken = "",
                    loggedInUserName = "",
                    setupError = null,
                    loginError = null
                )
            }
        }
    }

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            val phoneValue = phone.trim()
            if (!_uiState.value.setupComplete) {
                _uiState.update { it.copy(loginError = "Enter the Android unique code before login.") }
                return@launch
            }
            if (phoneValue.isEmpty() || password.isEmpty()) {
                _uiState.update { it.copy(loginError = "Username and password are required.") }
                return@launch
            }
            _uiState.update { it.copy(isLoggingIn = true, loginError = null) }
            val base = networkClientManager.sanitizeBaseUrl(_uiState.value.baseUrl)
            val companyCode = _uiState.value.companyCode
            try {
                val response = networkClientManager.attendanceService.login(
                    url = "${base}attendance_api/login",
                    phone = phoneValue,
                    password = password
                )
                val body = response.body()
                val token = body?.token?.trim().orEmpty()
                if (response.isSuccessful && body?.status.equals("success", ignoreCase = true) && token.isNotEmpty()) {
                    val name = body?.userName?.trim().orEmpty()
                    preferencesRepository.saveDeviceLogin(base, companyCode, token, body?.userId ?: 0, name, phoneValue)
                    preferencesRepository.setAutoSubmit(false)
                    networkClientManager.currentDeviceToken = token
                    _uiState.update {
                        it.copy(
                            isLoggingIn = false,
                            loginError = null,
                            baseUrl = base,
                            deviceToken = token,
                            loggedInUserName = name,
                            savedPhone = phoneValue,
                            isAutoSubmitEnabled = false
                        )
                    }
                    fetchCapturedPage(base, force = true)
                    refreshSites(base)
                    triggerLocationAndDerive()
                } else {
                    val errorBodyMsg = try {
                        val err = response.errorBody()?.string()
                        if (!err.isNullOrBlank()) {
                            val json = JSONObject(err)
                            json.optString("message")
                        } else null
                    } catch (e: Exception) { null }
                    val message = body?.message ?: errorBodyMsg ?: "Login failed (HTTP ${response.code()})"
                    if (message.contains("Service not enabled", ignoreCase = true) || message.contains("URL suspended", ignoreCase = true)) {
                        preferencesRepository.setSetupIncomplete()
                        _uiState.update {
                            it.copy(
                                isLoggingIn = false,
                                setupComplete = false,
                                setupError = message,
                                loginError = message
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoggingIn = false, loginError = message) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoggingIn = false,
                        loginError = e.localizedMessage ?: "Cannot reach the company server."
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.deviceToken.isNotBlank()) {
                try {
                    val url = "${networkClientManager.sanitizeBaseUrl(state.baseUrl)}attendance_api/logout"
                    networkClientManager.attendanceService.logout(url)
                } catch (_: Exception) {
                }
            }
            preferencesRepository.clearDeviceLogin()
            networkClientManager.currentDeviceToken = ""
            AreaTrackService.stop(getApplication())
            _uiState.update {
                it.copy(
                    deviceToken = "",
                    loggedInUserName = "",
                    showSettingsDialog = false,
                    areaTracking = false
                )
            }
        }
    }

    fun updateBaseUrl(newUrl: String) {
        updateSettings(newUrl, _uiState.value.isAutoSubmitEnabled)
    }

    fun testConnection(url: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val sanitized = networkClientManager.baseUrlFromCompanyCode(url)
            try {
                val apiUrl = "${sanitized}attendance_api/init"
                val response = networkClientManager.attendanceService.getInitData(apiUrl)

                if (response.isSuccessful) {
                    val body = response.body()
                    val siteName = body?.data?.siteName ?: "Connected"
                    onResult(true, "Connected: $siteName")
                } else {
                    onResult(false, "Server returned HTTP ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, "Connection error: ${e.localizedMessage ?: "Failed to reach server"}")
            }
        }
    }

    private fun fetchCapturedPage(base: String, force: Boolean = false) {
        viewModelScope.launch {
            try {
                val info = networkClientManager.getOrFetchToken(base, forceRefresh = force)
                _uiState.update {
                    it.copy(
                        tenantLogoUrl = info.tenantLogoUrl,
                        tenantSiteName = info.tenantSiteName
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(tenantLogoUrl = null) }
            }
        }
    }

    fun onCameraStarted() {
        _uiState.update {
            it.copy(
                isCameraActive = true,
                isCameraFailed = false,
                buttonState = if (it.buttonState == AttendanceButtonState.RETAKE) AttendanceButtonState.CAPTURE else it.buttonState
            )
        }
        if (!_uiState.value.isModelReady) {
            _uiState.update { it.copy(statusText = "Camera active. Loading face model...") }
        }
    }

    fun onCameraFailed(error: String? = null) {
        _uiState.update {
            it.copy(
                isCameraActive = false,
                isCameraFailed = true,
                statusText = "Camera failed to start. Tap Retake.",
                buttonState = AttendanceButtonState.RETAKE
            )
        }
        showToast(ToastType.ERROR, "Camera error: ${error ?: "Failed to access camera"}", 5500L)
    }

    fun onFaceModelLoaded(success: Boolean, message: String) {
        if (success) {
            _uiState.update {
                it.copy(
                    isModelReady = true,
                    statusText = "Face detection ready."
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isModelReady = false,
                    statusText = "Model load failed. Check storage."
                )
            }
            showToast(ToastType.ERROR, "Face model error: $message", 5500L)
        }
    }

    fun triggerLocationAndDerive() {
        viewModelScope.launch {
            val standing = refreshStandingLocation()
            if (standing == null) {
                showToast(
                    ToastType.ERROR,
                    "Precise location unavailable. Turn on high-accuracy GPS.",
                    5500L
                )
                return@launch
            }
            scheduleDerivePreview(includeDescriptor = false, debounceMs = 350L)
        }
    }

    private data class StandingFix(
        val latitude: Double,
        val longitude: Double,
        val landmark: String
    )

    /** Fresh GPS plus the street landmark for where the device is actually standing. */
    private suspend fun refreshStandingLocation(): StandingFix? {
        _uiState.update { it.copy(isLocationLoading = true) }
        try {
            val loc = locationHelper.getLocation().getOrNull() ?: return null
            val landmark = locationHelper.reverseGeocode(loc.latitude, loc.longitude)
            if (landmark.isNotBlank()) {
                lastAccurateFixAt = System.currentTimeMillis()
            }
            _uiState.update {
                it.copy(
                    currentLat = loc.latitude,
                    currentLng = loc.longitude,
                    currentLandmark = landmark
                )
            }
            return StandingFix(loc.latitude, loc.longitude, landmark)
        } finally {
            _uiState.update { it.copy(isLocationLoading = false) }
        }
    }

    /** A fix already accepted in the last 45s. Avoids another 15s GPS wait on every capture. */
    private fun recentStandingFix(): StandingFix? {
        val state = _uiState.value
        val lat = state.currentLat ?: return null
        val lng = state.currentLng ?: return null
        if (state.currentLandmark.isBlank()) return null
        if (System.currentTimeMillis() - lastAccurateFixAt > 45_000L) return null
        return StandingFix(lat, lng, state.currentLandmark)
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onToggleClicked(toggle: String) {
        _uiState.update { it.copy(activeToggle = toggle) }
    }

    fun onLocationSelected(option: LocationOption) {
        _uiState.update { it.copy(selectedLocation = option, locationChosenManually = true) }
        scheduleDerivePreview(includeDescriptor = false, debounceMs = 100L)
    }

    /**
     * Detection loop 900 ms result handling
     */
    fun onDetectionTick(result: FaceDetectionResult) {
        if (_uiState.value.capturedBitmap != null) return
        val now = System.currentTimeMillis()

        // 1. Update status text and buffer
        when {
            result.faceCount == 0 -> {
                _uiState.update { it.copy(statusText = "No face detected") }
                handleNonGoodFace()
            }
            result.faceCount > 1 -> {
                _uiState.update { it.copy(statusText = "Multiple faces detected") }
                handleNonGoodFace()
            }
            result.score < 0.55 -> {
                _uiState.update { it.copy(statusText = "Face detected, hold still for better accuracy") }
                handleNonGoodFace()
            }
            result.descriptor.size != 128 -> {
                _uiState.update { it.copy(statusText = "Scanning face features...") }
                handleNonGoodFace()
            }
            else -> {
                _uiState.update { it.copy(statusText = "Face detected successfully") }
                handleGoodFace(result, now)
            }
        }
    }

    private fun handleNonGoodFace() {
        consecutiveGoodTicks = 0
        consecutiveNoFaceTicks++
        // One missed frame used to wipe a good sample set, so Capture posted a single weak scan.
        if (consecutiveNoFaceTicks >= 3) {
            descriptorBuffer.clear()
        }

        // In open kiosk mode (no logged-in user), reset suggestion to IN and last activity to "--"
        // In personalized login mode, keep the user's active location status.
        if (consecutiveNoFaceTicks >= 12 && _uiState.value.deviceToken.isBlank()) {
            val isCachedToday = cachedAttendance?.isToday() == true
            if (!isCachedToday) {
                _uiState.update {
                    it.copy(
                        activeToggle = "IN",
                        lastActivityText = "--"
                    )
                }
            }
        }

    }

    private fun handleGoodFace(result: FaceDetectionResult, now: Long) {
        consecutiveNoFaceTicks = 0
        consecutiveGoodTicks++

        descriptorBuffer.add(result.descriptor)
        if (descriptorBuffer.size > 6) {
            descriptorBuffer.removeAt(0)
        }

        // On good face: at most every 2500 ms call derive_location_preview including descriptor
        if (now - lastDeriveWithDescriptorTime >= 2500L) {
            lastDeriveWithDescriptorTime = now
            scheduleDerivePreview(includeDescriptor = true, debounceMs = 0L)
        }

        // Auto-submit (kiosk):
        // when not posting, 5 s cooldown passed, lat/lng present, location picker hidden or a value selected,
        // buffer has 3 samples -> after 4 consecutive good ticks call store, then cooldown 5 s.
        val state = _uiState.value
        val cooldownPassed = (now - lastSubmitTime) >= 5000L
        val hasCoords = state.currentLat != null && state.currentLng != null && !state.isLocationLoading
        val locationReady = !state.showLocationPicker || state.selectedLocation != null
        val bufferReady = descriptorBuffer.size >= 4

        if (state.isAutoSubmitEnabled && state.isOnline && state.cameraAllowed && state.locationAllowed && state.gpsEnabled && !state.isPosting && state.capturedBitmap == null && cooldownPassed && hasCoords && locationReady && bufferReady && consecutiveGoodTicks >= 4) {
            submitAttendance(isAuto = true)
        }
    }

    private fun scheduleDerivePreview(includeDescriptor: Boolean, debounceMs: Long) {
        deriveDebounceJob?.cancel()
        deriveDebounceJob = viewModelScope.launch {
            if (debounceMs > 0) delay(debounceMs)
            executeDerivePreview(includeDescriptor)
        }
    }

    private suspend fun executeDerivePreview(includeDescriptor: Boolean) {
        val state = _uiState.value
        if (state.isPosting) return
        val lat = state.currentLat ?: return
        val lng = state.currentLng ?: return
        if (state.deviceToken.isBlank()) return

        try {
            val tokenInfo = networkClientManager.getOrFetchToken(state.baseUrl)
            val token = tokenInfo.token ?: ""

            val descriptorJson: String? = if (includeDescriptor && descriptorBuffer.isNotEmpty()) {
                val averaged = averageDescriptors(descriptorBuffer)
                formatDescriptorJson(averaged)
            } else null

            val selectedLocationName = state.selectedLocation?.pureName() ?: ""
            val url = "${networkClientManager.sanitizeBaseUrl(state.baseUrl)}attendance_api/derive_location_preview"

            val response = networkClientManager.attendanceService.deriveLocationPreview(
                url = url,
                token = token,
                latitude = lat.toString(),
                longitude = lng.toString(),
                landmark = state.currentLandmark,
                selectedDerivedLocation = selectedLocationName,
                descriptor = descriptorJson
            )

            if (response.code() == 403) {
                // Token might be expired, refetch token and retry once
                val refreshed = networkClientManager.getOrFetchToken(state.baseUrl, forceRefresh = true)
                executeDerivePreview(includeDescriptor)
                return
            }

            if (response.isSuccessful) {
                val data = response.body()
                if (data != null && data.status.equals("success", ignoreCase = true)) {
                    processDeriveResponse(data)
                }
            } else if (response.code() == 401) {
                logout()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processDeriveResponse(data: DeriveLocationPreviewResponse) {
        val options = (data.locationOptions ?: emptyList()).sortedByDistance()
        val requiresSelection = data.isLocationSelectionRequired()
        val manual = _uiState.value.locationChosenManually
        val previous = _uiState.value.selectedLocation
        val previousMatch = previous?.let { selected ->
            options.firstOrNull { it.pureName() == selected.pureName() }
        }

        var showPicker = false
        var newSelected = previousMatch
        var rederive = false

        if (options.size == 1) {
            val only = options[0]
            showPicker = true
            newSelected = only
            rederive = previous?.pureName() != only.pureName()
        } else if (requiresSelection || options.size >= 2) {
            showPicker = true
            if (manual && previousMatch != null) {
                newSelected = previousMatch
            } else {
                val first = options.first()
                newSelected = first
                rederive = previous?.pureName() != first.pureName()
            }
        } else {
            showPicker = false
            if (!manual) newSelected = null
        }

        // Determine Last activity text and Toggle
        val activity = formatLastActivity(
            lastType = data.lastActivityType,
            lastAt = data.lastActivityAt,
            selectedOutCheckinAt = data.selectedOutCheckinAt,
            selectedInCheckoutAt = data.selectedInCheckoutAt
        )

        val toggle = when {
            data.suggestedAttendanceType.equals("check_in", ignoreCase = true) -> "IN"
            data.suggestedAttendanceType.equals("check_out", ignoreCase = true) -> "OUT"
            activity.toggle != null -> activity.toggle
            else -> _uiState.value.activeToggle
        }

        _uiState.update {
            it.copy(
                locationOptions = options,
                selectedLocation = newSelected,
                showLocationPicker = showPicker,
                lastActivityText = activity.text,
                activeToggle = toggle
            )
        }

        if (rederive) {
            scheduleDerivePreview(includeDescriptor = false, debounceMs = 200L)
        }

        // Cache last activity if present
        if (data.lastActivityType != null && data.lastActivityAt != null) {
            viewModelScope.launch {
                preferencesRepository.saveCachedAttendance(
                    lastType = data.lastActivityType,
                    lastAt = data.lastActivityAt,
                    nextAction = toggle
                )
            }
        }
    }

    fun onCaptureButtonClicked() {
        val state = _uiState.value
        if (state.isPosting) return

        if (state.currentLat == null || state.currentLng == null || state.isLocationLoading) {
            showToast(ToastType.INFO, "Acquiring location. Please wait...", 2500L)
            return
        }

        if (state.capturedBitmap != null || !state.isCameraActive || state.buttonState == AttendanceButtonState.RETAKE) {
            resetCapturedJob?.cancel()
            _uiState.update {
                it.copy(
                    isCameraActive = true,
                    isCameraFailed = false,
                    capturedBitmap = null,
                    buttonState = AttendanceButtonState.CAPTURE,
                    statusText = "Face detection ready."
                )
            }
            return
        }

        // Wait for several steady samples so one blurry frame is not what gets matched.
        val validCount = synchronized(descriptorBuffer) { descriptorBuffer.count { it.size == 128 } }
        if (validCount >= 4) {
            submitAttendance(isAuto = false)
            return
        }

        _uiState.update { it.copy(buttonState = AttendanceButtonState.SCANNING, statusText = "Hold still, matching your face...") }
        scanningTimeoutJob?.cancel()
        scanningTimeoutJob = viewModelScope.launch {
            val deadline = System.currentTimeMillis() + 2200L
            while (System.currentTimeMillis() < deadline) {
                val ready = synchronized(descriptorBuffer) { descriptorBuffer.count { it.size == 128 } }
                if (ready >= 4) break
                delay(200L)
            }
            if (_uiState.value.buttonState != AttendanceButtonState.SCANNING) return@launch
            val currentValid = synchronized(descriptorBuffer) { descriptorBuffer.count { it.size == 128 } }
            if (currentValid == 0) {
                val locationSuffix = _uiState.value.selectedLocation?.let { " (Selected: ${it.pureName()})" } ?: ""
                showToast(ToastType.ERROR, "No face detected in camera. Please face the camera.$locationSuffix", 4000L)
                _uiState.update { it.copy(buttonState = AttendanceButtonState.CAPTURE, statusText = "No face detected") }
            } else {
                submitAttendance(isAuto = false)
            }
        }
    }

    private fun submitAttendance(isAuto: Boolean) {
        val state = _uiState.value
        if (state.isPosting) return
        if (state.deviceToken.isBlank()) {
            showToast(ToastType.ERROR, "Log in with your phone number before marking attendance.", 4000L)
            return
        }

        val validDescriptors = descriptorBuffer.filter { it.size == 128 }
        if (validDescriptors.isEmpty()) {
            showToast(ToastType.ERROR, "No face detected. Please face the camera directly.", 4000L)
            _uiState.update { it.copy(buttonState = AttendanceButtonState.CAPTURE, statusText = "No face detected") }
            return
        }

        if (state.currentLat == null || state.currentLng == null) {
            val locationSuffix = state.selectedLocation?.let { " (Selected: ${it.pureName()})" } ?: ""
            showToast(ToastType.ERROR, "GPS location is not available.$locationSuffix", 4000L)
            triggerLocationAndDerive()
            return
        }

        if (!networkClientManager.isOnline()) {
            saveOfflinePunch(state, validDescriptors)
            return
        }

        // Shutter matches the frozen photo, not the later server reply.
        playCaptureSound()
        _uiState.update {
            it.copy(
                isPosting = true,
                buttonState = AttendanceButtonState.SUBMITTING,
                capturedBitmap = latestBitmap ?: it.capturedBitmap
            )
        }

        val knownLat = state.currentLat ?: return
        val knownLng = state.currentLng ?: return
        var knownLandmark = state.currentLandmark

        viewModelScope.launch {
            try {
                if (knownLandmark.isBlank()) {
                    val resolved = locationHelper.reverseGeocode(knownLat, knownLng)
                    if (resolved.isNotBlank()) {
                        knownLandmark = resolved
                        _uiState.update { it.copy(currentLandmark = resolved) }
                    }
                }

                val avgDescriptor = averageDescriptors(validDescriptors)
                val descriptorJson = formatDescriptorJson(avgDescriptor)

                val attendanceType = if (state.activeToggle == "OUT") "check_out" else "check_in"
                val selectedLoc = _uiState.value.selectedLocation?.pureName() ?: ""
                val url = "${networkClientManager.sanitizeBaseUrl(state.baseUrl)}attendance_api/store"

                val response = networkClientManager.attendanceService.storeAttendance(
                    url = url,
                    token = null,
                    descriptor = descriptorJson,
                    latitude = knownLat.toString(),
                    longitude = knownLng.toString(),
                    landmark = knownLandmark,
                    notes = state.notes,
                    attendanceType = attendanceType,
                    selectedDerivedLocation = selectedLoc
                )

                lastSubmitTime = System.currentTimeMillis()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.status.equals("success", ignoreCase = true)) {
                        handleStoreSuccess(body)
                    } else {
                        val errMsg = body?.message ?: "Could not mark attendance."
                        handleStoreError(errMsg)
                    }
                } else {
                    val errBody = response.errorBody()?.string() ?: ""
                    val parsedMsg = try {
                        val json = JSONObject(errBody)
                        json.optString("message", json.optString("error", "Request failed (${response.code()})"))
                    } catch (e: Exception) {
                        when (response.code()) {
                            401 -> "Session expired. Please log in again."
                            403 -> "Access denied (403)"
                            404 -> "API endpoint not found. Please check Server Base URL"
                            500 -> "Server error (500). Please check server logs"
                            else -> "Server error (HTTP ${response.code()})"
                        }
                    }
                    handleStoreError(parsedMsg)
                    if (response.code() == 401) {
                        logout()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                lastSubmitTime = System.currentTimeMillis()
                val offline = e is java.net.ConnectException ||
                    e is java.net.SocketTimeoutException ||
                    e is java.net.UnknownHostException ||
                    !networkClientManager.isOnline()
                if (offline) {
                    saveOfflinePunch(state, validDescriptors, alreadyCaptured = true)
                    return@launch
                }
                handleStoreError(e.localizedMessage ?: "Network request failed.")
            }
        }
    }

    private fun saveOfflinePunch(
        state: AttendanceUiState,
        descriptors: List<List<Float>>,
        alreadyCaptured: Boolean = false
    ) {
        val lat = state.currentLat ?: return
        val lng = state.currentLng ?: return
        val valid = descriptors.filter { it.size == 128 }
        if (valid.isEmpty()) return
        val attendanceType = if (state.activeToggle == "OUT") "check_out" else "check_in"
        val punchedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val siteName = nearestSiteName(lat, lng).ifBlank { state.selectedLocation?.pureName().orEmpty() }
        viewModelScope.launch {
            val item = JSONObject()
            item.put("id", System.currentTimeMillis().toString())
            item.put("descriptor", formatDescriptorJson(averageDescriptors(valid)))
            item.put("latitude", lat.toString())
            item.put("longitude", lng.toString())
            item.put("landmark", state.currentLandmark)
            item.put("notes", state.notes)
            item.put("attendanceType", attendanceType)
            item.put("selectedLoc", siteName)
            item.put("baseUrl", state.baseUrl)
            item.put("punchedAt", punchedAt)
            offlineMutex.withLock {
                val queue = JSONArray(preferencesRepository.readOfflineQueue().ifBlank { "[]" })
                queue.put(item)
                preferencesRepository.saveOfflineQueue(queue.toString())
            }
            if (!alreadyCaptured) {
                playCaptureSound()
            }
            val place = siteName.ifBlank { "this GPS point" }
            val verb = if (attendanceType == "check_out") "Check-out" else "Check-in"
            val message = "$verb saved offline at $place. It will sync when internet is back."
            showToast(ToastType.SUCCESS, message, 6000L)
            val nextToggle = if (attendanceType == "check_in") "OUT" else "IN"
            _uiState.update {
                it.copy(
                    isOnline = false,
                    isPosting = false,
                    waitingToSend = true,
                    buttonState = AttendanceButtonState.RETAKE,
                    notes = "",
                    activeToggle = nextToggle,
                    lastActivityText = "$verb $punchedAt (offline)",
                    statusText = message,
                    capturedBitmap = latestBitmap ?: it.capturedBitmap
                )
            }
            preferencesRepository.saveCachedAttendance(
                lastType = attendanceType,
                lastAt = punchedAt,
                nextAction = nextToggle
            )
        }
    }

    private fun sendPendingPunch() {
        if (!networkClientManager.isOnline() || _uiState.value.deviceToken.isBlank()) return
        viewModelScope.launch {
            val start = offlineMutex.withLock {
                if (offlineSyncRunning) {
                    false
                } else {
                    offlineSyncRunning = true
                    true
                }
            }
            if (!start) return@launch
            try {
                refreshSites(_uiState.value.baseUrl)
                val raw = offlineMutex.withLock { preferencesRepository.readOfflineQueue() }
                val queue = JSONArray(raw.ifBlank { "[]" })
                if (queue.length() == 0) {
                    _uiState.update { it.copy(waitingToSend = false, isPosting = false) }
                    return@launch
                }
                _uiState.update { it.copy(isPosting = true, buttonState = AttendanceButtonState.SUBMITTING) }
                val syncedIds = mutableSetOf<String>()
                var lastSuccess: StoreAttendanceResponse? = null
                var failed = false
                for (index in 0 until queue.length()) {
                    val item = queue.getJSONObject(index)
                    try {
                        val url = "${networkClientManager.sanitizeBaseUrl(item.optString("baseUrl", _uiState.value.baseUrl))}attendance_api/store"
                        val response = networkClientManager.attendanceService.storeAttendance(
                            url = url,
                            token = null,
                            descriptor = item.optString("descriptor"),
                            latitude = item.optString("latitude"),
                            longitude = item.optString("longitude"),
                            landmark = item.optString("landmark"),
                            notes = item.optString("notes"),
                            attendanceType = item.optString("attendanceType", "check_in"),
                            selectedDerivedLocation = item.optString("selectedLoc"),
                            punchedAt = item.optString("punchedAt")
                        )
                        if (response.isSuccessful && response.body()?.status.equals("success", ignoreCase = true)) {
                            syncedIds.add(item.optString("id"))
                            lastSuccess = response.body()
                        } else {
                            failed = true
                            break
                        }
                    } catch (_: Exception) {
                        failed = true
                        break
                    }
                }
                val stillQueued = offlineMutex.withLock {
                    val latest = JSONArray(preferencesRepository.readOfflineQueue().ifBlank { "[]" })
                    val keep = JSONArray()
                    for (index in 0 until latest.length()) {
                        val row = latest.getJSONObject(index)
                        if (!syncedIds.contains(row.optString("id"))) {
                            keep.put(row)
                        }
                    }
                    preferencesRepository.saveOfflineQueue(keep.toString())
                    keep.length() > 0
                }
                val success = lastSuccess
                if (success != null && !failed && !stillQueued) {
                    handleStoreSuccess(success)
                } else if (success != null) {
                    _uiState.update { it.copy(isPosting = false, waitingToSend = stillQueued, buttonState = AttendanceButtonState.CAPTURE) }
                    showToast(ToastType.INFO, "Some offline attendance synced. The rest will send when internet is stable.", 5000L)
                } else {
                    _uiState.update {
                        it.copy(
                            isPosting = false,
                            isOnline = networkClientManager.isOnline(),
                            waitingToSend = stillQueued,
                            buttonState = AttendanceButtonState.CAPTURE
                        )
                    }
                }
            } finally {
                offlineMutex.withLock { offlineSyncRunning = false }
            }
        }
    }

    private suspend fun refreshSites(baseUrl: String) {
        if (!networkClientManager.isOnline() || _uiState.value.deviceToken.isBlank()) return
        try {
            val url = "${networkClientManager.sanitizeBaseUrl(baseUrl)}attendance_api/sites"
            val response = networkClientManager.attendanceService.getSites(url)
            val body = if (response.isSuccessful) response.body()?.string().orEmpty() else ""
            if (body.isBlank()) return
            val sites = JSONObject(body).optJSONArray("sites") ?: return
            preferencesRepository.saveCachedSites(sites.toString())
            cachedSites = parseSites(sites.toString())
        } catch (_: Exception) {
        }
    }

    private fun parseSites(json: String): List<CachedSite> {
        if (json.isBlank()) return emptyList()
        return try {
            val array = JSONArray(json)
            buildList {
                for (index in 0 until array.length()) {
                    val site = array.optJSONObject(index) ?: continue
                    val name = site.optString("name")
                    if (name.isBlank()) continue
                    add(
                        CachedSite(
                            name = name,
                            lat = site.optDouble("latitude"),
                            lng = site.optDouble("longitude"),
                            radiusM = site.optInt("radius_m", 0)
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun nearestSiteName(lat: Double, lng: Double): String {
        var bestName = ""
        var bestDistance = Double.MAX_VALUE
        for (site in cachedSites) {
            if (site.radiusM <= 0) continue
            val distance = distanceMeters(lat, lng, site.lat, site.lng)
            if (distance <= site.radiusM && distance < bestDistance) {
                bestName = site.name
                bestDistance = distance
            }
        }
        return bestName
    }

    private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earth = 6371000.0
        val latDelta = Math.toRadians(lat2 - lat1)
        val lonDelta = Math.toRadians(lon2 - lon1)
        val a = sin(latDelta / 2) * sin(latDelta / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(lonDelta / 2) * sin(lonDelta / 2)
        return 2 * earth * asin(sqrt(a))
    }

    private fun handleStoreSuccess(res: StoreAttendanceResponse) {
        val successMsg = res.message ?: "Attendance marked"
        showToast(ToastType.SUCCESS, successMsg, 7000L)

        val nextToggle = when {
            res.nextAction.equals("check_in", ignoreCase = true) || res.nextAction.equals("IN", ignoreCase = true) -> "IN"
            res.nextAction.equals("check_out", ignoreCase = true) || res.nextAction.equals("OUT", ignoreCase = true) -> "OUT"
            res.action.equals("check_in", ignoreCase = true) -> "OUT"
            res.action.equals("check_out", ignoreCase = true) -> "IN"
            _uiState.value.activeToggle == "IN" -> "OUT"
            else -> "IN"
        }

        val activity = formatLastActivity(
            lastType = res.lastActivityType ?: res.action,
            lastAt = res.lastActivityAt,
            selectedOutCheckinAt = null,
            selectedInCheckoutAt = null
        )

        _uiState.update {
            it.copy(
                isPosting = false,
                waitingToSend = false,
                buttonState = AttendanceButtonState.RETAKE,
                notes = "", // Clear notes
                activeToggle = nextToggle,
                lastActivityText = activity.text,
                statusText = successMsg
            )
        }

        // Keep captured image visible for 4.5 seconds after submit, then auto-reset for next person
        resetCapturedJob?.cancel()
        resetCapturedJob = viewModelScope.launch {
            delay(4500L)
            _uiState.update {
                it.copy(
                    capturedBitmap = null,
                    buttonState = AttendanceButtonState.CAPTURE,
                    statusText = "Face detection ready."
                )
            }
        }

        // Save cache
        viewModelScope.launch {
            preferencesRepository.saveCachedAttendance(
                lastType = res.lastActivityType ?: res.action,
                lastAt = res.lastActivityAt,
                nextAction = nextToggle
            )
        }
        syncAreaTracking(res)

        // Clear descriptor buffer after successful submission
        descriptorBuffer.clear()
        consecutiveGoodTicks = 0
    }

    private fun syncAreaTracking(res: StoreAttendanceResponse) {
        val app = getApplication<Application>()
        val action = res.action.orEmpty()
        if (action.equals("check_out", ignoreCase = true)) {
            AreaTrackService.stop(app)
            _uiState.update { it.copy(areaTracking = false) }
            return
        }
        if (!action.equals("check_in", ignoreCase = true)) return
        var name = res.derivedLocation?.trim().orEmpty()
        var lat = res.siteLatitude
        var lng = res.siteLongitude
        var radius = res.radiusM ?: 0
        if (name.isEmpty()) {
            name = _uiState.value.selectedLocation?.pureName().orEmpty()
        }
        if (lat == null || lng == null || radius <= 0) {
            val site = cachedSites.firstOrNull { it.name.equals(name, ignoreCase = true) }
            if (site != null) {
                lat = site.lat
                lng = site.lng
                if (radius <= 0) radius = site.radiusM
            }
        }
        if (name.isEmpty() || lat == null || lng == null || radius <= 0) return
        val token = _uiState.value.deviceToken
        val base = networkClientManager.sanitizeBaseUrl(_uiState.value.baseUrl)
        if (token.isBlank() || base.isBlank()) return
        AreaTrackService.start(
            app,
            AreaWatch(
                baseUrl = base,
                token = token,
                locationName = name,
                latitude = lat,
                longitude = lng,
                radiusM = radius,
                inside = true
            )
        )
        _uiState.update { it.copy(areaTracking = true) }
    }

    private fun handleStoreError(rawMessage: String) {
        val isMultipleLocs = rawMessage.contains("Multiple nearby locations found", ignoreCase = true)

        if (isMultipleLocs) {
            // Just show picker without error toast
            _uiState.update {
                it.copy(
                    isPosting = false,
                    buttonState = AttendanceButtonState.CAPTURE,
                    showLocationPicker = true
                )
            }
            scheduleDerivePreview(includeDescriptor = false, debounceMs = 100L)
            return
        }

        val locationSuffix = _uiState.value.selectedLocation?.let { " (Selected: ${it.pureName()})" } ?: ""
        showToast(ToastType.ERROR, "$rawMessage$locationSuffix", 6000L)

        _uiState.update {
            it.copy(
                isPosting = false,
                buttonState = AttendanceButtonState.RETAKE,
                statusText = rawMessage
            )
        }

        resetCapturedJob?.cancel()
        resetCapturedJob = viewModelScope.launch {
            delay(3500L)
            _uiState.update {
                it.copy(
                    capturedBitmap = null,
                    buttonState = AttendanceButtonState.CAPTURE
                )
            }
        }
    }

    private fun formatLastActivity(
        lastType: String?,
        lastAt: String?,
        selectedOutCheckinAt: String?,
        selectedInCheckoutAt: String?
    ): FormattedActivity {
        return when {
            lastType.equals("check_out", ignoreCase = true) && !lastAt.isNullOrBlank() -> {
                FormattedActivity("Check-out at $lastAt", toggle = "IN")
            }
            lastType.equals("check_in", ignoreCase = true) && !lastAt.isNullOrBlank() -> {
                FormattedActivity("Check-in at $lastAt", toggle = "OUT")
            }
            !selectedOutCheckinAt.isNullOrBlank() -> {
                FormattedActivity("Check-in at $selectedOutCheckinAt", toggle = "OUT")
            }
            !selectedInCheckoutAt.isNullOrBlank() -> {
                FormattedActivity("Check-out at $selectedInCheckoutAt", toggle = "IN")
            }
            else -> {
                FormattedActivity("--", toggle = null)
            }
        }
    }

    private data class FormattedActivity(val text: String, val toggle: String?)

    private fun averageDescriptors(buffer: List<List<Float>>): List<Float> {
        val list = synchronized(buffer) { buffer.filter { it.size == 128 } }
        if (list.isEmpty()) return emptyList()
        val samples = dropOutlierDescriptors(list)
        val dim = 128
        val result = FloatArray(dim)
        for (desc in samples) {
            for (i in 0 until dim) {
                result[i] += desc[i]
            }
        }
        val count = samples.size.toFloat()
        var normSq = 0.0
        for (i in 0 until dim) {
            result[i] /= count
            normSq += result[i] * result[i]
        }
        val norm = kotlin.math.sqrt(normSq).toFloat()
        if (norm > 1e-6f) {
            for (i in 0 until dim) {
                result[i] /= norm
            }
        }
        return result.toList()
    }

    /** Drop a turned-away frame so it cannot pull the average off the registered face. */
    private fun dropOutlierDescriptors(list: List<List<Float>>): List<List<Float>> {
        if (list.size <= 2) return list
        val median = FloatArray(128)
        for (i in 0 until 128) {
            val column = list.map { it[i] }.sorted()
            median[i] = column[column.size / 2]
        }
        val kept = list.filter { euclideanTo(it, median) <= 0.42f }
        return if (kept.size >= 2) kept else list
    }

    private fun euclideanTo(sample: List<Float>, center: FloatArray): Float {
        var sum = 0.0
        for (i in 0 until 128) {
            val diff = sample[i] - center[i]
            sum += diff * diff
        }
        return kotlin.math.sqrt(sum).toFloat()
    }

    private fun formatDescriptorJson(descriptor: List<Float>): String {
        return descriptor.joinToString(prefix = "[", postfix = "]", separator = ",") {
            String.format(Locale.US, "%.6f", it)
        }
    }

    fun showToast(type: ToastType, message: String, durationMs: Long) {
        toastDismissJob?.cancel()
        _uiState.update { it.copy(toast = ToastMessage(type, message, durationMs)) }
        toastDismissJob = viewModelScope.launch {
            delay(durationMs)
            _uiState.update { it.copy(toast = null) }
        }
    }

    fun dismissToast() {
        toastDismissJob?.cancel()
        _uiState.update { it.copy(toast = null) }
    }
}
