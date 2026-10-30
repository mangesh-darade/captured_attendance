package com.example.location

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class AreaWatch(
    val baseUrl: String,
    val token: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val radiusM: Int,
    val inside: Boolean
)

object AreaTrackStore {
    private const val PREFS = "area_track"
    private const val KEY_ACTIVE = "active"
    private const val KEY_BASE = "base_url"
    private const val KEY_TOKEN = "token"
    private const val KEY_NAME = "location"
    private const val KEY_LAT = "lat"
    private const val KEY_LNG = "lng"
    private const val KEY_RADIUS = "radius"
    private const val KEY_INSIDE = "inside"

    fun save(context: Context, watch: AreaWatch) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ACTIVE, true)
            .putString(KEY_BASE, watch.baseUrl)
            .putString(KEY_TOKEN, watch.token)
            .putString(KEY_NAME, watch.locationName)
            .putString(KEY_LAT, watch.latitude.toString())
            .putString(KEY_LNG, watch.longitude.toString())
            .putInt(KEY_RADIUS, watch.radiusM)
            .putBoolean(KEY_INSIDE, watch.inside)
            .apply()
    }

    fun updateInside(context: Context, inside: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_INSIDE, inside)
            .apply()
    }

    fun read(context: Context): AreaWatch? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_ACTIVE, false)) return null
        val base = prefs.getString(KEY_BASE, "").orEmpty()
        val token = prefs.getString(KEY_TOKEN, "").orEmpty()
        val name = prefs.getString(KEY_NAME, "").orEmpty()
        val lat = prefs.getString(KEY_LAT, "")?.toDoubleOrNull()
        val lng = prefs.getString(KEY_LNG, "")?.toDoubleOrNull()
        val radius = prefs.getInt(KEY_RADIUS, 0)
        if (base.isBlank() || token.isBlank() || name.isBlank() || lat == null || lng == null || radius <= 0) {
            return null
        }
        return AreaWatch(base, token, name, lat, lng, radius, prefs.getBoolean(KEY_INSIDE, true))
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }
}

class AreaTrackService : Service() {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()
    private val posting = AtomicBoolean(false)
    private var callback: LocationCallback? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopTracking()
            return START_NOT_STICKY
        }
        val watch = AreaTrackStore.read(this)
        if (watch == null || !hasLocationPermission()) {
            stopTracking()
            return START_NOT_STICKY
        }
        startAsForeground(watch.locationName)
        requestUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        removeUpdates()
        super.onDestroy()
    }

    private fun stopTracking() {
        removeUpdates()
        AreaTrackStore.clear(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun startAsForeground(locationName: String) {
        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Attendance area", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("Attendance area")
            .setContentText("Watching $locationName until check-out")
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestUpdates() {
        if (callback != null) return
        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, UPDATE_INTERVAL_MS)
            .setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL_MS)
            .setMinUpdateDistanceMeters(15f)
            .build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val fix = result.lastLocation ?: return
                onFix(fix.latitude, fix.longitude)
            }
        }
        callback = locationCallback
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    private fun removeUpdates() {
        val current = callback ?: return
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(current)
        callback = null
    }

    private fun onFix(lat: Double, lng: Double) {
        val watch = AreaTrackStore.read(this) ?: return
        val distance = distanceMeters(lat, lng, watch.latitude, watch.longitude)
        val inside = distance <= watch.radiusM
        if (inside == watch.inside || !posting.compareAndSet(false, true)) return
        Thread {
            try {
                val saved = postArea(watch, lat, lng)
                if (saved != null) {
                    AreaTrackStore.updateInside(this@AreaTrackService, saved)
                    if (!saved) {
                        com.example.reminder.ReminderScheduler.notifyLeftSite(this@AreaTrackService, watch.locationName)
                    }
                }
            } finally {
                posting.set(false)
            }
        }.start()
    }

    private fun postArea(watch: AreaWatch, lat: Double, lng: Double): Boolean? {
        val body = FormBody.Builder()
            .add("derived_location", watch.locationName)
            .add("latitude", lat.toString())
            .add("longitude", lng.toString())
            .add("token", watch.token)
            .build()
        val request = Request.Builder()
            .url(watch.baseUrl + "attendance_api/area_log")
            .header("Authorization", "Bearer ${watch.token}")
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            val text = response.body?.string().orEmpty()
            if (!response.isSuccessful || text.isBlank()) return null
            val json = JSONObject(text)
            if (!json.optString("status").equals("success", ignoreCase = true)) {
                if (json.optBoolean("tracking", true).not()) {
                    AreaTrackStore.clear(this)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
                return null
            }
            return json.optBoolean("inside")
        }
    }

    companion object {
        private const val CHANNEL_ID = "attendance_area"
        private const val NOTIFICATION_ID = 4108
        private const val ACTION_STOP = "com.example.area.STOP"
        private const val UPDATE_INTERVAL_MS = 20_000L
        private const val MIN_UPDATE_INTERVAL_MS = 10_000L

        fun start(context: Context, watch: AreaWatch) {
            AreaTrackStore.save(context, watch.copy(inside = true))
            val intent = Intent(context, AreaTrackService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            AreaTrackStore.clear(context)
            val intent = Intent(context, AreaTrackService::class.java).setAction(ACTION_STOP)
            try {
                context.startService(intent)
            } catch (_: Exception) {
            }
        }

        fun resume(context: Context) {
            if (AreaTrackStore.read(context) == null) return
            val intent = Intent(context, AreaTrackService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }
}

private fun distanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earth = 6371000.0
    val latDelta = Math.toRadians(lat2 - lat1)
    val lonDelta = Math.toRadians(lon2 - lon1)
    val a = sin(latDelta / 2) * sin(latDelta / 2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
        sin(lonDelta / 2) * sin(lonDelta / 2)
    return 2 * earth * asin(sqrt(a))
}
