package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.HandlerThread
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.data.network.NominatimService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Stop waiting once GPS is this tight. */
private const val TARGET_ACCURACY_METERS = 20f

/** A coarser fix than this can place a Pune device on a Mumbai site. Reject it. */
private const val MAX_ACCEPTED_ACCURACY_METERS = 80f

private const val ACCURATE_FIX_TIMEOUT_MS = 15_000L

internal data class LandmarkParts(
    val name: String = "",
    val amenity: String = "",
    val building: String = "",
    val office: String = "",
    val shop: String = "",
    val houseNumber: String = "",
    val road: String = "",
    val neighbourhood: String = "",
    val residential: String = "",
    val suburb: String = "",
    val city: String = "",
    val displayName: String = ""
)

/**
 * Prefer the named place, then the street address, then the neighbourhood.
 * Road + suburb + city alone is what made the landmark look like the wrong area.
 */
internal fun formatLandmark(parts: LandmarkParts): String {
    val road = parts.road.trim()
    val place = listOf(parts.name, parts.amenity, parts.building, parts.office, parts.shop)
        .map { it.trim() }
        .firstOrNull { it.isNotBlank() && !it.equals(road, ignoreCase = true) }
        .orEmpty()
    val street = listOf(parts.houseNumber.trim(), road)
        .filter { it.isNotBlank() }
        .joinToString(" ")
    // residential is the local area people use (Kothrud). suburb is often the
    // neighbouring polygon (Bavdhan) and was replacing the real locality.
    val area = listOf(parts.neighbourhood, parts.residential, parts.suburb, parts.city)
        .map { it.trim() }
        .firstOrNull { it.isNotBlank() }
        .orEmpty()

    val formatted = listOf(place, street, area)
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase() }
        .take(3)
    if (formatted.isNotEmpty()) {
        return formatted.joinToString(", ")
    }

    return parts.displayName
        .split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(", ")
}

class LocationHelper(
    private val context: Context,
    private val nominatimService: NominatimService
) {
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /** GPS callbacks must not run on the UI thread. */
    private val locationLooper: Looper by lazy {
        HandlerThread("attendance-location").apply { start() }.looper
    }

    private fun hasFineLocation(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getLocation(): Result<Location> {
        if (!hasFineLocation()) {
            return Result.failure(
                SecurityException("Precise location is required for the correct landmark.")
            )
        }

        return try {
            val fix = requestAccurateFix()
            if (fix != null) {
                Result.success(fix)
            } else {
                Result.failure(Exception("Location unavailable. Turn on high-accuracy GPS and try again."))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Location unavailable. Please check location permissions."))
        }
    }

    /**
     * Waits for a fresh high-accuracy fix. A cached or cell-tower location was
     * being accepted immediately, so the landmark resolved to the wrong place.
     */
    @SuppressLint("MissingPermission")
    private suspend fun requestAccurateFix(): Location? {
        val best = AtomicReference<Location?>(null)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000L)
            .setMinUpdateIntervalMillis(500L)
            .setWaitForAccurateLocation(true)
            .setGranularity(Granularity.GRANULARITY_FINE)
            .setMaxUpdateAgeMillis(0L)
            .setDurationMillis(ACCURATE_FIX_TIMEOUT_MS)
            .build()

        val precise = withTimeoutOrNull(ACCURATE_FIX_TIMEOUT_MS) {
            suspendCancellableCoroutine { cont ->
                val finished = AtomicBoolean(false)
                val callback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        for (location in result.locations) {
                            if (!location.hasAccuracy() || location.accuracy <= 0f) continue
                            best.updateAndGet { current ->
                                if (current == null || location.accuracy < current.accuracy) location else current
                            }
                        }
                        val chosen = best.get()
                        if (chosen != null && chosen.accuracy <= TARGET_ACCURACY_METERS) {
                            if (finished.compareAndSet(false, true)) {
                                fusedClient.removeLocationUpdates(this)
                                if (cont.isActive) cont.resume(chosen)
                            }
                        }
                    }
                }

                cont.invokeOnCancellation {
                    fusedClient.removeLocationUpdates(callback)
                }

                fusedClient.requestLocationUpdates(request, callback, locationLooper)
                    .addOnFailureListener { error ->
                        fusedClient.removeLocationUpdates(callback)
                        if (finished.compareAndSet(false, true) && cont.isActive) {
                            cont.resumeWithException(error)
                        }
                    }
            }
        }

        if (precise != null) return precise
        val fallback = best.get()
        return if (fallback != null && fallback.accuracy <= MAX_ACCEPTED_ACCURACY_METERS) {
            fallback
        } else {
            null
        }
    }

    suspend fun reverseGeocode(latitude: Double, longitude: Double): String {
        return try {
            val response = nominatimService.reverseGeocode(
                lat = latitude,
                lon = longitude,
                zoom = 18,
                addressDetails = 1,
                userAgent = "CaptureAttendanceKiosk/1.0 (Android Native; support@elintom.com)"
            )
            if (!response.isSuccessful) return ""
            val body = response.body()?.string() ?: return ""
            formatLandmark(parseLandmark(JSONObject(body)))
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun parseLandmark(json: JSONObject): LandmarkParts {
        val address = json.optJSONObject("address")
        return LandmarkParts(
            name = json.optString("name"),
            amenity = address?.optString("amenity").orEmpty(),
            building = address?.optString("building").orEmpty(),
            office = address?.optString("office").orEmpty(),
            shop = address?.optString("shop").orEmpty(),
            houseNumber = address?.optString("house_number").orEmpty(),
            road = address?.optString("road").orEmpty(),
            neighbourhood = address?.optString("neighbourhood").orEmpty(),
            residential = address?.optString("residential").orEmpty(),
            suburb = address?.optString("suburb").orEmpty(),
            city = address?.optString("city").orEmpty()
                .ifBlank { address?.optString("town").orEmpty() }
                .ifBlank { address?.optString("village").orEmpty() },
            displayName = json.optString("display_name")
        )
    }
}
