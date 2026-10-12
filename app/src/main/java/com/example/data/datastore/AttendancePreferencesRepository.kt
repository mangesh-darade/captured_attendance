package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "attendance_settings")

data class CachedAttendanceData(
    val date: String?,
    val lastType: String?,
    val lastAt: String?,
    val nextAction: String?
) {
    fun isToday(): Boolean {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return date == todayStr
    }
}

class AttendancePreferencesRepository(private val context: Context) {

    companion object {
        val KEY_CACHED_DATE = stringPreferencesKey("attendance_cached_date")
        val KEY_CACHED_LAST_TYPE = stringPreferencesKey("attendance_cached_last_type")
        val KEY_CACHED_LAST_AT = stringPreferencesKey("attendance_cached_last_at")
        val KEY_CACHED_NEXT_ACTION = stringPreferencesKey("attendance_cached_next_action")
        val KEY_TENANT_BASE_URL = stringPreferencesKey("tenant_base_url")
        val KEY_API_KEY = stringPreferencesKey("tenant_api_key")
        val KEY_AUTO_SUBMIT = booleanPreferencesKey("attendance_auto_submit_enabled")

        const val DEFAULT_BASE_URL = "https://attendance.elintom.com/"
    }

    val cachedAttendanceFlow: Flow<CachedAttendanceData> = context.dataStore.data.map { prefs ->
        CachedAttendanceData(
            date = prefs[KEY_CACHED_DATE],
            lastType = prefs[KEY_CACHED_LAST_TYPE],
            lastAt = prefs[KEY_CACHED_LAST_AT],
            nextAction = prefs[KEY_CACHED_NEXT_ACTION]
        )
    }

    val tenantBaseUrlFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_TENANT_BASE_URL] ?: DEFAULT_BASE_URL
    }

    val tenantApiKeyFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_API_KEY] ?: ""
    }

    val autoSubmitFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_SUBMIT] ?: true
    }

    suspend fun saveCachedAttendance(
        lastType: String?,
        lastAt: String?,
        nextAction: String?
    ) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        context.dataStore.edit { prefs ->
            prefs[KEY_CACHED_DATE] = today
            if (lastType != null) prefs[KEY_CACHED_LAST_TYPE] = lastType
            if (lastAt != null) prefs[KEY_CACHED_LAST_AT] = lastAt
            if (nextAction != null) prefs[KEY_CACHED_NEXT_ACTION] = nextAction
        }
    }

    suspend fun setTenantBaseUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TENANT_BASE_URL] = url
        }
    }

    suspend fun setTenantSettings(url: String, apiKey: String, autoSubmit: Boolean = true) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TENANT_BASE_URL] = url
            prefs[KEY_API_KEY] = apiKey
            prefs[KEY_AUTO_SUBMIT] = autoSubmit
        }
    }

    suspend fun setAutoSubmit(autoSubmit: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AUTO_SUBMIT] = autoSubmit
        }
    }
}
