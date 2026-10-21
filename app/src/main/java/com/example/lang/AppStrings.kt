package com.example.lang

/**
 * Interface contract defining all UI text, banners, toasts,
 * and HUD messages for the Attendance Kiosk application.
 */
interface AppStrings {
    val langCode: String
    val langName: String

    // Action buttons
    val allow: String
    val settings: String
    val turnOnGps: String

    // Permission & system banner messages
    val cameraPermissionOff: String
    val locationPermissionOff: String
    val gpsTurnedOff: String
    val noInternetWaiting: String
    val noInternet: String
    val internetRestoredSending: String

    // Camera HUD / Face Guide overlays
    val capturedChip: String
    val faceDetectedChip: String
    val faceDetectedSuccessCaption: String
    val multipleFacesChip: String
    val multipleFacesCaption: String
    val holdStillChip: String
    val holdStillCaption: String
    val lookingForFaceChip: String
    val noFaceCaption: String

    // UI Screen Labels & Buttons (Decoupled from Android R.string)
    val inButton: String
    val outButton: String
    val captureButton: String
    val scanningButton: String
    val submittingButton: String
    val retakeButton: String
    val closeButton: String
    val logoutButton: String
    val cancelButton: String
    val notesLabel: String
    val notesPlaceholder: String
    val languageLabel: String
    val autoSubmitLabel: String
    val autoSubmitReady: String
    val autoSubmitManual: String
    val selectLocation: String
    val multipleLocationsHint: String
    val standingLandmark: String
    val fetchingLocation: String
    val lastActivityPrefix: String
    val poweredBy: String

    // Dynamic message localizers
    fun localizeStatus(status: String): String
    fun localizeToast(message: String): String
    fun localizeActivity(text: String): String
}
