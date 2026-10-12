package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datastore.AttendancePreferencesRepository
import com.example.data.datastore.CachedAttendanceData
import com.example.data.model.AttendanceButtonState
import com.example.data.model.DeriveLocationPreviewResponse
import com.example.data.model.LocationOption
import com.example.data.model.StoreAttendanceResponse
import com.example.data.model.ToastMessage
import com.example.data.model.ToastType
import com.example.data.network.NetworkClientManager
import com.example.face.FaceDetectionResult
import com.example.location.LocationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import org.json.JSONObject
import java.util.Collections
import java.util.Locale

data class AttendanceUiState(
    val buttonState: AttendanceButtonState = AttendanceButtonState.CAPTURE,
    val statusText: String = "Camera active. Loading face model...",
    val activeToggle: String = "IN", // "IN" or "OUT"
    val lastActivityText: String = "--",
    val notes: String = "",
    val locationOptions: List<LocationOption> = emptyList(),
    val selectedLocation: LocationOption? = null,
    val showLocationPicker: Boolean = false,
    val toast: ToastMessage? = null,
    val tenantLogoUrl: String? = null,
    val tenantSiteName: String? = null,
    val baseUrl: String = AttendancePreferencesRepository.DEFAULT_BASE_URL,
    val apiKey: String = "",
    val isCameraActive: Boolean = true,
    val isCameraFailed: Boolean = false,
    val isModelReady: Boolean = false,
    val currentLat: Double? = null,
    val currentLng: Double? = null,
    val currentLandmark: String = "",
    val isPosting: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val capturedBitmap: Bitmap? = null,
    val isAutoSubmitEnabled: Boolean = true
)

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {

    val networkClientManager = NetworkClientManager(application)
    val preferencesRepository = AttendancePreferencesRepository(application)
    val locationHelper = LocationHelper(application, networkClientManager.nominatimService)

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    private val descriptorBuffer = Collections.synchronizedList(mutableListOf<List<Float>>())
    private var consecutiveGoodTicks = 0
    private var consecutiveNoFaceTicks = 0
    private var lastSubmitTime = 0L
    private var lastDeriveWithDescriptorTime = 0L

    private var cachedAttendance: CachedAttendanceData? = null
    private var deriveDebounceJob: Job? = null
    private var toastDismissJob: Job? = null
    private var scanningTimeoutJob: Job? = null
    private var resetCapturedJob: Job? = null
    private var latestBitmap: Bitmap? = null

    fun onLatestFrame(bitmap: Bitmap) {
        latestBitmap = bitmap
    }

    init {
        loadPreferencesAndBootstrap()
    }

    private fun loadPreferencesAndBootstrap() {
        viewModelScope.launch {
            val base = preferencesRepository.tenantBaseUrlFlow.first()
            val key = preferencesRepository.tenantApiKeyFlow.first()
            val autoSubmit = preferencesRepository.autoSubmitFlow.first()
            networkClientManager.currentApiKey = key
            _uiState.update { it.copy(baseUrl = base, apiKey = key, isAutoSubmitEnabled = autoSubmit) }

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

            // Fetch captured page (CSRF, cookies, logo, site name)
            fetchCapturedPage(base)
        }
    }

    fun onSettingsClicked() {
        _uiState.update { it.copy(showSettingsDialog = true) }
    }

    fun dismissSettingsDialog() {
        _uiState.update { it.copy(showSettingsDialog = false) }
    }

    fun updateSettings(newUrl: String, newApiKey: String, autoSubmit: Boolean = true) {
        viewModelScope.launch {
            val sanitized = networkClientManager.sanitizeBaseUrl(newUrl)
            val trimmedKey = newApiKey.trim()
            preferencesRepository.setTenantSettings(sanitized, trimmedKey, autoSubmit)
            networkClientManager.currentApiKey = trimmedKey
            _uiState.update {
                it.copy(
                    baseUrl = sanitized,
                    apiKey = trimmedKey,
                    isAutoSubmitEnabled = autoSubmit,
                    showSettingsDialog = false
                )
            }
            fetchCapturedPage(sanitized, force = true)
            triggerLocationAndDerive()
        }
    }

    fun updateBaseUrl(newUrl: String) {
        updateSettings(newUrl, _uiState.value.apiKey, _uiState.value.isAutoSubmitEnabled)
    }

    fun testConnection(url: String, apiKey: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val sanitized = networkClientManager.sanitizeBaseUrl(url)
            try {
                val apiUrl = "${sanitized}attendance_api/init"
                val oldKey = networkClientManager.currentApiKey
                networkClientManager.currentApiKey = apiKey.trim()
                val response = networkClientManager.attendanceService.getInitData(apiUrl)
                networkClientManager.currentApiKey = oldKey // restore until saved

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
            val locResult = locationHelper.getLocation()
            locResult.onSuccess { loc ->
                _uiState.update {
                    it.copy(
                        currentLat = loc.latitude,
                        currentLng = loc.longitude
                    )
                }
                // Reverse geocode via Nominatim
                val landmark = locationHelper.reverseGeocode(loc.latitude, loc.longitude)
                _uiState.update { it.copy(currentLandmark = landmark) }

                // Call derive location preview (debounce 350 ms)
                scheduleDerivePreview(includeDescriptor = false, debounceMs = 350L)
            }.onFailure { err ->
                val errorMsg = err.message ?: "Location unavailable. Please check location permissions."
                showToast(ToastType.ERROR, errorMsg, 5500L)
            }
        }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onToggleClicked(toggle: String) {
        _uiState.update { it.copy(activeToggle = toggle) }
    }

    fun onLocationSelected(option: LocationOption) {
        _uiState.update { it.copy(selectedLocation = option) }
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
            result.score < 0.65 -> {
                _uiState.update { it.copy(statusText = "Face detected, hold still for better accuracy") }
                handleNonGoodFace()
            }
            result.descriptor.size != 128 -> {
                _uiState.update { it.copy(statusText = "Scanning face features...") }
                handleNonGoodFace()
            }
            else -> {
                // Exactly 1 good face with score >= 0.65 and 128 points
                _uiState.update { it.copy(statusText = "Face detected successfully") }
                handleGoodFace(result, now)
            }
        }
    }

    private fun handleNonGoodFace() {
        descriptorBuffer.clear()
        consecutiveGoodTicks = 0
        consecutiveNoFaceTicks++

        // After 12 consecutive no/multiple ticks reset suggestion to IN and last activity to "--" unless today's cache exists
        if (consecutiveNoFaceTicks >= 12) {
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
        if (descriptorBuffer.size > 3) {
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
        val hasCoords = state.currentLat != null && state.currentLng != null
        val locationReady = !state.showLocationPicker || state.selectedLocation != null
        val bufferReady = descriptorBuffer.size >= 3

        if (state.isAutoSubmitEnabled && !state.isPosting && state.capturedBitmap == null && cooldownPassed && hasCoords && locationReady && bufferReady && consecutiveGoodTicks >= 4) {
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
                if (data != null) {
                    processDeriveResponse(data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processDeriveResponse(data: DeriveLocationPreviewResponse) {
        val options = data.locationOptions ?: emptyList()
        val requiresSelection = data.isLocationSelectionRequired()

        var showPicker = false
        var newSelected = _uiState.value.selectedLocation

        if (requiresSelection || options.size >= 2) {
            showPicker = true
            // Keep previous selection, otherwise auto-select first once and re-call derive
            if (newSelected == null && options.isNotEmpty()) {
                newSelected = options[0]
                _uiState.update { it.copy(selectedLocation = newSelected) }
                scheduleDerivePreview(includeDescriptor = false, debounceMs = 200L)
            }
        } else if (options.size == 1) {
            // 1 option -> auto-select hidden
            showPicker = false
            newSelected = options[0]
        } else {
            // Empty options or not required
            showPicker = false
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

        // If valid 128-point face ready, post now
        val validDescriptors = descriptorBuffer.filter { it.size == 128 }
        if (validDescriptors.isNotEmpty()) {
            submitAttendance(isAuto = false)
            return
        }

        // Else "Scanning...", detect one frame
        _uiState.update { it.copy(buttonState = AttendanceButtonState.SCANNING, statusText = "Scanning for face...") }
        scanningTimeoutJob?.cancel()
        scanningTimeoutJob = viewModelScope.launch {
            delay(1500L)
            if (_uiState.value.buttonState == AttendanceButtonState.SCANNING) {
                val currentValid = descriptorBuffer.filter { it.size == 128 }
                if (currentValid.isEmpty()) {
                    val locationSuffix = _uiState.value.selectedLocation?.let { " (Selected: ${it.pureName()})" } ?: ""
                    showToast(ToastType.ERROR, "No face detected in camera. Please face the camera.$locationSuffix", 4000L)
                    _uiState.update { it.copy(buttonState = AttendanceButtonState.CAPTURE, statusText = "No face detected") }
                } else {
                    submitAttendance(isAuto = false)
                }
            }
        }
    }

    private fun submitAttendance(isAuto: Boolean) {
        val state = _uiState.value
        if (state.isPosting) return

        val validDescriptors = descriptorBuffer.filter { it.size == 128 }
        if (validDescriptors.isEmpty()) {
            showToast(ToastType.ERROR, "No face detected. Please face the camera directly.", 4000L)
            _uiState.update { it.copy(buttonState = AttendanceButtonState.CAPTURE, statusText = "No face detected") }
            return
        }

        if (state.currentLat == null || state.currentLng == null) {
            val locationSuffix = state.selectedLocation?.let { " (Selected: ${it.pureName()})" } ?: ""
            showToast(ToastType.ERROR, "GPS coordinates unavailable. Please enable device location.$locationSuffix", 4000L)
            triggerLocationAndDerive()
            return
        }

        _uiState.update {
            it.copy(
                isPosting = true,
                buttonState = AttendanceButtonState.SUBMITTING,
                capturedBitmap = latestBitmap ?: it.capturedBitmap
            )
        }

        viewModelScope.launch {
            try {
                val tokenInfo = networkClientManager.getOrFetchToken(state.baseUrl)
                val token = tokenInfo.token ?: ""

                val avgDescriptor = averageDescriptors(validDescriptors)
                val descriptorJson = formatDescriptorJson(avgDescriptor)

                // attendance_type: "check_in" when IN, "check_out" when OUT
                val attendanceType = if (state.activeToggle == "OUT") "check_out" else "check_in"
                val selectedLoc = state.selectedLocation?.pureName() ?: ""

                val url = "${networkClientManager.sanitizeBaseUrl(state.baseUrl)}attendance_api/store"

                val response = networkClientManager.attendanceService.storeAttendance(
                    url = url,
                    token = token,
                    descriptor = descriptorJson,
                    latitude = state.currentLat.toString(),
                    longitude = state.currentLng.toString(),
                    landmark = state.currentLandmark,
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
                            401 -> "Unauthorized: Invalid or missing API Key"
                            403 -> "Access denied (403)"
                            404 -> "API endpoint not found. Please check Server Base URL"
                            500 -> "Server error (500). Please check server logs"
                            else -> "Server error (HTTP ${response.code()})"
                        }
                    }
                    handleStoreError(parsedMsg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                lastSubmitTime = System.currentTimeMillis()
                val errorMsg = when {
                    e is java.net.ConnectException -> "Cannot connect to server. Check IP & port in settings."
                    e is java.net.SocketTimeoutException -> "Server connection timed out."
                    e is java.net.UnknownHostException -> "Server host not found. Check WiFi / URL."
                    else -> e.localizedMessage ?: "Network request failed."
                }
                handleStoreError(errorMsg)
            }
        }
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

        // Clear descriptor buffer after successful submission
        descriptorBuffer.clear()
        consecutiveGoodTicks = 0
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
        val list = synchronized(buffer) { buffer.toList() }
        if (list.isEmpty()) return emptyList()
        val dim = list[0].size
        val count = list.size.toFloat()
        val result = FloatArray(dim)
        for (desc in list) {
            for (i in 0 until minOf(dim, desc.size)) {
                result[i] += desc[i]
            }
        }
        return result.map { it / count }
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
