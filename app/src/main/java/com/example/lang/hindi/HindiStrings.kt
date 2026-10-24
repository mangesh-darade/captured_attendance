package com.example.lang.hindi

import com.example.lang.AppStrings

object HindiStrings : AppStrings {
    override val langCode: String = "hi"
    override val langName: String = "हिंदी"

    override val allow: String = "अनुमति दें"
    override val settings: String = "सेटिंग्स"
    override val turnOnGps: String = "GPS चालू करें"

    override val cameraPermissionOff: String = "कैमरा अनुमति बंद है। उपस्थिति के लिए कैमरा आवश्यक है।"
    override val locationPermissionOff: String = "लोकेशन अनुमति बंद है। उपस्थिति के लिए GPS आवश्यक है।"
    override val gpsTurnedOff: String = "GPS बंद है। उपस्थिति दर्ज करने के लिए लोकेशन चालू करें।"
    override val noInternetWaiting: String = "इंटरनेट नहीं है। उपस्थिति इस फ़ोन में सहेजी गई है और इंटरनेट आने पर सिंक हो जाएगी।"
    override val noInternet: String = "इंटरनेट कनेक्शन नहीं है।"
    override val internetRestoredSending: String = "इंटरनेट पुनः जुड़ गया। सहेजी गई उपस्थिति भेज रहे हैं।"

    override val capturedChip: String = "कैप्चर किया (Captured)"
    override val faceDetectedChip: String = "चेहरा पहचाना"
    override val faceDetectedSuccessCaption: String = "चेहरा सफलतापूर्वक पहचाना गया"
    override val multipleFacesChip: String = "एक से अधिक चेहरे"
    override val multipleFacesCaption: String = "कृपया कैमरे के सामने केवल एक ही व्यक्ति रहें"
    override val holdStillChip: String = "स्थिर रहें"
    override val holdStillCaption: String = "कृपया कैमरे के सामने स्थिर रहें"
    override val lookingForFaceChip: String = "चेहरा ढूंढ रहे हैं..."
    override val noFaceCaption: String = "कोई चेहरा नहीं मिला। पुनः प्रयास करें।"

    // UI Screen Labels & Buttons
    override val inButton: String = "IN"
    override val outButton: String = "OUT"
    override val captureButton: String = "उपस्थिति दर्ज करें (Capture)"
    override val scanningButton: String = "स्कैन हो रहा है..."
    override val submittingButton: String = "दर्ज हो रहा है..."
    override val retakeButton: String = "पुनः फोटो लें (Retake)"
    override val closeButton: String = "बंद करें"
    override val logoutButton: String = "लॉग आउट"
    override val cancelButton: String = "रद्द करें"
    override val notesLabel: String = "नोट्स (Notes)"
    override val notesPlaceholder: String = "वैकल्पिक टिप्पणी (उदा. देर से आगमन, फील्ड विजिट)"
    override val languageLabel: String = "भाषा चुनें (Language)"
    override val autoSubmitLabel: String = "स्वचालित उपस्थिति (Auto Submit)"
    override val autoSubmitReady: String = "चेहरा तैयार होने पर उपस्थिति स्वतः दर्ज होगी"
    override val autoSubmitManual: String = "उपस्थिति दर्ज करने के लिए कैप्चर दबाएं"
    override val selectLocation: String = "निकटतम स्थान चुनें"
    override val multipleLocationsHint: String = "पास के कई स्थान मिले। कृपया एक स्थान चुनें।"
    override val standingLandmark: String = "पास में स्थित"
    override val fetchingLocation: String = "GPS लोकेशन प्राप्त कर रहे हैं..."
    override val refreshLocation: String = "GPS रीफ्रेश करें"
    override val locationAccuracyCoarse: String = "कम सटीकता, रीफ्रेश करें"
    override val locationUpdated: String = "स्थान अपडेट हुआ"
    override val lastActivityPrefix: String = "पिछली गतिविधि"
    override val poweredBy: String = "Powered by"

    // Bottom Navigation Tabs
    override val homeTab: String = "होम"
    override val captureTab: String = "उपस्थिति"
    override val settingsTab: String = "सेटिंग्स"

    // Home Screen
    override val helloPrefix: String = "नमस्ते"
    override val adminRole: String = "प्रशासक"
    override val employeeRole: String = "कर्मचारी"
    override val todayStatusHeader: String = "आज की स्थिति"
    override val checkedInStatus: String = "चेक इन संपन्न ✓"
    override val checkedOutStatus: String = "चेक आउट संपन्न"
    override val notCheckedInStatus: String = "उपस्थिति दर्ज नहीं"
    override val notCheckedInDesc: String = "आपने आज अभी तक उपस्थिति दर्ज नहीं की है।"
    override val quickActionsHeader: String = "त्वरित विकल्प"
    override val todayLogTitle: String = "उपस्थिति लॉग"
    override val todayLogSubtitle: String = "1 महीने का इन/आउट रिकॉर्ड"
    override val faceBiometricSubtitle: String = "चेहरा स्कैन पंजीकृत या अपडेट करें"

    // Merchant Code & Login Screen
    override val merchantCodeLabel: String = "मर्चेंट कोड (Merchant Code)"
    override val merchantCodePlaceholder: String = "5 अंकों का कोड दर्ज करें (उदा. LH022)"
    override val phoneLabel: String = "एडमिन / सुपरवाइजर फोन नंबर"
    override val phonePlaceholder: String = "पंजीकृत मोबाइल नंबर दर्ज करें"
    override val passwordLabel: String = "पासवर्ड"
    override val passwordPlaceholder: String = "पासवर्ड दर्ज करें"
    override val loginButton: String = "कनेक्ट करें और लॉगिन करें"
    override val connectingToMerchant: String = "कनेक्ट हो रहा है..."
    override val changeServerButton: String = "सर्वर बदलें"
    override val viewErrorLogsButton: String = "एरर लॉग देखें"
    override val serverConfigTitle: String = "सर्वर कॉन्फ़िगरेशन"
    override val serverConfigDesc: String = "लोकल डेवलपमेंट सर्वर या कस्टम एंडपॉइंट होने पर URL बदलें।"
    override val saveAndConnectButton: String = "सहेजें और कनेक्ट करें"

    // Settings Screen
    override val settingsTitle: String = "कियोस्क सेटिंग्स"
    override val settingsSubtitle: String = "डिवाइस अनुमतियां, बायोमेट्रिक्स, भाषा और डायग्नोस्टिक्स प्रबंधित करें"
    override val faceBiometricTitle: String = "चेहरा बायोमेट्रिक उपस्थिति"
    override val faceBiometricAdminDesc: String = "एडमिन मोड: कर्मचारी को स्वतः पहचानकर उपस्थिति दर्ज करता है"
    override val faceBiometricUserDesc: String = "यूजर मोड: पंजीकृत चेहरा सत्यापित करके उपस्थिति दर्ज करता है"
    override val chooseLanguageSubtitle: String = "कियोस्क डिस्प्ले और संदेशों के लिए भाषा चुनें"
    override val systemDiagnosticsTitle: String = "सिस्टम और डायग्नोस्टिक्स"
    override val systemDiagnosticsDesc: String = "लॉग्स जांचें, नेटवर्क कनेक्टिविटी और सिस्टम स्थिति देखें"
    override val viewAppLogs: String = "ऐप लॉग देखें"
    override val configureServerUrl: String = "सर्वर URL बदलें"
    override val confirmLogoutTitle: String = "लॉग आउट की पुष्टि करें"
    override val confirmLogoutDesc: String = "क्या आप वास्तव में इस कियोस्क डिवाइस से लॉग आउट करना चाहते हैं?"
    override val signedInText: String = "लॉगिन खाता"

    // Today Attendance Dialog
    override val todayAttendanceTitle: String = "उपस्थिति इतिहास (Attendance Log)"
    override val todayAttendanceSubtitle: String = "1 महीने के चेक-इन और चेक-आउट रिकॉर्ड और फ़िल्टर"
    override val searchEmployeePlaceholder: String = "नाम, फ़ोन या स्थान से खोजें..."
    override val noRecordsToday: String = "कोई उपस्थिति रिकॉर्ड नहीं मिला।"
    override val totalPresent: String = "आज उपस्थित"
    override val totalPunches: String = "कुल पंच"
    override val filterAll: String = "सभी (1 महीना)"
    override val filterToday: String = "आज"
    override val filterIn: String = "केवल Check-In"
    override val filterOut: String = "Check-Out हुए"
    override val totalEntries: String = "कुल रिकॉर्ड"
    override val recordDetailsTitle: String = "उपस्थिति विवरण (Attendance Details)"
    override val notCheckedOutYet: String = "अभी चेक-आउट नहीं हुआ"
    override val punchLocation: String = "उपस्थिति स्थान"
    override val recordIdLabel: String = "रिकॉर्ड आईडी"
    override val punchDate: String = "दिनांक"

    // Face Enrollment
    override val faceEnrollmentTitle: String = "चेहरा पंजीकरण (Face Registration)"
    override val selectEmployeeToEnroll: String = "चेहरा पंजीकृत करने के लिए कर्मचारी चुनें"
    override val registerFaceButton: String = "चेहरा पंजीकृत करें"
    override val faceRegisteredSuccess: String = "चेहरा सफलतापूर्वक पंजीकृत किया गया!"
    override val faceRegisterFailed: String = "चेहरा पंजीकरण विफल रहा। कृपया पुनः प्रयास करें।"
    override val lookDirectlyAtCamera: String = "कृपया अच्छी रोशनी में सीधे कैमरे की ओर देखें"

    override fun localizeStatus(status: String): String {
        if (status.isBlank()) return status
        val lower = status.lowercase()
        return when {
            lower.contains("face detected successfully") || lower.contains("successfully") -> "चेहरा सफलतापूर्वक पहचाना गया"
            lower.contains("no face detected") || lower.contains("no face") -> "कोई चेहरा नहीं मिला। पुनः प्रयास करें।"
            lower.contains("multiple faces") -> "एक से अधिक चेहरे मिले।"
            lower.contains("hold still") -> "कृपया कैमरे के सामने स्थिर रहें"
            lower.contains("face detection ready") || lower == "ready" -> "चेहरा स्कैनिंग तैयार है।"
            lower.contains("loading face model") || lower.contains("camera active") -> "कैमरा सक्रिय। मॉडल लोड हो रहा है..."
            lower.contains("scanning face") || lower.contains("matching your face") -> "चेहरा स्कैन हो रहा है..."
            lower.contains("submitting") -> "उपस्थिति दर्ज हो रही है..."
            lower.contains("check-in successful") || lower.contains("check in successful") -> "चेक इन सफल रहा!"
            lower.contains("check-out successful") || lower.contains("check out successful") -> "चेक आउट सफल रहा!"
            lower.contains("already checked in") -> "पहले से चेक इन हैं। चेक आउट करने के लिए OUT चुनें।"
            lower.contains("already checked out") -> "पहले से चेक आउट हैं। चेक इन करने के लिए IN चुनें।"
            lower.contains("not recognized") -> "चेहरा पहचाना नहीं गया। कृपया कैमरे की ओर सीधे देखें।"
            lower.contains("attendance marked") -> "उपस्थिति दर्ज की गई!"
            else -> status
        }
    }

    override fun localizeToast(message: String): String {
        if (message.isBlank()) return message
        val lower = message.lowercase()
        return when {
            lower.contains("check-in successful") || lower.contains("check in successful") || lower.contains("checkin successful") -> "चेक इन सफल रहा!"
            lower.contains("check-out successful") || lower.contains("check out successful") || lower.contains("checkout successful") -> "चेक आउट सफल रहा!"
            lower.contains("attendance marked") || lower.contains("attendance saved") -> "उपस्थिति दर्ज की गई!"
            lower.contains("already checked in") -> "पहले से चेक इन हैं। चेक आउट करने के लिए OUT चुनें।"
            lower.contains("already checked out") -> "पहले से चेक आउट हैं। चेक इन करने के लिए IN चुनें।"
            lower.contains("not recognized") -> "चेहरा पहचाना नहीं गया। कृपया कैमरे की ओर सीधे देखें।"
            lower.contains("no face detected") -> "कैमरे में कोई चेहरा नहीं मिला। कृपया कैमरे की ओर सीधे देखें।"
            lower.contains("geofence") || lower.contains("outside") -> "आप पंजीकृत कार्यालय/साइट की सीमा से बाहर हैं।"
            lower.contains("gps location is not available") || lower.contains("gps is turned off") -> "GPS लोकेशन उपलब्ध नहीं है। कृपया GPS चालू करें।"
            lower.contains("acquiring location") -> "लोकेशन प्राप्त कर रहे हैं। कृपया प्रतीक्षा करें..."
            lower.contains("log in with your phone number") -> "उपस्थिति दर्ज करने से पहले कृपया फ़ोन नंबर से लॉगिन करें।"
            lower.contains("internet restored") -> "इंटरनेट पुनः जुड़ गया।"
            lower.contains("offline attendance synced") -> "ऑफ़लाइन उपस्थिति सिंक हो गई।"
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
