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
    override val inButton: String = "चेक-इन"
    override val outButton: String = "चेक-आउट"
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
    override val todayLogTitle: String = "हजेरी नोंदवही"
    override val todayLogSubtitle: String = "१ महिन्याची चेक-इन/आउट यादी"
    override val faceBiometricHint: String = "चेहऱ्याचा डेटा नोंदवा किंवा रिफ्रेश करा"
    override val permissionsTitle: String = "परवानग्या"
    override val permCamera: String = "कॅमेरा"
    override val permPreciseLocation: String = "अचूक लोकेशन"
    override val permGps: String = "लोकेशन सेवा (GPS)"
    override val permBackgroundLocation: String = "लोकेशन: नेहमी परवानगी द्या"
    override val permBackgroundHint: String = "चेक-इननंतर अ‍ॅप बंद असतानाही एरिया ट्रॅकिंग चालू राहण्यासाठी आवश्यक."
    override val permNotifications: String = "नोटिफिकेशन"
    override val permBattery: String = "बॅटरी: अमर्यादित"
    override val permBatteryHint: String = "Android ला बॅकग्राउंडमध्ये अ‍ॅप बंद करण्यापासून रोखते."
    override val permAllowed: String = "परवानगी दिली"
    override val permFix: String = "परवानगी द्या"
    override val faceBiometricSubtitle: String = "चेहरा नोंदवा/अपडेट करा"

    // Merchant Code & Login Screen
    override val merchantCodeLabel: String = "मर्चंट कोड (Merchant Code)"
    override val merchantCodePlaceholder: String = "५ अंकी कोड टाका (उदा. LH022)"
    override val phoneLabel: String = "युजर नेम / मोबाइल नंबर"
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
    override val todayAttendanceTitle: String = "हजेरी इतिहास (Attendance Log)"
    override val todayAttendanceSubtitle: String = "१ महिन्याच्या चेक-इन आणि चेक-आउट नोंदी व फिल्टर"
    override val searchEmployeePlaceholder: String = "नाव, फोन किंवा लोकेशनने शोधा..."
    override val noRecordsToday: String = "हजेरीच्या नोंदी आढळल्या नाहीत."
    override val totalPresent: String = "आज उपस्थित"
    override val totalPunches: String = "एकूण पंच"
    override val filterAll: String = "सर्व (१ महिना)"
    override val filterToday: String = "आज"
    override val filterIn: String = "फक्त चेक-इन"
    override val filterOut: String = "चेक-आउट झालेले"
    override val totalEntries: String = "एकूण नोंदी"
    override val allEmployeesOption: String = "सर्व कर्मचारी"
    override val filterEmployeeLabel: String = "कर्मचारी निवडा"
    override val myAttendanceOnly: String = "माझी हजेरी"
    override val recordDetailsTitle: String = "हजेरी तपशील (Attendance Details)"
    override val notCheckedOutYet: String = "अजून चेक-आउट झालेले नाही"
    override val punchLocation: String = "हजेरी ठिकाण"
    override val recordIdLabel: String = "नोंद क्रमांक"
    override val punchDate: String = "तारीख"
    override val checkInLabel: String = "चेक-इन"
    override val checkOutLabel: String = "चेक-आउट"
    override val statusIn: String = "IN"
    override val statusOut: String = "OUT"
    override val timestampLabel: String = "नोंदणी वेळ"
    override val retryButton: String = "पुन्हा प्रयत्न करा"

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
            lower.contains("not logged in") -> "तुम्ही लॉगिन केलेले नाही. कृपया आधी लॉगिन करा."
            lower.contains("failed to load today") -> "आजची उपस्थिती लोड करता आली नाही."
            lower.contains("cannot reach") || lower.contains("failed to connect") || lower.contains("did not respond") || lower.contains("connection error") || lower.contains("network error") || lower.contains("network request failed") || lower.contains("failed to reach") -> "सर्व्हरशी कनेक्ट होता आले नाही. इंटरनेट किंवा सर्व्हर तपासा."
            lower.contains("failed to load employees") -> "कर्मचाऱ्यांची यादी लोड करता आली नाही."
            lower.contains("no face scan captured") -> "अजून कोणताही चेहरा स्कॅन झालेला नाही."
            lower.contains("enrolled successfully") -> "चेहरा यशस्वीरित्या नोंदवला गेला!"
            lower.contains("failed to register face") -> "चेहरा नोंदवता आला नाही."
            lower.contains("android unique code must") -> "अँड्रॉइड युनिक कोड ४ ते ६४ अक्षरे किंवा अंकांचा असावा."
            lower.contains("android code was not found") -> "अँड्रॉइड कोड सापडला नाही."
            lower.contains("enter the android unique code") -> "लॉगिनपूर्वी अँड्रॉइड युनिक कोड टाका."
            lower.contains("username and password are required") -> "युजर नेम आणि पासवर्ड आवश्यक आहेत."
            lower.contains("login failed") -> "लॉगिन अयशस्वी झाले."
            lower.contains("session expired") -> "सत्र संपले. कृपया पुन्हा लॉगिन करा."
            lower.contains("access denied") -> "प्रवेश नाकारला."
            lower.contains("api endpoint not found") -> "API एंडपॉइंट सापडला नाही. सर्व्हर URL तपासा."
            lower.contains("server error") -> "सर्व्हर त्रुटी. कृपया नंतर पुन्हा प्रयत्न करा."
            lower.contains("could not mark attendance") -> "उपस्थिती नोंदवता आली नाही."
            lower.contains("precise location unavailable") -> "अचूक लोकेशन उपलब्ध नाही. हाय-अ‍ॅक्युरसी GPS चालू करा."
            lower.contains("saved offline") -> "उपस्थिती ऑफलाइन जतन केली. इंटरनेट आल्यावर सिंक होईल."
            lower.contains("camera error") -> "कॅमेरा त्रुटी. कृपया कॅमेरा परवानगी तपासा."
            lower.contains("face model error") -> "फेस मॉडेल लोड झाले नाही."
            else -> message
        }
    }

    override fun localizeActivity(text: String): String {
        if (text.isBlank() || text == "--") return text
        return text
            .replace(Regex("(?i)check[- ]?out at"), "चेक-आउट:")
            .replace(Regex("(?i)check[- ]?in at"), "चेक-इन:")
            .replace(Regex("(?i)checked out"), "चेक आउट झाले")
            .replace(Regex("(?i)checked in"), "चेक इन झाले")
            .replace(Regex("(?i)\\(offline\\)"), "(ऑफलाईन)")
    }
}
