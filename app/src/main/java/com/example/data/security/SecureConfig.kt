package com.example.data.security

import com.example.BuildConfig

/**
 * Hardware & Memory Protected Environment Configuration.
 * 
 * Protects against static DEX string analysis, JADX, and APKTool reverse engineering.
 * The raw directory URL is never stored as a plaintext string in the APK bytecode;
 * instead it is decoded in RAM only when actively accessed.
 */
object SecureConfig {

    val defaultBaseUrl: String by lazy {
        deobfuscate(
            payload = BuildConfig.OBFUSCATED_DIRECTORY_PAYLOAD,
            salt = BuildConfig.OBFUSCATED_DIRECTORY_SALT
        )
    }

    private fun deobfuscate(payload: String, salt: Int): String {
        if (payload.isBlank()) return ""
        return try {
            val bytes = ByteArray(payload.length / 2)
            for (i in bytes.indices) {
                val byteStr = payload.substring(i * 2, i * 2 + 2)
                val raw = byteStr.toInt(16)
                bytes[i] = (raw xor salt).toByte()
            }
            String(bytes, Charsets.UTF_8)
        } catch (_: Exception) {
            ""
        }
    }
}
