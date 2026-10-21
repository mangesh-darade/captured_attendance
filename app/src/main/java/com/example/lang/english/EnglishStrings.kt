package com.example.lang.english

import com.example.lang.AppStrings

object EnglishStrings : AppStrings {
    override val langCode: String = "en"
    override val langName: String = "English"

    override val allow: String = "Allow"
    override val settings: String = "Settings"
    override val turnOnGps: String = "Turn on GPS"

    override val cameraPermissionOff: String = "Camera access is off. Face attendance needs the camera."
    override val locationPermissionOff: String = "Location access is off. Attendance needs GPS."
    override val gpsTurnedOff: String = "GPS is turned off. Turn on location to mark attendance."
    override val noInternetWaiting: String = "No internet. Check-in and check-out are saved on this phone and will sync when internet is back."
    override val noInternet: String = "No internet connection."
    override val internetRestoredSending: String = "Internet is back. Sending saved attendance."

    override val capturedChip: String = "Captured"
    override val faceDetectedChip: String = "Face detected"
    override val faceDetectedSuccessCaption: String = "Face detected successfully"
    override val multipleFacesChip: String = "Multiple faces detected"
    override val multipleFacesCaption: String = "Multiple faces detected. Please ensure only one person is visible."
    override val holdStillChip: String = "Hold still"
    override val holdStillCaption: String = "Face detected, hold still for better accuracy"
    override val lookingForFaceChip: String = "Looking for face..."
    override val noFaceCaption: String = "No face detected. Tap Retake to try again."

    override fun localizeStatus(status: String): String = status

    override fun localizeToast(message: String): String = message

    override fun localizeActivity(text: String): String = text
}
