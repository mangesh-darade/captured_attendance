package com.example.lang.marathi

import com.example.lang.AppStrings

object MarathiStrings : AppStrings {
    override val langCode: String = "mr"
    override val langName: String = "मराठी"

    override val allow: String = "परवानगी द्या"
    override val settings: String = "सेटिंग्ज"
    override val turnOnGps: String = "GPS चालू करा"

    override val cameraPermissionOff: String = "कॅमेरा परवानगी बंद आहे. हजेरीसाठी कॅमेरा आवश्यक आहे."
    override val locationPermissionOff: String = "लोकेशन परवानगी बंद आहे. हजेरीसाठी GPS आवश्यक आहे."
    override val gpsTurnedOff: String = "GPS बंद आहे. हजेरी नोंदवण्यासाठी लोकेशन चालू करा."
    override val noInternetWaiting: String = "इंटरनेट नाही. हजेरी या फोनवर सेव्ह केली आहे आणि इंटरनेट आल्यावर सिंक होईल."
    override val noInternet: String = "इंटरनेट कनेक्शन नाही."
    override val internetRestoredSending: String = "इंटरनेट पुन्हा सुरू झाले. सेव्ह केलेली हजेरी पाठवत आहे."

    override val capturedChip: String = "कॅप्चर झाले (Captured)"
    override val faceDetectedChip: String = "चेहरा ओळखला"
    override val faceDetectedSuccessCaption: String = "चेहरा यशस्वीपणे ओळखला"
    override val multipleFacesChip: String = "एकापेक्षा जास्त चेहरे"
    override val multipleFacesCaption: String = "कृपया कॅमेऱ्यासमोर फक्त एकच व्यक्ती उभे राहा"
    override val holdStillChip: String = "स्थिर राहा"
    override val holdStillCaption: String = "कृपया कॅमेऱ्यासमोर स्थिर उभे राहा"
    override val lookingForFaceChip: String = "चेहरा शोधत आहे..."
    override val noFaceCaption: String = "चेहरा आढळला नाही. पुन्हा प्रयत्न करा."

    // UI Screen Labels & Buttons
    override val inButton: String = "IN"
    override val outButton: String = "OUT"
    override val captureButton: String = "हजेरी नोंदवा (Capture)"
    override val scanningButton: String = "स्कॅनिंग होत आहे..."
    override val submittingButton: String = "नोंदवत आहे..."
    override val retakeButton: String = "पुन्हा फोटो घ्या (Retake)"
    override val closeButton: String = "बंद करा"
    override val logoutButton: String = "लॉग आउट"
    override val cancelButton: String = "रद्द करा"
    override val notesLabel: String = "नोंद (Notes)"
    override val notesPlaceholder: String = "पर्यायी नोंद (उदा. उशीर, फील्ड व्हिजिट)"
    override val languageLabel: String = "भाषा निवडा (Language)"
    override val autoSubmitLabel: String = "स्वयंचलित नोंदणी (Auto Submit)"
    override val autoSubmitReady: String = "चेहरा ओळखल्यावर हजेरी आपोआप नोंदवली जाईल"
    override val autoSubmitManual: String = "हजेरी नोंदवण्यासाठी कॅप्चर दाबा"
    override val selectLocation: String = "जवळचे लोकेशन निवडा"
    override val multipleLocationsHint: String = "एकापेक्षा जास्त जवळची ठिकाणे आढळली. कृपया एक निवडा."
    override val standingLandmark: String = "येथे उभे आहात"
    override val fetchingLocation: String = "GPS लोकेशन शोधत आहे..."
    override val refreshLocation: String = "GPS रिफ्रेश करा"
    override val locationAccuracyCoarse: String = "कमी अचूकता, रिफ्रेश करा"
    override val locationUpdated: String = "लोकेशन अपडेट झाले"
    override val lastActivityPrefix: String = "मागील नोंद"
    override val poweredBy: String = "Powered by"

    // Bottom Navigation Tabs
    override val homeTab: String = "मुख्य पृष्ठ"
    override val captureTab: String = "हजेरी"
    override val settingsTab: String = "सेटिंग्ज"

    // Home Screen
    override val helloPrefix: String = "नमस्कार"
    override val adminRole: String = "प्रशासक"
    override val employeeRole: String = "कर्मचारी"
    override val todayStatusHeader: String = "आजची स्थिती"
    override val checkedInStatus: String = "चेक इन झाले ✓"
    override val checkedOutStatus: String = "चेक आउट झाले"
    override val notCheckedInStatus: String = "हजेरी नोंदवली नाही"
    override val notCheckedInDesc: String = "तुम्ही आज अजून हजेरी नोंदवलेली नाही."
    override val quickActionsHeader: String = "जलद पर्याय"
    override val todayLogTitle: String = "आजचा हजेरी लॉग"
    override val todayLogSubtitle: String = "हजेरीच्या वेळा आणि नोंदी पहा"
    override val faceBiometricSubtitle: String = "चेहरा स्कॅन नोंदवा किंवा बदला"

    // Merchant Code & Login Screen
    override val merchantCodeLabel: String = "मर्चंट कोड (Merchant Code)"
    override val merchantCodePlaceholder: String = "५ अंकी कोड टाका (उदा. LH022)"
    override val phoneLabel: String = "प्रशासक / सुपरवायझर फोन नंबर"
    override val phonePlaceholder: String = "नोंदणीकृत मोबाईल नंबर टाका"
    override val passwordLabel: String = "पासवर्ड"
    override val passwordPlaceholder: String = "पासवर्ड टाका"
    override val loginButton: String = "कनेक्ट करा आणि लॉगिन करा"
    override val connectingToMerchant: String = "कनेक्ट करत आहे..."
    override val changeServerButton: String = "सर्व्हर बदला"
    override val viewErrorLogsButton: String = "एरर लॉग पहा"
    override val serverConfigTitle: String = "सर्व्हर कॉन्फिगरेशन"
    override val serverConfigDesc: String = "स्थानिक डेव्हलपमेंट सर्व्हर किंवा कस्टम एंडपॉईंट असल्यास URL बदला."
    override val saveAndConnectButton: String = "सेव्ह आणि कनेक्ट करा"

    // Settings Screen
    override val settingsTitle: String = "किऑस्क सेटिंग्ज"
    override val settingsSubtitle: String = "डिव्हाइस परवानग्या, बायोमेट्रिक्स, भाषा आणि निदान व्यवस्थापित करा"
    override val faceBiometricTitle: String = "चेहरा बायोमेट्रिक हजेरी"
    override val faceBiometricAdminDesc: String = "अॅडमिन मोड: कर्मचारी स्वयंचलित ओळखून हजेरी नोंदवतो"
    override val faceBiometricUserDesc: String = "युझर मोड: नोंदणीकृत चेहरा पडताळून हजेरी नोंदवतो"
    override val chooseLanguageSubtitle: String = "किऑस्क स्क्रीन आणि सूचनांसाठी भाषा निवडा"
    override val systemDiagnosticsTitle: String = "सिस्टम आणि डायग्नोस्टिक्स"
    override val systemDiagnosticsDesc: String = "लॉग्स तपासा, नेटवर्क कनेक्टिव्हिटी आणि सिस्टम स्थिती तपासा"
    override val viewAppLogs: String = "अॅप लॉग पहा"
    override val configureServerUrl: String = "सर्व्हर URL बदला"
    override val confirmLogoutTitle: String = "लॉग आउट निश्चित करा"
    override val confirmLogoutDesc: String = "तुम्हाला या किऑस्क डिव्हाइसवरून लॉग आउट करायचे आहे का?"
    override val signedInText: String = "लॉगिन केलेले खाते"

    // Today Attendance Dialog
    override val todayAttendanceTitle: String = "आजची हजेरी (Today's Attendance)"
    override val todayAttendanceSubtitle: String = "आजच्या कर्मचाऱ्यांची उपस्थिती आणि पंच नोंदी"
    override val searchEmployeePlaceholder: String = "कर्मचारी नाव किंवा आयडीने शोधा..."
    override val noRecordsToday: String = "आजच्या हजेरीची कोणतीही नोंद आढळली नाही."
    override val totalPresent: String = "उपस्थित"
    override val totalPunches: String = "एकूण पंच"

    // Face Enrollment
    override val faceEnrollmentTitle: String = "चेहरा नोंदणी (Face Registration)"
    override val selectEmployeeToEnroll: String = "चेहरा नोंदवण्यासाठी कर्मचारी निवडा"
    override val registerFaceButton: String = "चेहरा नोंदवा"
    override val faceRegisteredSuccess: String = "चेहरा यशस्वीपणे नोंदवला गेला!"
    override val faceRegisterFailed: String = "चेहरा नोंदणी अयशस्वी. कृपया पुन्हा प्रयत्न करा."
    override val lookDirectlyAtCamera: String = "कृपया चांगल्या प्रकाशात थेट कॅमेऱ्याकडे पहा"

    override fun localizeStatus(status: String): String {
        if (status.isBlank()) return status
        val lower = status.lowercase()
        return when {
            lower.contains("face detected successfully") || lower.contains("successfully") -> "चेहरा यशस्वीपणे ओळखला"
            lower.contains("no face detected") || lower.contains("no face") -> "चेहरा आढळला नाही. पुन्हा प्रयत्न करा."
            lower.contains("multiple faces") -> "एकापेक्षा जास्त चेहरे आढळले."
            lower.contains("hold still") -> "कृपया कॅमेऱ्यासमोर स्थिर उभे राहा"
            lower.contains("face detection ready") || lower == "ready" -> "चेहरा स्कॅनिंग तयार आहे."
            lower.contains("loading face model") || lower.contains("camera active") -> "कॅमेरा सुरू झाला. मॉडेल लोड होत आहे..."
            lower.contains("scanning face") || lower.contains("matching your face") -> "चेहरा स्कॅन करत आहे..."
            lower.contains("submitting") -> "हजेरी नोंदवत आहे..."
            lower.contains("check-in successful") || lower.contains("check in successful") -> "चेक इन यशस्वी झाले!"
            lower.contains("check-out successful") || lower.contains("check out successful") -> "चेक आउट यशस्वी झाले!"
            lower.contains("already checked in") -> "आधीच चेक इन झाले आहे. चेक आउट करण्यासाठी OUT निवडा."
            lower.contains("already checked out") -> "आधीच चेक आउट झाले आहे. चेक इन करण्यासाठी IN निवडा."
            lower.contains("not recognized") -> "चेहरा ओळखला नाही. कृपया कॅमेऱ्याकडे थेट पहा."
            lower.contains("attendance marked") -> "हजेरी नोंदवली गेली!"
            else -> status
        }
    }

    override fun localizeToast(message: String): String {
        if (message.isBlank()) return message
        val lower = message.lowercase()
        return when {
            lower.contains("check-in successful") || lower.contains("check in successful") || lower.contains("checkin successful") -> "चेक इन यशस्वी झाले!"
            lower.contains("check-out successful") || lower.contains("check out successful") || lower.contains("checkout successful") -> "चेक आउट यशस्वी झाले!"
            lower.contains("attendance marked") || lower.contains("attendance saved") -> "हजेरी नोंदवली गेली!"
            lower.contains("already checked in") -> "आधीच चेक इन झाले आहे. चेक आउट करण्यासाठी OUT निवडा."
            lower.contains("already checked out") -> "आधीच चेक आउट झाले आहे. चेक इन करण्यासाठी IN निवडा."
            lower.contains("not recognized") -> "चेहरा ओळखला नाही. कृपया कॅमेऱ्याकडे थेट पहा."
            lower.contains("no face detected") -> "कॅमेऱ्यात चेहरा आढळला नाही. कृपया कॅमेऱ्याकडे थेट पहा."
            lower.contains("geofence") || lower.contains("outside") -> "तुम्ही नोंदणीकृत ऑफिस / साईटच्या परिसराबाहेर आहात."
            lower.contains("gps location is not available") || lower.contains("gps is turned off") -> "GPS लोकेशन उपलब्ध नाही. कृपया GPS चालू करा."
            lower.contains("acquiring location") -> "लोकेशन शोधत आहे. कृपया थांबा..."
            lower.contains("log in with your phone number") -> "हजेरी नोंदवण्यापूर्वी कृपया फोन नंबरने लॉगिन करा."
            lower.contains("internet restored") -> "इंटरनेट पुन्हा सुरू झाले."
            lower.contains("offline attendance synced") -> "ऑफलाईन हजेरी सिंक झाली."
            else -> message
        }
    }

    override fun localizeActivity(text: String): String {
        if (text.isBlank() || text == "--") return text
        return text
            .replace("Check-out at", "चेक-आउट:")
            .replace("Check-in at", "चेक-इन:")
            .replace("Check out at", "चेक-आउट:")
            .replace("Check in at", "चेक-इन:")
    }
}
