package com.example.ui

import com.example.ui.theme.ElintTheme
import com.example.ui.theme.ElintBackground
import com.example.ui.theme.ElintAmberIcon
import com.example.ui.theme.ElintAbsentIcon
import com.example.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.data.model.HomeRequest
import com.example.lang.AppStrings
import com.example.lang.LangManager
import com.example.ui.theme.PlusJakartaSans
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val Amber = ElintAmberIcon
private val Red = ElintAbsentIcon

private fun statusColor(status: String): Color = when (status) {
    "approved" -> Color(0xFF166534)
    "rejected" -> Red
    "pending" -> Amber
    else -> HomeMuted
}

private fun statusLabel(status: String, s: AppStrings): String = when (status) {
    "approved" -> s.reqApprovedLabel
    "rejected" -> s.reqRejectedLabel
    "pending" -> s.reqPendingLabel
    else -> s.reqSentLabel
}

private fun kindLabel(kind: String, s: AppStrings): String = when (kind) {
    "late" -> s.qaRunningLate
    "wfh" -> s.qaWfh
    "cant" -> s.qaCantCome
    "leave" -> s.qaTakeLeave
    "manual" -> s.nonBioConfirm
    else -> s.reqCorrection
}

private fun leaveName(code: String, s: AppStrings): String = when (code) {
    "casual" -> s.qaCasual
    "sick" -> s.qaSick
    else -> s.qaEarned
}

private fun dayLabel(date: String): String = try {
    val d = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(date)!!
    SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(d)
} catch (_: Exception) {
    date
}

private fun monthLabel(month: String): String = try {
    val d = SimpleDateFormat("yyyy-MM", Locale.US).parse(month)!!
    SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(d)
} catch (_: Exception) {
    month
}

private fun shiftMonth(month: String, delta: Int): String {
    val cal = Calendar.getInstance()
    try {
        cal.time = SimpleDateFormat("yyyy-MM", Locale.US).parse(month)!!
    } catch (_: Exception) {
    }
    cal.add(Calendar.MONTH, delta)
    return SimpleDateFormat("yyyy-MM", Locale.US).format(cal.time)
}

@Composable
private fun ScreenFrame(title: String, content: @Composable () -> Unit) {
    ProvideTextStyle(
        LocalTextStyle.current.copy(
            lineHeight = 1.2.em,
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ElintBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

private val Line = Color(0xFFEFEDE7)
private val Purple = Color(0xFF7C3AED)
private val Blue = Color(0xFF2F7CF6)

@Composable
private fun Card(title: String? = null, trailing: String? = null, content: @Composable () -> Unit) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (title != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = HomeInk, modifier = Modifier.weight(1f))
                    if (trailing != null) Text(trailing, fontFamily = PlusJakartaSans, fontSize = 12.sp, color = HomeMuted)
                }
            }
            content()
        }
    }
}

@Composable
private fun HLine() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
}

@Composable
private fun Pill(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.12f)) {
        Text(text, fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = color, modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp))
    }
}

@Composable
private fun SummaryCell(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, tint: Color, modifier: Modifier) {
    Column(modifier.padding(vertical = 14.dp, horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(Modifier.size(34.dp).clip(CircleShape).background(tint.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(19.dp))
        }
        Text(value, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = HomeInk, maxLines = 1)
        Text(label, fontFamily = PlusJakartaSans, fontSize = 11.sp, color = HomeMuted, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 13.sp)
    }
}

@Composable
private fun LeaveCell(name: String, left: String, all: String, fraction: Float, tint: Color, modifier: Modifier) {
    Column(modifier.padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(name, fontFamily = PlusJakartaSans, fontSize = 12.sp, color = HomeMuted)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(left, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = HomeInk)
            Text(" / $all", fontFamily = PlusJakartaSans, fontSize = 13.sp, color = HomeMuted, modifier = Modifier.padding(bottom = 3.dp))
        }
        Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)).background(tint.copy(alpha = 0.15f))) {
            Box(Modifier.fillMaxWidth(fraction.coerceIn(0f, 1f)).height(6.dp).clip(RoundedCornerShape(50)).background(tint))
        }
    }
}

@Composable
private fun RoundNav(forward: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.size(40.dp).clip(CircleShape).background(Color.White).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(if (forward) Icons.AutoMirrored.Filled.ArrowForward else Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = HomeInk, modifier = Modifier.size(20.dp))
    }
}

/**
 * Records tab: a month at a glance (one summary card), leave balance, the person's own requests and the
 * day-by-day list, all in the same white-card system.
 */
@Composable
fun ElintRecordsScreen(uiState: AttendanceUiState, viewModel: AttendanceViewModel, language: String) = ElintTheme {
    val s = LangManager.getStrings(language)
    var month by remember { mutableStateOf(SimpleDateFormat("yyyy-MM", Locale.US).format(java.util.Date())) }
    LaunchedEffect(month, uiState.deviceToken) { viewModel.loadRecords(month) }
    val data = uiState.records?.takeIf { it.month == month }

    ScreenFrame(s.recordsTitle) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            RoundNav(false) { month = shiftMonth(month, -1) }
            Text(
                monthLabel(month), fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = HomeInk,
                modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            RoundNav(true) { month = shiftMonth(month, 1) }
        }

        if (data == null) {
            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                if (uiState.isRecordsLoading) CircularProgressIndicator(color = HomeGreen)
            }
            return@ScreenFrame
        }

        data.missedPunch.orEmpty().forEach { mp ->
            val shiftEnd = data.shift?.end.orEmpty()
            if (shiftEnd.isNotBlank()) MissedPunchCard(s, mp.date, shiftEnd) { viewModel.sendMissedPunchFix(mp.attendanceId, shiftEnd, s.missedPunchSent) }
        }

        // 1. Month summary: one card, four cells.
        val t = data.totals
        Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
                SummaryCell(Icons.Default.CalendarMonth, "${t?.daysPresent ?: 0}", s.daysPresentLabel, HomeGreen, Modifier.weight(1f))
                Box(Modifier.width(1.dp).fillMaxSize().background(Line))
                SummaryCell(Icons.Default.Schedule, workedText(t?.workedMinutes ?: 0), s.hoursWorkedLabel, Blue, Modifier.weight(1f))
                Box(Modifier.width(1.dp).fillMaxSize().background(Line))
                SummaryCell(Icons.Default.Schedule, workedText(t?.overtimeMinutes ?: 0), s.overtimeLabel, Purple, Modifier.weight(1f))
                Box(Modifier.width(1.dp).fillMaxSize().background(Line))
                SummaryCell(Icons.Default.Warning, "${t?.lateDays ?: 0}", s.lateDaysLabel, Amber, Modifier.weight(1f))
            }
        }

        // 2. Leave balance
        Card(title = s.leaveBalanceTitle) {
            Row(Modifier.fillMaxWidth().height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
                val tints = listOf(HomeGreen, Blue, Purple)
                val list = data.leaveBalance.orEmpty()
                list.forEachIndexed { idx, b ->
                    val left = if (b.remaining % 1.0 == 0.0) b.remaining.toInt().toString() else b.remaining.toString()
                    val all = if (b.allotted % 1.0 == 0.0) b.allotted.toInt().toString() else b.allotted.toString()
                    LeaveCell(leaveName(b.type, s), left, all, if (b.allotted > 0) (b.remaining / b.allotted).toFloat() else 0f, tints[idx % tints.size], Modifier.weight(1f))
                    if (idx < list.lastIndex) Box(Modifier.width(1.dp).fillMaxSize().background(Line))
                }
            }
        }

        // 3. Requests
        val reqs = data.requests.orEmpty().filter { it.status != "undone" }
        Card(title = s.myRequestsTitle, trailing = if (reqs.isNotEmpty()) "${reqs.size}" else null) {
            if (reqs.isEmpty()) {
                Text(s.noRequests, fontFamily = PlusJakartaSans, fontSize = 13.sp, color = HomeMuted)
            }
            reqs.take(15).forEachIndexed { i, r ->
                if (i > 0) HLine()
                RequestRow(r, s)
            }
        }

        // 4. Day by day
        val days = data.days.orEmpty()
        Card(title = s.recordsAttendance, trailing = if (days.isNotEmpty()) "${days.size}" else null) {
            if (days.isEmpty()) {
                Text(s.noRecords, fontFamily = PlusJakartaSans, fontSize = 13.sp, color = HomeMuted)
            }
            days.forEachIndexed { i, d ->
                if (i > 0) HLine()
                DayRow(d, s)
            }
        }
    }
}

@Composable
private fun DayRow(d: com.example.data.model.RecordDay, s: AppStrings) {
    val parsed = try { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(d.date) } catch (_: Exception) { null }
    val dayNum = parsed?.let { SimpleDateFormat("d", Locale.getDefault()).format(it) } ?: d.date.takeLast(2)
    val weekday = parsed?.let { SimpleDateFormat("EEE", Locale.getDefault()).format(it) } ?: ""
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Column(
            Modifier.width(46.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF3F7F5)).padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(dayNum, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = HomeInk)
            Text(weekday, fontFamily = PlusJakartaSans, fontSize = 10.sp, color = HomeMuted)
        }
        Column(Modifier.weight(1f)) {
            val range = clockOf(d.firstIn) + " – " + (if (d.lastOut != null) clockOf(d.lastOut) else if (d.missedCheckout) s.noCheckout else s.stillIn)
            Text(range, fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (d.missedCheckout) Red else HomeInk, maxLines = 1)
            if (d.location.isNotBlank()) {
                Text(d.location, fontFamily = PlusJakartaSans, fontSize = 12.sp, color = HomeMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(workedText(d.workedMinutes), fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = HomeInk)
            if (d.overtimeMinutes > 0) Pill("+" + workedText(d.overtimeMinutes), Purple)
            else if (d.late) Pill(s.lateChip, Amber)
        }
    }
}

@Composable
internal fun MissedPunchCard(s: AppStrings, date: String, suggestedTime: String, onSend: () -> Unit) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFFFF3D6), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(s.missedPunchTitle.replace("{date}", dayLabel(date)), fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = Amber)
            Text(s.missedPunchBody.replace("{time}", clockOfHm(suggestedTime)), fontFamily = PlusJakartaSans, fontSize = 13.sp, color = HomeInk)
            Surface(shape = RoundedCornerShape(50), color = HomeGreen, modifier = Modifier.clickable { onSend() }) {
                Text(
                    s.missedPunchYes, fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                )
            }
        }
    }
}

/** "18:30" -> "6:30 PM". */
internal fun clockOfHm(hm: String): String = try {
    SimpleDateFormat("h:mm a", Locale.getDefault()).format(SimpleDateFormat("HH:mm", Locale.US).parse(hm)!!)
} catch (_: Exception) {
    hm
}

@Composable
private fun RequestRow(r: HomeRequest, s: AppStrings) {
    val tint = when (r.kind) { "late" -> Amber; "wfh" -> Purple; "cant" -> Red; "leave" -> Blue; else -> HomeGreen }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Box(Modifier.size(38.dp).clip(CircleShape).background(tint.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            val icon = when (r.kind) { "late" -> Icons.Default.Schedule; "leave" -> Icons.Default.CalendarMonth; else -> Icons.Default.Description }
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(
                kindLabel(r.kind, s) + if (r.kind == "leave" && r.leaveType.isNotBlank()) " · ${leaveName(r.leaveType, s)}" else "",
                fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = HomeInk, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                if (r.fromDate == r.toDate) dayLabel(r.fromDate) else "${dayLabel(r.fromDate)} → ${dayLabel(r.toDate)}",
                fontFamily = PlusJakartaSans, fontSize = 12.sp, color = HomeMuted
            )
            if (r.managerNote.isNotBlank()) {
                Text("“${r.managerNote}”", fontFamily = PlusJakartaSans, fontSize = 12.sp, color = HomeMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Pill(statusLabel(r.status, s), statusColor(r.status))
    }
}

/**
 * My day tab: today's shift, the punches in order and the breaks between them.
 */
@Composable
fun ElintMyDayScreen(uiState: AttendanceUiState, viewModel: AttendanceViewModel, language: String) = ElintTheme {
    val s = LangManager.getStrings(language)
    LaunchedEffect(uiState.deviceToken) { viewModel.loadMyDay() }
    val d = uiState.myDay

    ScreenFrame(s.myDayTitle) {
        if (d == null) {
            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                if (uiState.isMyDayLoading) CircularProgressIndicator(color = HomeGreen)
            }
            return@ScreenFrame
        }
        Surface(shape = RoundedCornerShape(24.dp), color = HomeGreen, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(dayLabel(d.date), fontFamily = PlusJakartaSans, fontSize = 13.sp, color = Color(0xFFCFE8E0))
                Text("${s.shiftLabel} ${d.shift?.text.orEmpty()}", fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.White)
                val worked = d.today?.workedMinutes ?: 0
                Text("${s.workedLabel}: ${workedText(worked)}", fontFamily = PlusJakartaSans, fontSize = 14.sp, color = Color.White)
                d.shift?.end?.let { Text(s.shiftEnds.replace("{time}", clockOfHm(it)), fontFamily = PlusJakartaSans, fontSize = 12.sp, color = Color(0xFFCFE8E0)) }
            }
        }
        d.request?.let { r ->
            Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFFFF3D6), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(kindLabel(r.kind, s), fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Amber, modifier = Modifier.weight(1f))
                    Text(statusLabel(r.status, s), fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = statusColor(r.status))
                }
            }
        }
        Card {
            Text(s.punchesTitle, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = HomeInk)
            val punches = d.punches.orEmpty()
            if (punches.isEmpty()) {
                Text(s.noPunchesToday, fontFamily = PlusJakartaSans, fontSize = 13.sp, color = HomeMuted)
            }
            punches.forEach { p ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(if (p.open) Color(0xFF16A34A) else HomeMuted))
                    Column(Modifier.weight(1f)) {
                        Text(
                            clockOf(p.checkIn) + " – " + (if (p.open) s.stillIn else clockOf(p.checkOut)),
                            fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = HomeInk
                        )
                        if (p.location.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(painterResource(R.drawable.ic_location), contentDescription = null, tint = HomeMuted, modifier = Modifier.size(13.dp))
                                Text(p.location, fontFamily = PlusJakartaSans, fontSize = 12.sp, color = HomeMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
        val breaks = d.breaks.orEmpty()
        if (breaks.isNotEmpty()) {
            Card {
                Text(s.breaksTitle, fontFamily = PlusJakartaSans, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = HomeInk)
                breaks.forEach { b ->
                    Text(
                        "${clockOf(b.from)} – ${clockOf(b.to)} · ${b.minutes} min",
                        fontFamily = PlusJakartaSans, fontSize = 13.sp, color = HomeMuted
                    )
                }
            }
        }
    }
}
