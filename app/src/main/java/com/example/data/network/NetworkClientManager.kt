package com.example.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import com.example.data.model.DeriveLocationPreviewResponse
import com.example.data.model.StoreAttendanceResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

data class CapturedPageInfo(
    val token: String?,
    val tenantLogoUrl: String?,
    val tenantSiteName: String?
)

/**
 * Logo on the site login screen. Skips the Elintom product mark and favicon
 * when the uploaded company logo is also present.
 */
internal fun pickLoginScreenLogo(html: String): String? {
    val imgPattern = Pattern.compile("<img\\b[^>]*>", Pattern.CASE_INSENSITIVE)
    val srcPattern = Pattern.compile("(?:src|data-src)=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE)
    val candidates = mutableListOf<String>()
    val matcher = imgPattern.matcher(html)
    while (matcher.find()) {
        val tag = matcher.group(0) ?: continue
        val srcMatcher = srcPattern.matcher(tag)
        if (!srcMatcher.find()) continue
        val src = srcMatcher.group(1)?.trim().orEmpty()
        if (src.isBlank()) continue
        val blob = (tag + " " + src).lowercase()
        if (blob.contains("logo")) candidates.add(src)
    }
    return candidates.firstOrNull { src ->
        val value = src.lowercase()
        !value.contains("elintom") && !value.contains("favicon")
    } ?: candidates.firstOrNull { !it.lowercase().contains("favicon") }
}

class NetworkClientManager(private val context: Context) {

    val cookieJar = PersistentCookieJar(context)

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    var currentApiKey: String = ""

    private val authInterceptor = okhttp3.Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
        if (currentApiKey.isNotBlank()) {
            builder.header("X-API-KEY", currentApiKey)
        }
        chain.proceed(builder.build())
    }

    val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val nominatimRetrofit = Retrofit.Builder()
        .baseUrl("https://nominatim.openstreetmap.org/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val nominatimService: NominatimService = nominatimRetrofit.create(NominatimService::class.java)

    // Base Retrofit for attendance service (endpoints use dynamic @Url)
    private val attendanceRetrofit = Retrofit.Builder()
        .baseUrl("https://attendance.elintom.com/") // default fallback, dynamic @Url overrides
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val attendanceService: AttendanceApiService = attendanceRetrofit.create(AttendanceApiService::class.java)

    private var cachedToken: String? = null
    private var lastCapturedTimestamp: Long = 0L
    private var cachedLogoUrl: String? = null
    private var cachedSiteName: String? = null

    fun isCapturedExpired(): Boolean {
        return (System.currentTimeMillis() - lastCapturedTimestamp) > (60 * 60 * 1000L) // 1 hour
    }

    suspend fun getOrFetchToken(baseUrl: String, forceRefresh: Boolean = false): CapturedPageInfo {
        if (!isOnline()) {
            val host = Uri.parse(sanitizeBaseUrl(baseUrl)).host ?: ""
            val token = cookieJar.getTokenCookie(host) ?: cookieJar.getTokenCookie(null)
            return CapturedPageInfo(token ?: cachedToken, tenantLogoUrl = null, tenantSiteName = cachedSiteName)
        }
        if (!forceRefresh && cachedToken != null && !isCapturedExpired()) {
            return CapturedPageInfo(cachedToken, cachedLogoUrl, cachedSiteName)
        }
        return fetchCapturedPage(baseUrl)
    }

    fun isOnline(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun fetchCapturedPage(baseUrl: String): CapturedPageInfo {
        val sanitizedBase = sanitizeBaseUrl(baseUrl)

        // Try clean REST API first
        try {
            val apiUrl = "${sanitizedBase}attendance_api/init"
            val apiResponse = attendanceService.getInitData(apiUrl)
            if (apiResponse.isSuccessful && apiResponse.body()?.data != null) {
                val data = apiResponse.body()!!.data!!
                val siteName = data.siteName
                val logoUrl = normalizeLogo(sanitizedBase, data.uploadedLogoUrl())
                    ?: fetchLoginScreenLogo(sanitizedBase)
                cachedLogoUrl = logoUrl
                cachedSiteName = siteName
                cachedToken = ""
                lastCapturedTimestamp = System.currentTimeMillis()
                return CapturedPageInfo(token = "", tenantLogoUrl = logoUrl, tenantSiteName = siteName)
            }
        } catch (e: Exception) {
            // Fallback to legacy HTML page scrape if attendance_api/init not reachable
        }

        val url = "${sanitizedBase}attendance/captured"
        return try {
            val response = attendanceService.getCapturedPage(url)
            val host = Uri.parse(sanitizedBase).host ?: ""
            val html = response.body()?.string() ?: ""

            // Parse token from HTML input or fallback to cookieJar
            var token: String? = parseInputToken(html)
            if (token.isNullOrBlank()) {
                token = cookieJar.getTokenCookie(host)
            }
            if (token.isNullOrBlank()) {
                token = cookieJar.getTokenCookie(null)
            }

            // Login-screen logo of this site. Fall back to the tenant mark on the captured page.
            val logoUrl = fetchLoginScreenLogo(sanitizedBase) ?: parseTenantLogo(html, sanitizedBase)
            val siteName = parseSiteName(html)

            cachedToken = token
            cachedLogoUrl = logoUrl
            cachedSiteName = siteName
            lastCapturedTimestamp = System.currentTimeMillis()

            CapturedPageInfo(token, logoUrl, siteName)
        } catch (e: Exception) {
            e.printStackTrace()
            val host = Uri.parse(sanitizedBase).host ?: ""
            val token = cookieJar.getTokenCookie(host) ?: cookieJar.getTokenCookie(null)
            CapturedPageInfo(token ?: cachedToken, tenantLogoUrl = null, tenantSiteName = cachedSiteName)
        }
    }

    fun parseInputToken(html: String): String? {
        val pattern = Pattern.compile("<input[^>]*name=[\"']token[\"'][^>]*value=[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(html)
        if (matcher.find()) {
            return matcher.group(1)
        }
        val altPattern = Pattern.compile("<input[^>]*value=[\"']([^\"']*)[\"'][^>]*name=[\"']token[\"']", Pattern.CASE_INSENSITIVE)
        val altMatcher = altPattern.matcher(html)
        if (altMatcher.find()) {
            return altMatcher.group(1)
        }
        return null
    }

    private suspend fun fetchLoginScreenLogo(baseUrl: String): String? {
        return try {
            val response = attendanceService.getCapturedPage("${baseUrl}login")
            val html = response.body()?.string() ?: return null
            val raw = pickLoginScreenLogo(html) ?: return null
            normalizeLogo(baseUrl, raw)
        } catch (e: Exception) {
            null
        }
    }

    private fun normalizeLogo(baseUrl: String, raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        return resolveUrl(baseUrl, raw.trim())
    }

    private fun parseTenantLogo(html: String, baseUrl: String): String? {
        val imgPattern = Pattern.compile("<img[^>]+class=[\"'][^\"']*attendance-logo[^\"']*[\"'][^>]*>", Pattern.CASE_INSENSITIVE)
        val matcher = imgPattern.matcher(html)
        val logos = mutableListOf<String>()

        while (matcher.find()) {
            val imgTag = matcher.group(0) ?: continue
            val srcPattern = Pattern.compile("src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE)
            val srcMatcher = srcPattern.matcher(imgTag)
            if (srcMatcher.find()) {
                val src = srcMatcher.group(1) ?: continue
                logos.add(src)
            }
        }

        // Rule: 2nd img.attendance-logo (left is ElintOm logo, right is tenant logo)
        val rawLogo = if (logos.size >= 2) logos[1] else logos.lastOrNull() ?: return null
        return resolveUrl(baseUrl, rawLogo)
    }

    private fun parseSiteName(html: String): String? {
        // Look for tenant name class or header text
        val classPattern = Pattern.compile("<(?:span|h1|h2|h3|p|div)[^>]*class=[\"'][^\"']*(?:tenant-site-name|tenant-name|site-name|tenant_name)[^\"']*[\"'][^>]*>([^<]+)</", Pattern.CASE_INSENSITIVE)
        val matcher = classPattern.matcher(html)
        if (matcher.find()) {
            return matcher.group(1)?.trim()
        }

        // Look for <title>
        val titlePattern = Pattern.compile("<title>([^<]+)</title>", Pattern.CASE_INSENSITIVE)
        val titleMatcher = titlePattern.matcher(html)
        if (titleMatcher.find()) {
            val title = titleMatcher.group(1)?.trim() ?: ""
            val cleaned = title.replace(Regex("(?i)\\b(attendance|capture|kiosk)\\b"), "")
                .replace(Regex("[-|•]"), "")
                .trim()
            if (cleaned.isNotBlank()) return cleaned
        }
        return null
    }

    private fun resolveUrl(baseUrl: String, path: String): String {
        return when {
            path.startsWith("http://") || path.startsWith("https://") -> path
            path.startsWith("//") -> "https:$path"
            path.startsWith("/") -> {
                val uri = Uri.parse(baseUrl)
                "${uri.scheme}://${uri.authority}$path"
            }
            else -> "$baseUrl$path"
        }
    }

    fun sanitizeBaseUrl(raw: String): String {
        var trimmed = raw.trim()
        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            trimmed = "https://$trimmed"
        }
        if (!trimmed.endsWith("/")) {
            trimmed = "$trimmed/"
        }
        return trimmed
    }
}
