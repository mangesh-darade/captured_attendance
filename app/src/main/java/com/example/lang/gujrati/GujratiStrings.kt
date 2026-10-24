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
    override val inButton: String = "IN"
    override val outButton: String = "OUT"
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
    override val lastActivityPrefix: String = "છેલ્લી પ્રવૃત્તિ"
    override val poweredBy: String = "Powered by"

    // Bottom Navigation Tabs
    override val homeTab: String = "હોમ"
    override val captureTab: String = "હાજરી"
    override val settingsTab: String = "સેટિંગ્સ"

    // Merchant Code & Login Screen
    override val merchantCodeLabel: String = "મર્ચન્ટ કોડ (Merchant Code)"
    override val merchantCodePlaceholder: String = "૫ અંકનો કોડ દાખલ કરો (દા.ત. LH022)"
    override val phoneLabel: String = "એડમિન / સુપરવાઇઝર ફોન નંબર"
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
    override val todayAttendanceTitle: String = "આજની હાજરી (Today's Attendance)"
    override val todayAttendanceSubtitle: String = "આજના કર્મચારીઓની હાજરી અને પંચ વિગતો"
    override val searchEmployeePlaceholder: String = "કર્મચારી નામ અથવા આઈડીથી શોધો..."
    override val noRecordsToday: String = "આજ માટે કોઈ હાજરી રેકોર્ડ મળ્યો નથી."
    override val totalPresent: String = "હાજર"
    override val totalPunches: String = "કુલ પંચ"

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
            else -> message
        }
    }

    override fun localizeActivity(text: String): String {
        if (text.isBlank() || text == "--") return text
        return text
            .replace("Check-out at", "ચેક-આઉટ:")
            .replace("Check-in at", "ચેક-ઇન:")
            .replace("Check out at", "ચેક-આઉટ:")
            .replace("Check in at", "ચેક-ઇન:")
    }
}
