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

    // UI Screen Labels & Buttons
    override val inButton: String = "IN"
    override val outButton: String = "OUT"
    override val captureButton: String = "Capture Attendance"
    override val scanningButton: String = "Scanning..."
    override val submittingButton: String = "Submitting..."
    override val retakeButton: String = "Retake"
    override val closeButton: String = "Close"
    override val logoutButton: String = "Log out"
    override val cancelButton: String = "Cancel"
    override val notesLabel: String = "Notes"
    override val notesPlaceholder: String = "Optional notes (e.g. reason for late check-in, field visit)"
    override val languageLabel: String = "Language / भाषा"
    override val autoSubmitLabel: String = "Auto Submit"
    override val autoSubmitReady: String = "Marks attendance when your face is ready"
    override val autoSubmitManual: String = "Press Capture to mark attendance"
    override val selectLocation: String = "Select nearby location"
    override val multipleLocationsHint: String = "Multiple nearby locations found. Please select one location."
    override val standingLandmark: String = "Standing near"
    override val fetchingLocation: String = "Acquiring GPS location..."
    override val lastActivityPrefix: String = "Last Activity"
    override val poweredBy: String = "Powered by"

    // Bottom Navigation Tabs
    override val homeTab: String = "Home"
    override val captureTab: String = "Capture"
    override val settingsTab: String = "Settings"

    // Home Screen
    override val helloPrefix: String = "Hello"
    override val adminRole: String = "Admin"
    override val employeeRole: String = "Employee"
    override val todayStatusHeader: String = "TODAY'S STATUS"
    override val checkedInStatus: String = "Checked In ✓"
    override val checkedOutStatus: String = "Checked Out"
    override val notCheckedInStatus: String = "Not Checked In"
    override val notCheckedInDesc: String = "You haven't marked attendance today yet."
    override val quickActionsHeader: String = "Quick Actions"
    override val todayLogTitle: String = "Today's Log"
    override val todayLogSubtitle: String = "View in/out timestamps"
    override val faceBiometricSubtitle: String = "Register / update scan"

    // Merchant Code & Login Screen
    override val merchantCodeLabel: String = "Merchant Code"
    override val merchantCodePlaceholder: String = "Enter 5-digit code (e.g. LH022)"
    override val phoneLabel: String = "Admin / Supervisor Phone"
    override val phonePlaceholder: String = "Enter registered mobile number"
    override val passwordLabel: String = "Password"
    override val passwordPlaceholder: String = "Enter password"
    override val loginButton: String = "Connect & Sign In"
    override val connectingToMerchant: String = "Connecting..."
    override val changeServerButton: String = "Change Server"
    override val viewErrorLogsButton: String = "View Error Logs"
    override val serverConfigTitle: String = "Server Configuration"
    override val serverConfigDesc: String = "Change Superadmin API URL if connecting to a local dev server or custom endpoint."
    override val saveAndConnectButton: String = "Save & Connect"

    // Settings Screen
    override val settingsTitle: String = "Kiosk Settings"
    override val settingsSubtitle: String = "Configure device permissions, biometrics, language, and diagnostics"
    override val faceBiometricTitle: String = "Face Biometric Attendance"
    override val faceBiometricAdminDesc: String = "Admin mode: auto-detects employee and records attendance seamlessly"
    override val faceBiometricUserDesc: String = "User mode: verifies registered face before recording attendance"
    override val chooseLanguageSubtitle: String = "Choose language for kiosk display and voice prompts"
    override val systemDiagnosticsTitle: String = "System & Diagnostics"
    override val systemDiagnosticsDesc: String = "Review logs, test network reachability, and check system health"
    override val viewAppLogs: String = "View Application Logs"
    override val configureServerUrl: String = "Configure Server URL"
    override val confirmLogoutTitle: String = "Confirm Sign Out"
    override val confirmLogoutDesc: String = "Are you sure you want to log out from this kiosk device?"
    override val signedInText: String = "Signed in as"

    // Today Attendance Dialog
    override val todayAttendanceTitle: String = "Today's Attendance"
    override val todayAttendanceSubtitle: String = "Overview of punches and employee check-ins for today"
    override val searchEmployeePlaceholder: String = "Search employee by name or ID..."
    override val noRecordsToday: String = "No attendance records found for today."
    override val totalPresent: String = "Present"
    override val totalPunches: String = "Total Punches"

    // Face Enrollment
    override val faceEnrollmentTitle: String = "Face Registration"
    override val selectEmployeeToEnroll: String = "Select an employee to register their face"
    override val registerFaceButton: String = "Register Face"
    override val faceRegisteredSuccess: String = "Face registered successfully!"
    override val faceRegisterFailed: String = "Face registration failed. Please try again."
    override val lookDirectlyAtCamera: String = "Please look directly at the camera with good lighting"

    override fun localizeStatus(status: String): String = status

    override fun localizeToast(message: String): String = message

    override fun localizeActivity(text: String): String = text
}
