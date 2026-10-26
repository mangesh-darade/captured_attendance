package com.example.lang.gujrati

import com.example.lang.AppStrings

object GujratiStrings : AppStrings {
    override val langCode: String = "gu"
    override val langName: String = "ગુજરાતી"

    override val allow: String = "પરવાનગી આપો"
    override val settings: String = "સેટિંગ્સ"
    override val turnOnGps: String = "GPS ચાલુ કરો"

    override val cameraPermissionOff: String = "કેમેરા પરવાનગી બંધ છે. હાજરી માટે કેમેરા જરૂરી છે."
    override val locationPermissionOff: String = "લોકેશન પરવાનગી બંધ છે. હાજરી માટે GPS જરૂરી છે."
    override val gpsTurnedOff: String = "GPS બંધ છે. હાજરી નોંધવા માટે લોકેશન ચાલુ કરો."
    override val noInternetWaiting: String = "ઇન્ટરનેટ નથી. હાજરી આ ફોનમાં સાચવવામાં આવી છે અને ઇન્ટરનેટ આવતાં સિંક થઈ જશે."
    override val noInternet: String = "ઇન્ટરનેટ કનેક્શન નથી."
    override val internetRestoredSending: String = "ઇન્ટરનેટ ફરી શરૂ થયું. સાચવેલી હાજરી મોકલી રહ્યાં છીએ."

    override val capturedChip: String = "કેપ્ચર થયું (Captured)"
    override val faceDetectedChip: String = "ચહેરો ઓળખાયો"
    override val faceDetectedSuccessCaption: String = "ચહેરો સફળતાપૂર્વક ઓળખાયો"
    override val multipleFacesChip: String = "એક કરતાં વધુ ચહેરા"
    override val multipleFacesCaption: String = "કૃપા કરીને કેમેરા સામે ફક્ત એક જ વ્યક્તિ રહો"
    override val holdStillChip: String = "સ્થિર રહો"
    override val holdStillCaption: String = "કૃપા કરીને કેમેરા સામે સ્થિર રહો"
    override val lookingForFaceChip: String = "ચહેરો શોધી રહ્યાં છીએ..."
    override val noFaceCaption: String = "કોઈ ચહેરો મળ્યો નથી. ફરી પ્રયાસ કરો."

    // UI Screen Labels & Buttons
    override val inButton: String = "ચેક-ઇન"
    override val outButton: String = "ચેક-આઉટ"
    override val captureButton: String = "હાજરી નોંધો (Capture)"
    override val scanningButton: String = "સ્કેન થઈ રહ્યું છે..."
    override val submittingButton: String = "નોંધાઈ રહ્યું છે..."
    override val retakeButton: String = "ફરીથી ફોટો લો (Retake)"
    override val closeButton: String = "બંધ કરો"
    override val logoutButton: String = "લૉગ આઉટ"
    override val cancelButton: String = "રદ કરો"
    override val notesLabel: String = "નોંધ (Notes)"
    override val notesPlaceholder: String = "વૈકલ્પિક નોંધ (દા.ત. મોડા આવવાનું કારણ)"
    override val languageLabel: String = "ભાષા પસંદ કરો (Language)"
    override val autoSubmitLabel: String = "સ્વચાલિત હાજરી (Auto Submit)"
    override val autoSubmitReady: String = "ચહેરો ઓળખાતાં જ હાજરી આપોઆપ નોંધાઈ જશે"
    override val autoSubmitManual: String = "હાજરી નોંધવા માટે કેપ્ચર દબાવો"
    override val selectLocation: String = "નજીકનું સ્થળ પસંદ કરો"
    override val multipleLocationsHint: String = "નજીકના અનેક સ્થળો મળ્યા. કૃપા કરીને એક પસંદ કરો."
    override val standingLandmark: String = "નજીક ઊભા છો"
    override val fetchingLocation: String = "GPS લોકેશન શોધી રહ્યાં છીએ..."
    override val refreshLocation: String = "GPS રિફ્રેશ કરો"
    override val locationAccuracyCoarse: String = "ઓછી ચોકસાઈ, રિફ્રેશ કરો"
    override val locationUpdated: String = "સ્થાન અપડેટ થયું"
    override val lastActivityPrefix: String = "છેલ્લી પ્રવૃત્તિ"
    override val poweredBy: String = "Powered by"

    // Bottom Navigation Tabs
    override val homeTab: String = "હોમ"
    override val captureTab: String = "હાજરી"
    override val settingsTab: String = "સેટિંગ્સ"

    // Home Screen
    override val helloPrefix: String = "નમસ્તે"
    override val adminRole: String = "એડમિન"
    override val employeeRole: String = "કર્મચારી"
    override val todayStatusHeader: String = "આજની સ્થિતિ"
    override val checkedInStatus: String = "ચેક ઇન થઈ ગયું ✓"
    override val checkedOutStatus: String = "ચેક આઉટ થઈ ગયું"
    override val notCheckedInStatus: String = "હાજરી નોંધાઈ નથી"
    override val notCheckedInDesc: String = "તમે આજે હજી સુધી હાજરી નોંધાવી નથી."
    override val quickActionsHeader: String = "ઝડપી ક્રિયાઓ"
    override val todayLogTitle: String = "હાજરી લોગ"
    override val todayLogSubtitle: String = "૧ મહિનાનો ઇન/આઉટ રેકોર્ડ"
    override val faceBiometricHint: String = "ચહેરાનો ડેટા નોંધો અથવા રિફ્રેશ કરો"
    override val permissionsTitle: String = "પરવાનગીઓ"
    override val permCamera: String = "કેમેરા"
    override val permPreciseLocation: String = "ચોક્કસ લોકેશન"
    override val permGps: String = "લોકેશન સેવા (GPS)"
    override val permBackgroundLocation: String = "લોકેશન: હંમેશા મંજૂરી આપો"
    override val permBackgroundHint: String = "ચેક-ઇન પછી એપ બંધ હોય ત્યારે પણ એરિયા ટ્રેકિંગ ચાલુ રહે તે માટે જરૂરી."
    override val permNotifications: String = "નોટિફિકેશન"
    override val permBattery: String = "બેટરી: અપ્રતિબંધિત"
    override val permBatteryHint: String = "Android ને બેકગ્રાઉન્ડમાં એપ બંધ કરતા અટકાવે છે."
    override val permAllowed: String = "મંજૂર"
    override val permFix: String = "મંજૂરી આપો"
    override val faceBiometricSubtitle: String = "ચહેરો નોંધો/અપડેટ કરો"

    // Merchant Code & Login Screen
    override val merchantCodeLabel: String = "મર્ચન્ટ કોડ (Merchant Code)"
    override val merchantCodePlaceholder: String = "૫ અંકનો કોડ દાખલ કરો (દા.ત. LH022)"
    override val phoneLabel: String = "યુઝર નેમ / મોબાઇલ નંબર"
    override val phonePlaceholder: String = "નોંધાયેલ મોબાઈલ નંબર દાખલ કરો"
    override val passwordLabel: String = "પાસવર્ડ"
    override val passwordPlaceholder: String = "પાસવર્ડ દાખલ કરો"
    override val loginButton: String = "કનેક્ટ કરો અને લૉગિન કરો"
    override val connectingToMerchant: String = "કનેક્ટ થઈ રહ્યું છે..."
    override val changeServerButton: String = "સર્વર બદલો"
    override val viewErrorLogsButton: String = "ભૂલ લોગ જુઓ"
    override val serverConfigTitle: String = "સર્વર રૂપરેખાંકન"
    override val serverConfigDesc: String = "સ્થાનિક વિકાસ સર્વર અથવા વૈવિધ્યપૂર્ણ એન્ડપોઇન્ટ હોય તો URL બદલો."
    override val saveAndConnectButton: String = "સાચવો અને કનેક્ટ કરો"

    // Settings Screen
    override val settingsTitle: String = "કિયોસ્ક સેટિંગ્સ"
    override val settingsSubtitle: String = "ઉપકરણ પરવાનગીઓ, બાયોમેટ્રિક્સ, ભાષા અને સિસ્ટમ સ્થિતિ ગોઠવો"
    override val faceBiometricTitle: String = "ચહેરો બાયોમેટ્રિક હાજરી"
    override val faceBiometricAdminDesc: String = "એડમિન મોડ: કર્મચારીને આપમેળે ઓળખીને હાજરી નોંધે છે"
    override val faceBiometricUserDesc: String = "યુઝર મોડ: નોંધાયેલ ચહેરો ચકાસીને હાજરી નોંધે છે"
    override val chooseLanguageSubtitle: String = "કિયોસ્ક પ્રદર્શન અને સંદેશાઓ માટે ભાષા પસંદ કરો"
    override val systemDiagnosticsTitle: String = "સિસ્ટમ અને ડાયગ્નોસ્ટિક્સ"
    override val systemDiagnosticsDesc: String = "લોગ તપાસો, નેટવર્ક કનેક્ટિવિટી અને સિસ્ટમ સ્થિતિ ચકાસો"
    override val viewAppLogs: String = "એપ્લિકેશન લોગ જુઓ"
    override val configureServerUrl: String = "સર્વર URL બદલો"
    override val confirmLogoutTitle: String = "લૉગ આઉટની ખાતરી કરો"
    override val confirmLogoutDesc: String = "શું તમે ખરેખર આ કિયોસ્ક ઉપકરણમાંથી લૉગ આઉટ કરવા માંગો છો?"
    override val signedInText: String = "લૉગિન થયેલ ખાતું"

    // Today Attendance Dialog
    override val todayAttendanceTitle: String = "હાજરી ઇતિહાસ (Attendance Log)"
    override val todayAttendanceSubtitle: String = "૧ મહિનાના ચેક-ઇન અને ચેક-આઉટ રેકોર્ડ અને ફિલ્ટર"
    override val searchEmployeePlaceholder: String = "નામ, ફોન અથવા સ્થળથી શોધો..."
    override val noRecordsToday: String = "કોઈ હાજરી રેકોર્ડ મળ્યો નથી."
    override val totalPresent: String = "આજે હાજર"
    override val totalPunches: String = "કુલ પંચ"
    override val filterAll: String = "બધા (૧ મહિનો)"
    override val filterToday: String = "આજે"
    override val filterIn: String = "માત્ર ચેક-ઇન"
    override val filterOut: String = "ચેક-આઉટ થયેલ"
    override val totalEntries: String = "કુલ રેકોર્ડ"
    override val allEmployeesOption: String = "બધા કર્મચારીઓ"
    override val filterEmployeeLabel: String = "કર્મચારી પસંદ કરો"
    override val myAttendanceOnly: String = "મારી હાજરી"
    override val recordDetailsTitle: String = "હાજરી વિગત (Attendance Details)"
    override val notCheckedOutYet: String = "હજુ ચેક-આઉટ થયું નથી"
    override val punchLocation: String = "હાજરી સ્થળ"
    override val recordIdLabel: String = "રેકોર્ડ આઈડી"
    override val punchDate: String = "તારીખ"
    override val checkInLabel: String = "ચેક-ઇન"
    override val checkOutLabel: String = "ચેક-આઉટ"
    override val statusIn: String = "IN"
    override val statusOut: String = "OUT"
    override val timestampLabel: String = "સમય"
    override val retryButton: String = "ફરી પ્રયાસ કરો"

    // Face Enrollment
    override val faceEnrollmentTitle: String = "ચહેરો નોંધણી (Face Registration)"
    override val selectEmployeeToEnroll: String = "ચહેરો નોંધવા માટે કર્મચારી પસંદ કરો"
    override val registerFaceButton: String = "ચહેરો નોંધો"
    override val faceRegisteredSuccess: String = "ચહેરો સફળતાપૂર્વક નોંધાયો!"
    override val faceRegisterFailed: String = "ચહેરો નોંધણી નિષ્ફળ રહી. કૃપા કરીને ફરી પ્રયાસ કરો."
    override val lookDirectlyAtCamera: String = "કૃપા કરીને સારા પ્રકાશમાં સીધા કેમેરા સામે જુઓ"

    override fun localizeStatus(status: String): String {
        if (status.isBlank()) return status
        val lower = status.lowercase()
        return when {
            lower.contains("face detected successfully") || lower.contains("successfully") -> "ચહેરો સફળતાપૂર્વક ઓળખાયો"
            lower.contains("no face detected") || lower.contains("no face") -> "કોઈ ચહેરો મળ્યો નથી. ફરી પ્રયાસ કરો."
            lower.contains("multiple faces") -> "એક કરતાં વધુ ચહેરા મળ્યા."
            lower.contains("hold still") -> "કૃપા કરીને કેમેરા સામે સ્થિર રહો"
            lower.contains("face detection ready") || lower == "ready" -> "ચહેરો સ્કેન કરવા તૈયાર છે."
            lower.contains("loading face model") || lower.contains("camera active") -> "કેમેરા સક્રિય થયો. મોડલ લોડ થઈ રહ્યું છે..."
            lower.contains("scanning face") || lower.contains("matching your face") -> "ચહેરો સ્કેન થઈ રહ્યો છે..."
            lower.contains("submitting") -> "હાજરી નોંધાઈ રહી છે..."
            lower.contains("check-in successful") || lower.contains("check in successful") -> "ચેક ઇન સફળ થયું!"
            lower.contains("check-out successful") || lower.contains("check out successful") -> "ચેક આઉટ સફળ થયું!"
            lower.contains("already checked in") -> "પહેલેથી ચેક ઇન થયેલું છે. ચેક આઉટ કરવા માટે OUT પસંદ કરો."
            lower.contains("already checked out") -> "પહેલેથી ચેક આઉટ થયેલું છે. ચેક ઇન કરવા માટે IN પસંદ કરો."
            lower.contains("not recognized") -> "ચહેરો ઓળખાયો નથી. કૃપા કરીને કેમેરા સામે સીધા જુઓ."
            lower.contains("attendance marked") -> "હાજરી નોંધાઈ ગઈ!"
            else -> status
        }
    }

    override fun localizeToast(message: String): String {
        if (message.isBlank()) return message
        val lower = message.lowercase()
        return when {
            lower.contains("check-in successful") || lower.contains("check in successful") || lower.contains("checkin successful") -> "ચેક ઇન સફળ થયું!"
            lower.contains("check-out successful") || lower.contains("check out successful") || lower.contains("checkout successful") -> "ચેક આઉટ સફળ થયું!"
            lower.contains("attendance marked") || lower.contains("attendance saved") -> "હાજરી નોંધાઈ ગઈ!"
            lower.contains("already checked in") -> "પહેલેથી ચેક ઇન થયેલું છે. ચેક આઉટ કરવા માટે OUT પસંદ કરો."
            lower.contains("already checked out") -> "પહેલેથી ચેક આઉટ થયેલું છે. ચેક ઇન કરવા માટે IN પસંદ કરો."
            lower.contains("not recognized") -> "ચહેરો ઓળખાયો નથી. કૃપા કરીને કેમેરા સામે સીધા જુઓ."
            lower.contains("no face detected") -> "કેમેરામાં કોઈ ચહેરો મળ્યો નથી. કૃપા કરીને કેમેરા સામે સીધા જુઓ."
            lower.contains("geofence") || lower.contains("outside") -> "તમે રજિસ્ટર્ડ ઓફિસ / સાઇટ પરિસરની બહાર છો."
            lower.contains("gps location is not available") || lower.contains("gps is turned off") -> "GPS લોકેશન ઉપલબ્ધ નથી. કૃપા કરીને GPS ચાલુ કરો."
            lower.contains("acquiring location") -> "લોકેશન શોધી રહ્યાં છીએ. કૃપા કરીને રાહ જુઓ..."
            lower.contains("log in with your phone number") -> "હાજરી નોંધાવતા પહેલાં કૃપા કરીને ફોન નંબરથી લોગિન કરો."
            lower.contains("internet restored") -> "ઇન્ટરનેટ ફરી શરૂ થયું."
            lower.contains("offline attendance synced") -> "ઓફલાઇન હાજરી સિંક થઈ ગઈ."
            lower.contains("not logged in") -> "તમે લૉગિન નથી. કૃપા કરીને પહેલા લૉગિન કરો."
            lower.contains("failed to load today") -> "આજની હાજરી લોડ થઈ શકી નથી."
            lower.contains("cannot reach") || lower.contains("failed to connect") || lower.contains("did not respond") || lower.contains("connection error") || lower.contains("network error") || lower.contains("network request failed") || lower.contains("failed to reach") -> "સર્વર સાથે કનેક્ટ થઈ શક્યું નથી. ઇન્ટરનેટ અથવા સર્વર તપાસો."
            lower.contains("failed to load employees") -> "કર્મચારીઓની યાદી લોડ થઈ શકી નથી."
            lower.contains("no face scan captured") -> "હજુ સુધી કોઈ ચહેરો સ્કેન થયો નથી."
            lower.contains("enrolled successfully") -> "ચહેરો સફળતાપૂર્વક નોંધાયો!"
            lower.contains("failed to register face") -> "ચહેરો નોંધી શકાયો નથી."
            lower.contains("android unique code must") -> "એન્ડ્રોઇડ યુનિક કોડ 4 થી 64 અક્ષર અથવા અંકનો હોવો જોઈએ."
            lower.contains("android code was not found") -> "એન્ડ્રોઇડ કોડ મળ્યો નથી."
            lower.contains("enter the android unique code") -> "લૉગિન પહેલા એન્ડ્રોઇડ યુનિક કોડ દાખલ કરો."
            lower.contains("username and password are required") -> "યુઝર નેમ અને પાસવર્ડ જરૂરી છે."
            lower.contains("login failed") -> "લૉગિન નિષ્ફળ ગયું."
            lower.contains("session expired") -> "સત્ર સમાપ્ત થયું. કૃપા કરીને ફરી લૉગિન કરો."
            lower.contains("access denied") -> "પ્રવેશ નકારાયો."
            lower.contains("api endpoint not found") -> "API એન્ડપોઇન્ટ મળ્યો નથી. સર્વર URL તપાસો."
            lower.contains("server error") -> "સર્વર ભૂલ. કૃપા કરીને પછી ફરી પ્રયાસ કરો."
            lower.contains("could not mark attendance") -> "હાજરી નોંધી શકાઈ નથી."
            lower.contains("precise location unavailable") -> "ચોક્કસ લોકેશન ઉપલબ્ધ નથી. હાઈ-એક્યુરસી GPS ચાલુ કરો."
            lower.contains("saved offline") -> "હાજરી ઓફલાઇન સાચવી. ઇન્ટરનેટ આવે ત્યારે સિંક થશે."
            lower.contains("camera error") -> "કેમેરા ભૂલ. કૃપા કરીને કેમેરા પરવાનગી તપાસો."
            lower.contains("face model error") -> "ફેસ મૉડલ લોડ થયું નથી."
            else -> message
        }
    }

    override fun localizeActivity(text: String): String {
        if (text.isBlank() || text == "--") return text
        return text
            .replace(Regex("(?i)check[- ]?out at"), "ચેક-આઉટ:")
            .replace(Regex("(?i)check[- ]?in at"), "ચેક-ઇન:")
            .replace(Regex("(?i)checked out"), "ચેક આઉટ થયું")
            .replace(Regex("(?i)checked in"), "ચેક ઇન થયું")
            .replace(Regex("(?i)\\(offline\\)"), "(ઑફલાઇન)")
    }
}
