package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.R

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold),
)

val Fraunces = FontFamily(
    Font(R.font.fraunces_medium, FontWeight.Medium),
    Font(R.font.fraunces_semibold, FontWeight.SemiBold),
)

/** Named text styles used on the home screen. Access via ElintTheme.text. */
object ElintText {
    /** Daily line over the background scene */
    val dailyLine = TextStyle(fontFamily = Fraunces, fontSize = 22.sp, lineHeight = 26.4.sp, fontWeight = FontWeight.Medium)
    /** Today's thought quote */
    val quote = TextStyle(fontFamily = Fraunces, fontSize = 16.sp, lineHeight = 21.6.sp, fontWeight = FontWeight.Medium)
    /** Name in greeting */
    val userName = TextStyle(fontFamily = PlusJakartaSans, fontSize = 21.sp, lineHeight = 26.sp, fontWeight = FontWeight.ExtraBold)
    /** Bottom sheet title */
    val sheetTitle = TextStyle(fontFamily = PlusJakartaSans, fontSize = 19.sp, lineHeight = 24.sp, fontWeight = FontWeight.ExtraBold)
    /** Check in text inside the round button */
    val checkInLabel = TextStyle(fontFamily = PlusJakartaSans, fontSize = 19.sp, lineHeight = 22.sp, fontWeight = FontWeight.ExtraBold)
    /** Primary sheet button (Notify my manager) */
    val buttonLarge = TextStyle(fontFamily = PlusJakartaSans, fontSize = 16.sp, lineHeight = 20.sp, fontWeight = FontWeight.ExtraBold)
    /** Streak numbers, card titles */
    val cardTitle = TextStyle(fontFamily = PlusJakartaSans, fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
    /** Announcement text, input text */
    val body = TextStyle(fontFamily = PlusJakartaSans, fontSize = 14.sp, lineHeight = 19.6.sp, fontWeight = FontWeight.SemiBold)
    /** Status, chips, links, section labels in sheet */
    val label = TextStyle(fontFamily = PlusJakartaSans, fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold)
    /** Dates, helper text, status line */
    val caption = TextStyle(fontFamily = PlusJakartaSans, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium)
    /** Quick-update tile labels */
    val tileLabel = TextStyle(fontFamily = PlusJakartaSans, fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold)
    /** TODAY'S THOUGHT, PUNE OFFICE · ANNOUNCEMENT */
    val overline = TextStyle(fontFamily = PlusJakartaSans, fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.06.em)
    /** Bottom nav labels (700 when active) */
    val navLabel = TextStyle(fontFamily = PlusJakartaSans, fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium)
    /** Tap and look at camera */
    val micro = TextStyle(fontFamily = PlusJakartaSans, fontSize = 10.sp, lineHeight = 13.sp, fontWeight = FontWeight.Medium)
}

/** Material 3 mapping so standard components pick up the brand fonts. */
// Getter, not an initialised val: building it inside the file's static init can run while ElintText is half-built and leave null styles.
val ElintTypography: Typography get() = Typography(
    displaySmall = ElintText.dailyLine,
    headlineSmall = ElintText.userName,
    titleLarge = ElintText.sheetTitle,
    titleMedium = ElintText.cardTitle,
    bodyLarge = ElintText.body,
    bodyMedium = ElintText.label,
    bodySmall = ElintText.caption,
    labelLarge = ElintText.label,
    labelMedium = ElintText.caption,
    labelSmall = ElintText.tileLabel,
)
