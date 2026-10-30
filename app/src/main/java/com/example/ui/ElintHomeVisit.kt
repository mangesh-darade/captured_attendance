package com.example.ui

import com.example.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HomeWrapup
import com.example.lang.AppStrings
import com.example.ui.theme.PlusJakartaSans
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale

private val ServerTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

/** "2026-10-30 09:41:00" -> "9:41 AM", or the input when it cannot be parsed. */
internal fun clockOf(serverTime: String?): String {
    if (serverTime.isNullOrBlank()) return ""
    return try {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(ServerTime.parse(serverTime)!!)
    } catch (_: Exception) {
        serverTime
    }
}

internal fun workedText(minutes: Int): String = "${minutes / 60}h ${String.format(Locale.US, "%02d", minutes % 60)}m"

/**
 * Checked in: the current site, how long the person has been there, and End visit (which opens the check-out scan).
 */
@Composable
internal fun VisitSection(s: AppStrings, place: String, firstIn: String?, current: com.example.data.model.VisitCurrent?, fallbackStartMs: Long?, outsideLine: String, onEnd: () -> Unit) {
    val startMillis = remember(firstIn, fallbackStartMs) {
        (try { ServerTime.parse(firstIn ?: "")?.time } catch (_: Exception) { null }) ?: fallbackStartMs
    }
    var seconds by remember(startMillis) {
        mutableStateOf(if (startMillis != null) ((System.currentTimeMillis() - startMillis) / 1000).toInt().coerceAtLeast(0) else 0)
    }
    LaunchedEffect(startMillis) {
        while (true) {
            delay(1000L)
            seconds = if (startMillis != null) ((System.currentTimeMillis() - startMillis) / 1000).toInt().coerceAtLeast(0) else seconds + 1
        }
    }
    val timer = String.format(Locale.US, "%02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60)
    Surface(shape = RoundedCornerShape(24.dp), color = HomeGreen, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    s.onVisit.replace("{time}", clockOf(firstIn).uppercase()),
                    fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 12.sp,
                    color = Color(0xFFCFE8E0), modifier = Modifier.weight(1f)
                )
                Text(timer, fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = HomeGreen, modifier = Modifier.size(28.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        (current?.name?.takeIf { it.isNotBlank() } ?: place).ifBlank { "—" }, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold,
                        fontSize = 19.sp, color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                    val sub = current?.address?.takeIf { it.isNotBlank() }?.let { "$it · ${s.recognizedSite}" } ?: if (place.isNotBlank()) s.recognizedSite else ""
                    if (sub.isNotBlank()) {
                        Text(sub, fontFamily = PlusJakartaSans, fontSize = 12.sp, color = Color(0xFFCFE8E0), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            if (current != null) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VisitStat(current.openOrders.toString(), s.openOrdersLbl, Modifier.weight(1f))
                    VisitStat(current.lastVisit.ifBlank { "—" }, s.lastVisitLbl, Modifier.weight(1f))
                    VisitStat(current.tasksDue.toString(), s.taskDueLbl, Modifier.weight(1f))
                }
            }
            if (outsideLine.isNotBlank()) {
                Text(outsideLine, fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFFFFE08A))
            }
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().height(50.dp).clickable { onEnd() }
            ) {
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Box(Modifier.size(14.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFDC2626)))
                    Spacer(Modifier.size(10.dp))
                    Text(s.endVisit, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = HomeInk)
                }
            }
        }
    }
}

@Composable
private fun VisitStat(value: String, label: String, modifier: Modifier) {
    Column(
        modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.14f)).padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(value, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White, maxLines = 1)
        Text(label, fontFamily = PlusJakartaSans, fontSize = 11.sp, color = Color(0xFFCFE8E0), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Shown after check-out: real worked time and punches, an optional note and mood saved on the server.
 */
@Composable
internal fun DayCompleteContent(
    s: AppStrings,
    name: String,
    checkOutTime: String,
    place: String,
    workedMinutes: Int,
    visits: Int,
    outsideLine: String,
    saved: HomeWrapup?,
    onSave: (note: String, mood: Int) -> Unit
) {
    var note by remember(saved?.note) { mutableStateOf(saved?.note.orEmpty()) }
    var mood by remember(saved?.mood) { mutableStateOf((saved?.mood ?: 0) - 1) }
    val moods = listOf(
        s.moodRough to Color(0xFFDC2626), s.moodMeh to Color(0xFFEA580C), s.moodOkay to HomeMuted,
        s.moodGood to HomeGreen, s.moodGreat to Color(0xFF16A34A)
    )
    Column(
        Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(Modifier.size(60.dp).clip(CircleShape).background(HomeGreen), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
        }
        Text(
            "${s.dayComplete} ${name.trim().substringBefore(" ").ifBlank { "User" }}!",
            fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp,
            color = HomeInk, textAlign = TextAlign.Center
        )
        Text(
            listOf(s.checkedOutAt.replace("{time}", checkOutTime), place).filter { it.isNotBlank() }.joinToString(" · "),
            fontFamily = PlusJakartaSans, fontSize = 12.sp, color = HomeMuted, textAlign = TextAlign.Center
        )
    }
    Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_clock), contentDescription = null, tint = HomeGreen, modifier = Modifier.size(26.dp))
                Spacer(Modifier.size(8.dp))
                Column {
                    Text(workedText(workedMinutes), fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, color = HomeInk)
                    Text(s.workedLabel, fontFamily = PlusJakartaSans, fontSize = 12.sp, color = HomeMuted)
                }
            }
            Box(Modifier.width(1.dp).height(36.dp).background(Color(0xFFE5E7EB)))
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Groups, contentDescription = null, tint = HomeGreen, modifier = Modifier.size(26.dp))
                Spacer(Modifier.size(8.dp))
                Column {
                    Text("$visits", fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, color = HomeInk)
                    Text(s.clientVisitsLabel, fontFamily = PlusJakartaSans, fontSize = 12.sp, color = HomeMuted)
                }
            }
        }
    }
    if (outsideLine.isNotBlank()) {
        Text(outsideLine, fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = HomeMuted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    }
    Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(s.notesForToday, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = HomeInk)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text(s.notesForTodayHint, fontFamily = PlusJakartaSans, fontSize = 13.sp) },
                minLines = 2,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HomeGreen, unfocusedBorderColor = Color(0xFFCBD5E1)),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(s.howWasDay, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = HomeInk)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                moods.forEachIndexed { i, (label, color) ->
                    val picked = mood == i
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (picked) Color(0xFFE8F4EF) else Color(0xFFF3F4F6))
                            .border(if (picked) 2.dp else 0.dp, if (picked) HomeGreen else Color.Transparent, RoundedCornerShape(14.dp))
                            .clickable { mood = i }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(Icons.Default.SentimentSatisfied, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                        Text(label, fontFamily = PlusJakartaSans, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = HomeInk)
                    }
                }
            }
            Text(s.moodHint, fontFamily = PlusJakartaSans, fontSize = 11.sp, color = HomeMuted)
        }
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = HomeGreen,
        modifier = Modifier.fillMaxWidth().height(48.dp).clickable { onSave(note.trim(), mood + 1) }
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(s.saveLabel, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
        }
    }
}
