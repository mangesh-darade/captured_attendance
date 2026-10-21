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

    // Dynamic message localizers
    fun localizeStatus(status: String): String
    fun localizeToast(message: String): String
    fun localizeActivity(text: String): String
}
