package com.example.data.network

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

class PersistentCookieJar(context: Context) : CookieJar {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("attendance_cookies", Context.MODE_PRIVATE)
    private val memoryStore = ConcurrentHashMap<String, MutableMap<String, Cookie>>()

    init {
        loadFromPrefs()
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val hostMap = memoryStore.getOrPut(host) { ConcurrentHashMap() }
        for (cookie in cookies) {
            hostMap[cookie.name] = cookie
        }
        persistToPrefs()
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        val hostMap = memoryStore[host] ?: return emptyList()
        val now = System.currentTimeMillis()
        val validCookies = mutableListOf<Cookie>()
        val it = hostMap.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next()
            val cookie = entry.value
            if (cookie.expiresAt < now) {
                it.remove()
            } else {
                validCookies.add(cookie)
            }
        }
        return validCookies
    }

    @Synchronized
    fun getTokenCookie(host: String? = null): String? {
        if (host != null) {
            memoryStore[host]?.get("token_cookie")?.value?.let { return it }
        }
        // Fallback: search any host
        for ((_, cookies) in memoryStore) {
            cookies["token_cookie"]?.value?.let { return it }
        }
        return null
    }

    @Synchronized
    fun clear() {
        memoryStore.clear()
        prefs.edit().clear().apply()
    }

    private fun persistToPrefs() {
        val editor = prefs.edit()
        editor.clear()
        for ((host, cookies) in memoryStore) {
            val serialized = cookies.values.joinToString(";;;") { serializeCookie(it) }
            editor.putString(host, serialized)
        }
        editor.apply()
    }

    private fun loadFromPrefs() {
        for ((host, value) in prefs.all) {
            if (value is String) {
                val hostMap = memoryStore.getOrPut(host) { ConcurrentHashMap() }
                val items = value.split(";;;")
                for (item in items) {
                    deserializeCookie(host, item)?.let { cookie ->
                        if (cookie.expiresAt > System.currentTimeMillis()) {
                            hostMap[cookie.name] = cookie
                        }
                    }
                }
            }
        }
    }

    private fun serializeCookie(cookie: Cookie): String {
        return "${cookie.name}|${cookie.value}|${cookie.expiresAt}|${cookie.domain}|${cookie.path}|${cookie.secure}|${cookie.httpOnly}"
    }

    private fun deserializeCookie(host: String, raw: String): Cookie? {
        val parts = raw.split("|")
        if (parts.size < 7) return null
        return try {
            val builder = Cookie.Builder()
                .name(parts[0])
                .value(parts[1])
                .expiresAt(parts[2].toLong())
                .domain(parts[3].ifBlank { host })
                .path(parts[4].ifBlank { "/" })
            if (parts[5].toBoolean()) builder.secure()
            if (parts[6].toBoolean()) builder.httpOnly()
            builder.build()
        } catch (e: Exception) {
            null
        }
    }
}
