package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationOption(
    val name: String,
    val distance: Any? = null
) {
    fun displayLabel(): String {
        return if (distance != null && distance.toString().isNotBlank()) {
            val distStr = distance.toString().trim()
            if (distStr.endsWith("m") || distStr.endsWith("km")) {
                "$name ($distStr)"
            } else {
                "$name (${distStr}m)"
            }
        } else {
            name
        }
    }

    /**
     * Sends pure name (strip " (12.3m)")
     */
    fun pureName(): String {
        return name.replace(Regex("\\s*\\([^)]*\\)$"), "").trim()
    }

    /** Distance from the device, in meters. "7.6km" and "7631.49" are both accepted. */
    fun distanceMeters(): Double? {
        val raw = distance?.toString()?.trim()?.lowercase() ?: return null
        if (raw.isBlank() || raw == "null") return null
        val kilometers = raw.endsWith("km")
        val number = raw.removeSuffix("km").removeSuffix("m").trim().toDoubleOrNull() ?: return null
        return if (kilometers) number * 1000.0 else number
    }
}

/** Office sites farther than this are not where the person is standing. */
const val NEARBY_LOCATION_METERS = 500.0

fun List<LocationOption>.sortedByDistance(): List<LocationOption> {
    return sortedBy { it.distanceMeters() ?: Double.MAX_VALUE }
}

/** Nearest site only when it is actually nearby. A 7 km "Mumbai" hit must not be auto-selected. */
fun List<LocationOption>.nearestWithin(maxMeters: Double = NEARBY_LOCATION_METERS): LocationOption? {
    return mapNotNull { option -> option.distanceMeters()?.let { option to it } }
        .filter { it.second <= maxMeters }
        .minByOrNull { it.second }
        ?.first
}

@JsonClass(generateAdapter = true)
data class DeriveLocationPreviewResponse(
    val status: String? = null,
    val message: String? = null,
    @Json(name = "derived_location")
    val derivedLocation: String? = null,
    @Json(name = "requires_location_selection")
    val requiresLocationSelection: Any? = null, // can be boolean or int/string
    @Json(name = "location_options")
    val locationOptions: List<LocationOption>? = null,
    @Json(name = "suggested_attendance_type")
    val suggestedAttendanceType: String? = null,
    @Json(name = "last_activity_type")
    val lastActivityType: String? = null,
    @Json(name = "last_activity_at")
    val lastActivityAt: String? = null,
    @Json(name = "selected_out_checkin_at")
    val selectedOutCheckinAt: String? = null,
    @Json(name = "selected_in_checkout_at")
    val selectedInCheckoutAt: String? = null
) {
    fun isLocationSelectionRequired(): Boolean {
        return when (requiresLocationSelection) {
            is Boolean -> requiresLocationSelection
            is Number -> requiresLocationSelection.toInt() != 0
            is String -> requiresLocationSelection.equals("true", ignoreCase = true) || requiresLocationSelection == "1"
            else -> false
        }
    }
}

@JsonClass(generateAdapter = true)
data class AttendanceInitData(
    @Json(name = "site_name")
    val siteName: String? = null,
    @Json(name = "tenant_logo_url")
    val tenantLogoUrl: String? = null,
    @Json(name = "login_logo")
    val loginLogo: String? = null,
    @Json(name = "site_logo")
    val siteLogo: String? = null,
    @Json(name = "company_logo")
    val companyLogo: String? = null,
    @Json(name = "logo_url")
    val logoUrl: String? = null,
    @Json(name = "default_attendance_type")
    val defaultAttendanceType: String? = null,
    @Json(name = "is_admin")
    val isAdmin: Boolean? = null,
    @Json(name = "server_time")
    val serverTime: String? = null
) {
    /** Logo uploaded for this site, the same one shown on its login screen. */
    fun uploadedLogoUrl(): String? {
        return listOf(tenantLogoUrl, loginLogo, siteLogo, companyLogo, logoUrl)
            .firstOrNull { !it.isNullOrBlank() }
    }
}

@JsonClass(generateAdapter = true)
data class AttendanceSetupResponse(
    val status: String? = null,
    val message: String? = null,
    val url: String? = null,
    val code: String? = null,
    val name: String? = null
)

@JsonClass(generateAdapter = true)
data class AttendanceLoginResponse(
    val status: String? = null,
    val message: String? = null,
    val token: String? = null,
    @Json(name = "user_id")
    val userId: Int? = null,
    @Json(name = "user_name")
    val userName: String? = null,
    @Json(name = "is_admin")
    val isAdmin: Boolean? = null,
    val code: String? = null
)

@JsonClass(generateAdapter = true)
data class AttendanceInitResponse(
    val status: String? = null,
    val message: String? = null,
    val data: AttendanceInitData? = null
)

@JsonClass(generateAdapter = true)
data class StoreAttendanceResponse(
    val status: String? = null,
    val message: String? = null,
    val action: String? = null,
    @Json(name = "next_action")
    val nextAction: String? = null,
    @Json(name = "user_name")
    val userName: String? = null,
    @Json(name = "last_activity_type")
    val lastActivityType: String? = null,
    @Json(name = "last_activity_at")
    val lastActivityAt: String? = null,
    @Json(name = "derived_location")
    val derivedLocation: String? = null,
    @Json(name = "site_latitude")
    val siteLatitude: Double? = null,
    @Json(name = "site_longitude")
    val siteLongitude: Double? = null,
    @Json(name = "radius_m")
    val radiusM: Int? = null
)

data class ToastMessage(
    val type: ToastType,
    val message: String,
    val durationMs: Long
)

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO
}

enum class AttendanceButtonState(val label: String) {
    CAPTURE("Capture"),
    SCANNING("Scanning..."),
    SUBMITTING("Submitting..."),
    RETAKE("Retake")
}

@JsonClass(generateAdapter = true)
data class TodayAttendanceSummary(
    @Json(name = "total_present") val totalPresent: Int = 0,
    @Json(name = "total_in") val totalIn: Int = 0,
    @Json(name = "total_out") val totalOut: Int = 0
)

@JsonClass(generateAdapter = true)
data class AttendanceRecordItem(
    val id: Int = 0,
    @Json(name = "user_id") val userId: Int = 0,
    @Json(name = "user_name") val userName: String = "",
    val phone: String? = null,
    val date: String? = null,
    @Json(name = "in_time") val inTime: String = "--:--",
    @Json(name = "out_time") val outTime: String? = null,
    @Json(name = "is_in") val isIn: Boolean = true,
    val location: String = "Main Site",
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AttendanceUserOption(
    val id: Int = 0,
    val name: String = "",
    val phone: String? = null
)

@JsonClass(generateAdapter = true)
data class TodayAttendanceResponse(
    val status: String? = null,
    val message: String? = null,
    val date: String? = null,
    @Json(name = "is_admin") val isAdmin: Boolean? = null,
    @Json(name = "caller_user_id") val callerUserId: Int? = null,
    @Json(name = "caller_user_name") val callerUserName: String? = null,
    @Json(name = "selected_user_id") val selectedUserId: Int? = null,
    val users: List<AttendanceUserOption>? = null,
    val summary: TodayAttendanceSummary? = null,
    val records: List<AttendanceRecordItem>? = null
)

@JsonClass(generateAdapter = true)
data class EnrollmentUserItem(
    val id: Int = 0,
    val name: String = "",
    val phone: String? = null,
    val email: String? = null,
    val company: String? = null,
    val group: String? = null,
    @Json(name = "has_face_enrolled") val hasFaceEnrolled: Boolean = false
)

@JsonClass(generateAdapter = true)
data class EnrollmentUsersResponse(
    val status: String? = null,
    val message: String? = null,
    val total: Int? = null,
    val users: List<EnrollmentUserItem>? = null
)

@JsonClass(generateAdapter = true)
data class RegisterFaceResponse(
    val status: String? = null,
    val message: String? = null,
    @Json(name = "user_id") val userId: Int? = null,
    @Json(name = "has_face_enrolled") val hasFaceEnrolled: Boolean? = null
)
