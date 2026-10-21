package com.example.lang

import com.example.lang.english.EnglishStrings
import com.example.lang.gujrati.GujratiStrings
import com.example.lang.hindi.HindiStrings
import com.example.lang.marathi.MarathiStrings

/**
 * Central manager to fetch localized strings based on the selected language code.
 */
object LangManager {
    val english: AppStrings = EnglishStrings
    val marathi: AppStrings = MarathiStrings
    val hindi: AppStrings = HindiStrings
    val gujrati: AppStrings = GujratiStrings

    /**
     * Resolves the AppStrings implementation for the given language code or name.
     * Supported codes: "en", "mr", "hi", "gu".
     */
    fun getStrings(lang: String): AppStrings {
        return when (lang.lowercase().trim()) {
            "mr", "marathi" -> marathi
            "hi", "hindi" -> hindi
            "gu", "gujrati", "gujarati" -> gujrati
            else -> english
        }
    }
}
