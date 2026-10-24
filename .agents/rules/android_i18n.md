# Android Internationalization & Localization Rule (Android Kiosk i18n)

When adding or modifying any screen, UI component, dialog, button, label, placeholder, toast, status message, or alert in the Android application (`captured_attendance`):

1. **NO Hardcoded UI Strings**:
   - Never write raw/hardcoded English text directly in Jetpack Compose UI components (`Text("...")`, placeholders, button titles, dialogs).

2. **Always Update `AppStrings` Interface First**:
   - Declare the new property in `app/src/main/java/com/example/lang/AppStrings.kt`.

3. **Mandatory Implementation in All Supported Languages**:
   - You MUST simultaneously implement the string in all supported language files:
     - `app/src/main/java/com/example/lang/english/EnglishStrings.kt` (en - English)
     - `app/src/main/java/com/example/lang/marathi/MarathiStrings.kt` (mr - मराठी)
     - `app/src/main/java/com/example/lang/hindi/HindiStrings.kt` (hi - हिंदी)
     - `app/src/main/java/com/example/lang/gujrati/GujratiStrings.kt` (gu - ગુજરાતી)

4. **UI Binding via `LangManager`**:
   - Access strings using `val s = LangManager.getStrings(currentLanguage)` and bind via `s.<propertyName>`.

5. **Dynamic/Backend Status & Toast Messages**:
   - For backend/server messages or dynamic state strings, add translation mapping cases in:
     - `localizeStatus(status: String)`
     - `localizeToast(message: String)`
     - `localizeActivity(text: String)`
