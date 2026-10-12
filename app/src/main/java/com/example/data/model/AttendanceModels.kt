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
    @Json(name = "default_attendance_type")
    val defaultAttendanceType: String? = null,
    @Json(name = "server_time")
    val serverTime: String? = null
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
    val lastActivityAt: String? = null
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
