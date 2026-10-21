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
