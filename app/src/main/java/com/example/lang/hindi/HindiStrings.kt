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
    override val lastActivityPrefix: String = "पिछली गतिविधि"
    override val poweredBy: String = "Powered by"

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
