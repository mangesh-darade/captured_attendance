package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.data.network.NominatimService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private suspend fun <T> Task<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result ->
            cont.resume(result)
        }
        addOnFailureListener { exception ->
            cont.resumeWithException(exception)
        }
        addOnCanceledListener {
            cont.cancel()
        }
    }

class LocationHelper(
    private val context: Context,
    private val nominatimService: NominatimService
) {
    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getLocation(): Result<Location> {
        if (!hasLocationPermission()) {
            return Result.failure(SecurityException("Location permission denied."))
        }

        try {
            // High accuracy with 10s timeout
            val highAccLocation = withTimeoutOrNull(10_000L) {
                val tokenSource = CancellationTokenSource()
                fusedClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    tokenSource.token
                ).await()
            }

            if (highAccLocation != null) {
                return Result.success(highAccLocation)
            }

            // Fallback: low/balanced accuracy or lastLocation
            val fallbackLocation = withTimeoutOrNull(4_000L) {
                val tokenSource = CancellationTokenSource()
                val loc = fusedClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    tokenSource.token
                ).await()
                loc ?: fusedClient.lastLocation.await()
            }

            if (fallbackLocation != null) {
                return Result.success(fallbackLocation)
            }

            return Result.failure(Exception("Location unavailable. Please check location permissions."))
        } catch (e: Exception) {
            e.printStackTrace()
            // Check last location as final fallback
            try {
                val lastLoc = fusedClient.lastLocation.await()
                if (lastLoc != null) {
                    return Result.success(lastLoc)
                }
            } catch (ignored: Exception) {}
            return Result.failure(Exception("Location unavailable. Please check location permissions."))
        }
    }

    suspend fun reverseGeocode(latitude: Double, longitude: Double): String {
        return try {
            val response = nominatimService.reverseGeocode(
                lat = latitude,
                lon = longitude,
                userAgent = "CaptureAttendanceKiosk/1.0 (Android Native; support@elintom.com)"
            )
            if (response.isSuccessful) {
                val body = response.body()?.string() ?: return ""
                val json = JSONObject(body)
                val displayName = json.optString("display_name", "")
                val addressObj = json.optJSONObject("address")

                if (addressObj != null) {
                    val road = addressObj.optString("road", "")
                    val suburb = addressObj.optString("suburb", addressObj.optString("neighbourhood", ""))
                    val city = addressObj.optString("city", addressObj.optString("town", ""))
                    val parts = listOf(road, suburb, city).filter { it.isNotBlank() }
                    if (parts.isNotEmpty()) {
                        return parts.joinToString(", ")
                    }
                }
                if (displayName.isNotBlank()) {
                    return displayName.split(",").take(3).joinToString(",").trim()
                }
            }
            ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
