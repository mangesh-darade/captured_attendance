package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
        val KEY_COMPANY_CODE = stringPreferencesKey("tenant_company_code")
        val KEY_MASTER_URL = stringPreferencesKey("merchant_directory_url")
        val KEY_ANDROID_CODE = stringPreferencesKey("merchant_android_code")
        val KEY_SETUP_COMPLETE = booleanPreferencesKey("merchant_setup_complete")
        val KEY_DEVICE_TOKEN = stringPreferencesKey("device_token")
        val KEY_DEVICE_USER_ID = stringPreferencesKey("device_user_id")
        val KEY_DEVICE_USER_NAME = stringPreferencesKey("device_user_name")
        val KEY_LAST_PHONE = stringPreferencesKey("device_last_phone")
        val KEY_SITES = stringPreferencesKey("attendance_sites_json")
        val KEY_OFFLINE_QUEUE = stringPreferencesKey("attendance_offline_queue")

        const val DEFAULT_BASE_URL = "https://qa18.elintom.us/"
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

    val companyCodeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_COMPANY_CODE] ?: ""
    }

    val masterUrlFlow: Flow<String> = context.dataStore.data.map { prefs ->
        val saved = prefs[KEY_MASTER_URL] ?: prefs[KEY_TENANT_BASE_URL]
        if (saved.isNullOrBlank() || saved.contains("192.168.") || saved.contains("localhost") || saved.contains("127.0.0.1") || saved.contains("10.0.2.2")) {
            DEFAULT_BASE_URL
        } else {
            saved
        }
    }

    val androidCodeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_ANDROID_CODE] ?: ""
    }

    val setupCompleteFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SETUP_COMPLETE] ?: false
    }

    val deviceTokenFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_DEVICE_TOKEN] ?: ""
    }

    val deviceUserNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_DEVICE_USER_NAME] ?: ""
    }

    val lastPhoneFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LAST_PHONE] ?: ""
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

    suspend fun setTenantSettings(url: String, apiKey: String, autoSubmit: Boolean = true, companyCode: String = "") {
        context.dataStore.edit { prefs ->
            prefs[KEY_TENANT_BASE_URL] = url
            prefs[KEY_API_KEY] = apiKey
            prefs[KEY_AUTO_SUBMIT] = autoSubmit
            if (companyCode.isNotBlank()) {
                prefs[KEY_COMPANY_CODE] = companyCode.trim()
            }
        }
    }

    suspend fun setAutoSubmit(autoSubmit: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AUTO_SUBMIT] = autoSubmit
        }
    }

    suspend fun saveMasterUrl(url: String, autoSubmit: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MASTER_URL] = url
            prefs[KEY_AUTO_SUBMIT] = autoSubmit
        }
    }

    suspend fun saveMerchantSetup(merchantUrl: String, androidCode: String, companyCode: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TENANT_BASE_URL] = merchantUrl
            prefs[KEY_ANDROID_CODE] = androidCode
            prefs[KEY_COMPANY_CODE] = companyCode
            prefs[KEY_SETUP_COMPLETE] = true
            prefs[KEY_API_KEY] = ""
        }
    }

    suspend fun clearMerchantSetup() {
        context.dataStore.edit { prefs ->
            val master = prefs[KEY_MASTER_URL] ?: prefs[KEY_TENANT_BASE_URL] ?: DEFAULT_BASE_URL
            prefs[KEY_MASTER_URL] = master
            prefs[KEY_TENANT_BASE_URL] = master
            prefs[KEY_ANDROID_CODE] = ""
            prefs[KEY_COMPANY_CODE] = ""
            prefs[KEY_SETUP_COMPLETE] = false
            prefs[KEY_DEVICE_TOKEN] = ""
            prefs[KEY_DEVICE_USER_ID] = ""
            prefs[KEY_DEVICE_USER_NAME] = ""
            prefs[KEY_API_KEY] = ""
        }
    }

    suspend fun setSetupIncomplete() {
        context.dataStore.edit { prefs ->
            prefs[KEY_SETUP_COMPLETE] = false
            prefs[KEY_DEVICE_TOKEN] = ""
            prefs[KEY_DEVICE_USER_ID] = ""
            prefs[KEY_DEVICE_USER_NAME] = ""
        }
    }

    suspend fun saveDeviceLogin(
        baseUrl: String,
        companyCode: String,
        token: String,
        userId: Int,
        userName: String,
        phone: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TENANT_BASE_URL] = baseUrl
            prefs[KEY_COMPANY_CODE] = companyCode
            prefs[KEY_DEVICE_TOKEN] = token
            prefs[KEY_DEVICE_USER_ID] = userId.toString()
            prefs[KEY_DEVICE_USER_NAME] = userName
            prefs[KEY_LAST_PHONE] = phone
            prefs[KEY_API_KEY] = ""
        }
    }

    suspend fun clearDeviceLogin() {
        context.dataStore.edit { prefs ->
            prefs[KEY_DEVICE_TOKEN] = ""
            prefs[KEY_DEVICE_USER_ID] = ""
            prefs[KEY_DEVICE_USER_NAME] = ""
            prefs[KEY_API_KEY] = ""
        }
    }

    suspend fun saveCachedSites(json: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SITES] = json
        }
    }

    suspend fun readCachedSites(): String {
        return context.dataStore.data.first()[KEY_SITES] ?: ""
    }

    suspend fun readOfflineQueue(): String {
        return context.dataStore.data.first()[KEY_OFFLINE_QUEUE] ?: ""
    }

    suspend fun saveOfflineQueue(json: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_OFFLINE_QUEUE] = json
        }
    }
}
