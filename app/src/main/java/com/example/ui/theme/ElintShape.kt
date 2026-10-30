package com.example.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object ElintShapes {
    val checkInCard = RoundedCornerShape(24.dp)   // check-in card
    val card = RoundedCornerShape(20.dp)          // announcement, quote cards
    val smallCard = RoundedCornerShape(18.dp)     // streak cards
    val tile = RoundedCornerShape(16.dp)          // quick-update tiles, sheet option cards
    val statusLine = RoundedCornerShape(12.dp)    // sent-request status line, WFH info box
    val sheet = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp) // bottom sheet
    val pill = CircleShape                        // chips, location pill, buttons, avatar
}

val ElintMaterialShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)
