package com.example.lang

/**
 * Interface contract defining all UI text, banners, toasts,
 * dialogs, and HUD messages for the Attendance Kiosk application.
 *
 * Supported languages:
 *  - en: English
 *  - mr: मराठी (Marathi)
 *  - hi: हिंदी (Hindi)
 *  - gu: ગુજરાતી (Gujarati)
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

    // UI Screen Labels & Buttons
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
    val refreshLocation: String
    val locationAccuracyCoarse: String
    val locationUpdated: String
    val lastActivityPrefix: String
    val poweredBy: String

    // Bottom Navigation Tabs
    val homeTab: String
    val captureTab: String
    val settingsTab: String

    // Home Screen
    val helloPrefix: String
    val adminRole: String
    val employeeRole: String
    val todayStatusHeader: String
    val checkedInStatus: String
    val checkedOutStatus: String
    val notCheckedInStatus: String
    val notCheckedInDesc: String
    val quickActionsHeader: String
    val todayLogTitle: String
    val todayLogSubtitle: String
    val faceBiometricSubtitle: String

    // Merchant Code & Login Screen
    val merchantCodeLabel: String
    val merchantCodePlaceholder: String
    val phoneLabel: String
    val phonePlaceholder: String
    val passwordLabel: String
    val passwordPlaceholder: String
    val loginButton: String
    val connectingToMerchant: String
    val changeServerButton: String
    val viewErrorLogsButton: String
    val serverConfigTitle: String
    val serverConfigDesc: String
    val saveAndConnectButton: String

    // Settings Screen
    val settingsTitle: String
    val settingsSubtitle: String
    val faceBiometricTitle: String
    val faceBiometricAdminDesc: String
    val faceBiometricUserDesc: String
    val chooseLanguageSubtitle: String
    val systemDiagnosticsTitle: String
    val systemDiagnosticsDesc: String
    val viewAppLogs: String
    val configureServerUrl: String
    val confirmLogoutTitle: String
    val confirmLogoutDesc: String
    val signedInText: String

    // Today Attendance Dialog
    val todayAttendanceTitle: String
    val todayAttendanceSubtitle: String
    val searchEmployeePlaceholder: String
    val noRecordsToday: String
    val totalPresent: String
    val totalPunches: String
    val filterAll: String
    val filterToday: String
    val filterIn: String
    val filterOut: String
    val totalEntries: String
    val recordDetailsTitle: String
    val notCheckedOutYet: String
    val punchLocation: String
    val recordIdLabel: String
    val punchDate: String
    val checkInLabel: String
    val checkOutLabel: String
    val statusIn: String
    val statusOut: String
    val timestampLabel: String
    val retryButton: String

    // Face Enrollment
    val faceEnrollmentTitle: String
    val selectEmployeeToEnroll: String
    val registerFaceButton: String
    val faceRegisteredSuccess: String
    val faceRegisterFailed: String
    val lookDirectlyAtCamera: String

    // Dynamic message localizers
    fun localizeStatus(status: String): String
    fun localizeToast(message: String): String
    fun localizeActivity(text: String): String
}
