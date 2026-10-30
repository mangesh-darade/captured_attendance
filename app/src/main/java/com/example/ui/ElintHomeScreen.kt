package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.HomeAnnouncement
import com.example.data.model.HomeRequest
import com.example.lang.AppStrings
import com.example.lang.LangManager
import com.example.ui.theme.ElintBackground
import com.example.ui.theme.ElintBrandPrimary
import com.example.ui.theme.ElintBrandPrimaryPressed
import com.example.ui.theme.ElintDimens
import com.example.ui.theme.ElintDivider
import com.example.ui.theme.ElintOutline
import com.example.ui.theme.ElintScrim
import com.example.ui.theme.ElintShapes
import com.example.ui.theme.ElintSurface
import com.example.ui.theme.ElintSurfaceSubtle
import com.example.ui.theme.ElintText
import com.example.ui.theme.ElintTextMuted
import com.example.ui.theme.ElintTextPrimary
import com.example.ui.theme.ElintTextSecondary
import com.example.ui.theme.ElintTheme
import com.example.ui.theme.PlusJakartaSans
import com.example.ui.theme.QuickActionColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Shared by the other ElintOmAT screens (Records, My day, bottom bar, visit card).
internal val HomeGreen = ElintBrandPrimary
internal val HomeInk = ElintTextPrimary
internal val HomeMuted = ElintTextMuted

private enum class QuickKind(val code: String) { LATE("late"), WFH("wfh"), CANT("cant"), LEAVE("leave") }

/**
 * Home tab of the ElintOmAT companion: greeting, quick day updates, the big Check in button,
 * announcements, daily thought and weekly progress. All data comes from attendance_api/home_summary.
 * Check in / End visit hand over to the face-scan tab.
 */
@Composable
fun ElintHomeScreen(
    uiState: AttendanceUiState,
    viewModel: AttendanceViewModel,
    currentLanguage: String,
    onCheckIn: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElintTheme {
        HomeContent(uiState, viewModel, currentLanguage, onCheckIn, onOpenHistory, onOpenProfile, modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    uiState: AttendanceUiState,
    viewModel: AttendanceViewModel,
    currentLanguage: String,
    onCheckIn: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier
) {
    val s = LangManager.getStrings(currentLanguage)
    val context = LocalContext.current
    var sheetKind by remember { mutableStateOf<QuickKind?>(null) }
    var rejectedNote by remember { mutableStateOf<HomeRequest?>(null) }
    var showLocation by remember { mutableStateOf(false) }
    var showCollect by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.deviceToken) { viewModel.loadHome() }
    LaunchedEffect(Unit) { viewModel.triggerLocationAndDerive(forceFresh = true) }

    val home = uiState.home
    val today = home?.today
    val pendingSync = uiState.waitingToSend
    val serverState = today?.state ?: "not_in"
    val state = if (uiState.activeToggle == "OUT" && serverState == "not_in") "in" else serverState
    val checkedIn = state == "in"
    val dayPrefs = remember { context.getSharedPreferences("home_prefs", android.content.Context.MODE_PRIVATE) }
    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var dayWrapDone by remember { mutableStateOf(dayPrefs.getString("daywrap_saved_on", "") == todayKey) }
    // A new check-in starts a new day-complete cycle.
    LaunchedEffect(state) { if (state == "in" && dayWrapDone) { dayWrapDone = false; dayPrefs.edit().remove("daywrap_saved_on").apply() } }
    val dayDone = state == "out" && !dayWrapDone
    // Live position first (re-read each time Home opens); saved site / last punch only as fallback.
    val place = if (uiState.isLocationLoading) s.fetchingLocation
        else if (uiState.currentLat != null) uiState.currentLandmark.ifBlank { String.format(Locale.US, "%.5f, %.5f", uiState.currentLat, uiState.currentLng) }
        else uiState.selectedLocation?.pureName()?.takeIf { it.isNotBlank() }
        ?: today?.location?.takeIf { it.isNotBlank() }
        ?: uiState.tenantSiteName.orEmpty()
    val shiftText = home?.shift?.text.orEmpty()
    val outsideLine = if ((today?.outsideCount ?: 0) > 0)
        s.outsideLine.replace("{m}", workedText(today?.outsideMinutes ?: 0)).replace("{n}", (today?.outsideCount ?: 0).toString()) else ""
    val announcements = home?.announcements.orEmpty()
    val unread = home?.unreadCount ?: 0
    val name = home?.user?.name?.takeIf { it.isNotBlank() } ?: uiState.loggedInUserName

    ProvideTextStyle(
        LocalTextStyle.current.copy(
            fontFamily = PlusJakartaSans,
            lineHeight = 1.2.em,
            lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both)
        )
    ) {
        androidx.compose.foundation.layout.BoxWithConstraints(modifier = modifier.fillMaxSize().background(ElintBackground)) {
            val screenHeight = maxHeight
            // Morning scene behind the content, full width at the top.
            Image(
                painter = painterResource(R.drawable.bg_home_morning),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = screenHeight)
                    .padding(horizontal = ElintDimens.screenPaddingHorizontal, vertical = 8.dp),
                verticalArrangement = SpreadToFill
            ) {
                if (pendingSync) {
                    PendingNote(s.pendingSyncLabel)
                }
                if (dayDone) {
                    TrackingNote(s.trackingOff, s.trackingOn, on = false) { showCollect = true }
                    DayCompleteContent(
                        s = s,
                        name = name,
                        checkOutTime = clockOf(today?.lastOut),
                        place = place,
                        workedMinutes = today?.workedMinutes ?: 0,
                        visits = today?.visits ?: 0,
                        outsideLine = outsideLine,
                        saved = home?.wrapup,
                        onSave = { n, m ->
                            viewModel.saveDayWrapup(n, m, s.savedLabel)
                            dayPrefs.edit().putString("daywrap_saved_on", todayKey).apply()
                            dayWrapDone = true
                        }
                    )
                } else {
                    if (!checkedIn) {
                        Text(
                            formatNote(home?.hero?.takeIf { it.isNotBlank() } ?: s.heroLine),
                            style = ElintText.dailyLine,
                            color = ElintTheme.colors.headingSerif,
                            modifier = Modifier.fillMaxWidth(0.85f).padding(vertical = 4.dp)
                        )
                        QuickTiles(s) { sheetKind = it }
                    }
                    if (checkedIn) {
                        VisitSection(
                            s, today?.location?.takeIf { it.isNotBlank() } ?: uiState.selectedLocation?.pureName()?.takeIf { it.isNotBlank() } ?: place, today?.firstIn, uiState.visit?.current,
                            run {
                                val hp = context.getSharedPreferences("home_prefs", android.content.Context.MODE_PRIVATE)
                                val saved = hp.getLong("checkin_ms", 0L)
                                if (saved > 0 && System.currentTimeMillis() - saved < 24 * 3600 * 1000L) saved
                                else System.currentTimeMillis().also { hp.edit().putLong("checkin_ms", it).apply() }
                            },
                            outsideLine,
                            onCheckIn
                        )
                        VisitTools(s, uiState, viewModel)
                        uiState.visit?.let { PlanCard(s, it) }
                        TrackingNote(s.trackingOn, null, on = true) { showCollect = true }
                    } else {
                        CheckInCard(
                            s = s,
                            request = home?.request,
                            place = place,
                            recognized = uiState.selectedLocation != null,
                            shiftText = shiftText,
                            onUndo = { id -> viewModel.undoQuickUpdate(id) },
                            onDismiss = { id -> viewModel.undoQuickUpdate(id, dismiss = true) },
                            onViewRejected = { rejectedNote = it },
                            onCheckIn = onCheckIn,
                            onLocation = { viewModel.triggerLocationAndDerive(forceFresh = true); showLocation = true },
                            onMap = { openLiveMap(context, uiState.currentLat, uiState.currentLng) }
                        )
                    }
                    home?.missedPunch.orEmpty().firstOrNull()?.let { mp ->
                        val end = home?.shift?.end.orEmpty()
                        if (end.isNotBlank()) MissedPunchCard(s, mp.date, end) { viewModel.sendMissedPunchFix(mp.attendanceId, end, s.missedPunchSent) }
                    }
                    if (!checkedIn) {
                        AnnouncementPager(s, announcements, viewModel::acknowledgeAnnouncement)
                        home?.thought?.takeIf { it.isNotBlank() }?.let { quote ->
                            val prefs = remember { context.getSharedPreferences("home_prefs", android.content.Context.MODE_PRIVATE) }
                            var liked by remember(quote) { mutableStateOf(prefs.getStringSet("favourite_quotes", emptySet())?.contains(quote) == true) }
                            ThoughtCard(s, quote, liked) {
                                val set = (prefs.getStringSet("favourite_quotes", emptySet()) ?: emptySet()).toMutableSet()
                                if (liked) set.remove(quote) else set.add(quote)
                                prefs.edit().putStringSet("favourite_quotes", set).apply()
                                liked = !liked
                            }
                        }
                        home?.stats?.let { st ->
                            StatsRow(s, st.daysWeek, st.daysWeekTarget, st.streak, st.monthPct, onOpenHistory)
                        }
                    }
                }
            }
        }
    }

    sheetKind?.let { initial ->
        ModalBottomSheet(
            onDismissRequest = { sheetKind = null; viewModel.clearQuickUpdateError() },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ElintSurface,
            shape = ElintShapes.sheet,
            scrimColor = ElintScrim
        ) {
            ElintTheme { UpdateDaySheet(s, initial, uiState, viewModel) { sheetKind = null } }
        }
    }

    if (uiState.showAnnouncements) {
        ModalBottomSheet(
            onDismissRequest = viewModel::closeAnnouncements,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ElintSurface,
            shape = ElintShapes.sheet,
            scrimColor = ElintScrim
        ) {
            ElintTheme { AnnouncementList(s, announcements, viewModel::acknowledgeAnnouncement) }
        }
    }

    if (showCollect) {
        AlertDialog(
            onDismissRequest = { showCollect = false },
            containerColor = ElintSurface,
            confirmButton = { TextButton(onClick = { showCollect = false }) { Text("OK", color = HomeGreen, fontFamily = PlusJakartaSans) } },
            title = { Text(s.collectTitle, style = ElintText.sheetTitle, color = HomeInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                    listOf(
                        s.collectLocationTitle to s.collectLocationBody,
                        s.collectFaceTitle to s.collectFaceBody,
                        s.collectRecordsTitle to s.collectRecordsBody
                    ).forEach { (head, body) ->
                        Column {
                            Text(head, style = ElintText.cardTitle, color = HomeInk)
                            Text(body, style = ElintText.label, color = HomeMuted)
                        }
                    }
                }
            }
        )
    }

    if (showLocation) {
        val acc = uiState.currentAccuracy?.toInt()
        AlertDialog(
            onDismissRequest = { showLocation = false },
            containerColor = ElintSurface,
            confirmButton = { TextButton(onClick = { showLocation = false }) { Text("OK", color = HomeGreen, fontFamily = PlusJakartaSans) } },
            title = { Text(s.locDetailTitle, style = ElintText.sheetTitle, color = HomeInk) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(place.ifBlank { "—" }, style = ElintText.cardTitle, color = HomeInk)
                    if (uiState.currentLat != null && uiState.currentLng != null) {
                        Text(String.format(Locale.US, "%.5f, %.5f", uiState.currentLat, uiState.currentLng), style = ElintText.caption, color = HomeMuted)
                    }
                    if (acc != null) Text("${s.accuracyLabel}: ±${acc}m", style = ElintText.caption, color = HomeMuted)
                    Text(
                        if (uiState.selectedLocation != null) s.locRecognizedInfo else s.locUnknownInfo,
                        style = ElintText.label, color = HomeInk
                    )
                }
            }
        )
    }

    rejectedNote?.let { req ->
        AlertDialog(
            onDismissRequest = { rejectedNote = null },
            containerColor = ElintSurface,
            confirmButton = {
                TextButton(onClick = {
                    viewModel.undoQuickUpdate(req.id, dismiss = true)
                    rejectedNote = null
                }) { Text("OK", color = HomeGreen, fontFamily = PlusJakartaSans) }
            },
            title = { Text(rejectedTitle(req, s), style = ElintText.sheetTitle, color = HomeInk) },
            text = { Text(req.managerNote.ifBlank { "—" }, style = ElintText.body, color = HomeInk) }
        )
    }
}

private fun rejectedTitle(req: HomeRequest, s: AppStrings) = if (req.kind == "leave") s.qaRejectedLeave else s.qaRejectedWfh

@Composable
internal fun GreetingRow(
    name: String,
    s: AppStrings,
    unreadCount: Int,
    photoUrl: String?,
    onOpenProfile: () -> Unit,
    onCalendar: () -> Unit,
    onBell: () -> Unit,
    greetingOverride: String? = null,
    statusLine: String? = null,
    titleOverride: String? = null
) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = greetingOverride ?: when {
        hour < 12 -> s.greetMorning
        hour < 17 -> s.greetAfternoon
        else -> s.greetEvening
    }
    val date = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).format(Date())
    val initials = name.trim().split(" ").filter { it.isNotBlank() }
        .take(2).joinToString("") { it.first().uppercase() }.ifBlank { "U" }
    Surface(shape = androidx.compose.ui.graphics.RectangleShape, color = ElintSurface, shadowElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
    Row(
        modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(ElintDimens.avatarSize)
                .clip(CircleShape)
                .background(ElintSurface)
                .border(ElintDimens.avatarRing, ElintBrandPrimary, CircleShape)
                .clickable { onOpenProfile() },
            contentAlignment = Alignment.Center
        ) {
            if (!photoUrl.isNullOrBlank()) {
                AsyncImage(model = photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape))
            } else {
                Text(initials, style = ElintText.cardTitle, color = ElintBrandPrimary)
            }
        }
        Column(Modifier.weight(1f)) {
            if (statusLine != null) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(ElintBrandPrimary))
                    Text(statusLine, style = ElintText.caption.copy(fontWeight = FontWeight.SemiBold), color = ElintBrandPrimary)
                }
                Text(titleOverride ?: name.ifBlank { "User" }, style = ElintText.userName, color = HomeInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
            } else {
                Text(greeting, style = ElintText.caption, color = ElintTheme.colors.textSecondary)
                Text(name.ifBlank { "User" }, style = ElintText.userName, color = HomeInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(date, style = ElintText.caption, color = HomeMuted)
            }
        }
        RoundIconButton(painterResource(R.drawable.ic_bell), onBell, count = unreadCount)
        RoundIconButton(painterResource(R.drawable.ic_calendar), onCalendar)
    }
    }
}

@Composable
private fun RoundIconButton(icon: Painter, onClick: () -> Unit, count: Int = 0) {
    // The badge sits on the corner of the round button, outside its clip, with a white ring.
    Box(Modifier.size(ElintDimens.iconButtonSize)) {
        Box(
            Modifier.matchParentSize().clip(CircleShape).background(ElintSurface).clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = HomeInk, modifier = Modifier.size(ElintDimens.iconMedium))
        }
        if (count > 0) {
            Box(
                Modifier.align(Alignment.TopEnd)
                    .offset(x = 3.dp, y = (-3).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(ElintSurface)
                    .padding(1.5.dp)
                    .clip(CircleShape)
                    .background(ElintTheme.colors.unreadDot),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (count > 9) "9+" else count.toString(),
                    fontFamily = PlusJakartaSans, fontWeight = FontWeight.Bold, fontSize = 9.sp, lineHeight = 9.sp, color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun QuickTiles(s: AppStrings, onTile: (QuickKind) -> Unit) {
    val c = ElintTheme.colors
    Row(horizontalArrangement = Arrangement.spacedBy(ElintDimens.quickTileGap), modifier = Modifier.fillMaxWidth()) {
        QuickTile(painterResource(R.drawable.ic_clock), s.qaRunningLate, c.runningLate, Modifier.weight(1f)) { onTile(QuickKind.LATE) }
        QuickTile(painterResource(R.drawable.ic_home), s.qaWfh, c.workFromHome, Modifier.weight(1f)) { onTile(QuickKind.WFH) }
        QuickTile(painterResource(R.drawable.ic_x_circle), s.qaCantCome, c.cantCome, Modifier.weight(1f)) { onTile(QuickKind.CANT) }
        QuickTile(painterResource(R.drawable.ic_calendar), s.qaTakeLeave, c.takeLeave, Modifier.weight(1f)) { onTile(QuickKind.LEAVE) }
    }
}

@Composable
private fun QuickTile(icon: Painter, label: String, colors: QuickActionColors, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        shape = ElintShapes.tile,
        color = ElintSurface.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, colors.border),
        modifier = modifier.heightIn(min = ElintDimens.quickTileHeight).clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = colors.icon, modifier = Modifier.size(ElintDimens.iconMedium))
            Spacer(Modifier.height(3.dp))
            Text(label, style = ElintText.tileLabel, color = colors.label, textAlign = TextAlign.Center, maxLines = 2, minLines = 2)
        }
    }
}

private fun reasonLabel(code: String, s: AppStrings): String = when (code) {
    "traffic" -> s.qaReasonTraffic
    "personal" -> s.qaReasonPersonal
    "client" -> s.qaReasonClient
    "unwell" -> s.qaUnwell
    "family" -> s.qaFamily
    "travel" -> s.qaTravel
    else -> s.qaReasonOther
}

private fun leaveLabel(code: String, s: AppStrings): String = when (code) {
    "casual" -> s.qaCasual
    "sick" -> s.qaSick
    else -> s.qaEarned
}

private fun durationLabel(code: String, s: AppStrings): String = when (code) {
    "first_half" -> s.qaFirstHalf
    "second_half" -> s.qaSecondHalf
    else -> s.qaFullDay
}

private fun whenLabel(req: HomeRequest, s: AppStrings): String = when (req.`when`) {
    "today" -> s.qaToday
    "tomorrow" -> s.qaTomorrow
    else -> if (req.fromDate == req.toDate) req.fromDate else "${req.fromDate} → ${req.toDate}"
}

/** The status line shown on the Check in card after a quick update. */
private fun requestText(req: HomeRequest, s: AppStrings): String = when (req.status) {
    "approved" -> if (req.kind == "leave") s.qaApprovedLeave.replace("{when}", whenLabel(req, s)) else s.qaApprovedWfh
    "rejected" -> rejectedTitle(req, s)
    else -> when (req.kind) {
        "late" -> s.qaStatusLate.replace("{time}", req.expectedArrival)
        "wfh" -> s.qaStatusWfh.replace("{when}", whenLabel(req, s)).replace("{duration}", durationLabel(req.duration, s))
        "cant" -> s.qaStatusCant.replace("{reason}", reasonLabel(req.reason, s))
        else -> s.qaStatusLeave.replace("{type}", leaveLabel(req.leaveType, s)).replace("{when}", whenLabel(req, s))
    }
}

@Composable
private fun CheckInCard(
    s: AppStrings,
    request: HomeRequest?,
    place: String,
    recognized: Boolean,
    shiftText: String,
    onUndo: (Int) -> Unit,
    onDismiss: (Int) -> Unit,
    onViewRejected: (HomeRequest) -> Unit,
    onCheckIn: () -> Unit,
    onLocation: () -> Unit,
    onMap: () -> Unit
) {
    val c = ElintTheme.colors
    Surface(shape = ElintShapes.checkInCard, color = ElintSurface.copy(alpha = 0.94f), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (request != null) {
                val bg: Color
                val fg: Color
                when (request.status) {
                    "approved" -> { bg = Color(0xFFDCF5E6); fg = Color(0xFF166534) }
                    "rejected" -> { bg = Color(0xFFFDE2E2); fg = Color(0xFF9F1D1D) }
                    else -> { bg = c.statusLineSurface; fg = c.statusLineText }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().clip(ElintShapes.statusLine).background(bg).padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(requestText(request, s), style = ElintText.caption.copy(fontWeight = FontWeight.SemiBold), color = fg, modifier = Modifier.weight(1f))
                    when (request.status) {
                        "sent", "pending" -> Text(
                            s.qaUndo, style = ElintText.label.copy(fontWeight = FontWeight.Bold), color = ElintBrandPrimary,
                            modifier = Modifier.clickable { onUndo(request.id) }.padding(start = 10.dp)
                        )
                        "rejected" -> Text(
                            s.qaView, style = ElintText.label.copy(fontWeight = FontWeight.Bold), color = fg,
                            modifier = Modifier.clickable { onViewRejected(request) }.padding(start = 10.dp)
                        )
                        else -> Icon(
                            painterResource(R.drawable.ic_close), contentDescription = null, tint = fg,
                            modifier = Modifier.padding(start = 10.dp).size(ElintDimens.iconSmall).clickable { onDismiss(request.id) }
                        )
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.size(9.dp).clip(CircleShape).background(c.neutralDot))
                    Column {
                        Text(s.notCheckedInStatus, style = ElintText.cardTitle, color = HomeInk)
                        if (shiftText.isNotBlank()) {
                            Text("${s.shiftLabel} $shiftText", style = ElintText.caption, color = HomeMuted)
                        }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                // Check-in button: primary circle, light ring, 1dp outer edge.
                Box(
                    modifier = Modifier
                        .size(ElintDimens.checkinButtonSize)
                        .border(1.dp, c.checkInRingEdge, CircleShape)
                        .clip(CircleShape)
                        .background(c.checkInRing)
                        .padding(ElintDimens.checkinButtonRing)
                        .clip(CircleShape)
                        .background(ElintBrandPrimary)
                        .clickable { onCheckIn() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(painterResource(R.drawable.ic_face_scan), contentDescription = null, tint = Color.White, modifier = Modifier.size(ElintDimens.iconCheckin))
                        Text(s.checkInBig, style = ElintText.checkInLabel, color = Color.White)
                        Text(
                            s.tapAndLook, style = ElintText.micro, color = c.checkInRing,
                            textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .heightIn(min = ElintDimens.locationPillHeight)
                            .clip(ElintShapes.pill)
                            .background(c.locationPill)
                            .clickable { onLocation() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(painterResource(R.drawable.ic_location), contentDescription = null, tint = ElintBrandPrimary, modifier = Modifier.size(ElintDimens.iconSmall))
                        Text(place.ifBlank { "—" }, style = ElintText.label, color = HomeInk, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    if (recognized) {
                        Row(
                            modifier = Modifier.clip(ElintShapes.pill).background(ElintBrandPrimary.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(painterResource(R.drawable.ic_check), contentDescription = null, tint = ElintBrandPrimary, modifier = Modifier.size(14.dp))
                            Text(s.recognizedLocation, style = ElintText.caption.copy(fontWeight = FontWeight.SemiBold), color = ElintBrandPrimary)
                        }
                    }
                    Text(
                        s.viewOnMap, style = ElintText.label, color = ElintBrandPrimary,
                        modifier = Modifier.clickable { onMap() }.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnnouncementPager(s: AppStrings, all: List<HomeAnnouncement>, onAck: (Int) -> Unit) {
    val c = ElintTheme.colors
    // Unacknowledged notices first so what needs attention is on top.
    val items = remember(all) { all.sortedBy { it.acknowledged || !it.needsAck } }
    if (items.isEmpty()) return
    var index by remember(items.size) { mutableStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var overflows by remember { mutableStateOf(false) }
    val a = items[index.coerceIn(0, items.lastIndex)]
    Surface(
        shape = ElintShapes.card,
        color = c.announcementSurface,
        border = BorderStroke(1.dp, c.announcementBorder),
        modifier = Modifier.fillMaxWidth().pointerInput(items.size) {
            var drag = 0f
            detectHorizontalDragGestures(
                onDragStart = { drag = 0f },
                onHorizontalDrag = { _, delta -> drag += delta },
                onDragEnd = {
                    if (items.size > 1 && kotlin.math.abs(drag) > 80f) {
                        index = if (drag < 0) (index + 1) % items.size else (index - 1 + items.size) % items.size
                        expanded = false
                    }
                }
            )
        }
    ) {
        Column(Modifier.heightIn(min = 104.dp).padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(painterResource(R.drawable.ic_megaphone), contentDescription = null, tint = c.announcementText, modifier = Modifier.size(ElintDimens.iconMedium))
                Text(
                    (listOf(a.tag.uppercase()).filter { it.isNotBlank() } + s.announceTag).joinToString(" · "),
                    style = ElintText.overline, color = c.announcementText,
                    modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                if (items.size > 1) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = c.announcementText,
                        modifier = Modifier.size(20.dp).clickable { index = (index - 1 + items.size) % items.size; expanded = false }
                    )
                    Text("${index + 1}/${items.size}", style = ElintText.caption, color = HomeMuted)
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = c.announcementText,
                        modifier = Modifier.size(20.dp).clickable { index = (index + 1) % items.size; expanded = false }
                    )
                }
            }
            Text(a.title, style = ElintText.cardTitle, color = c.announcementText)
            if (a.body.isNotBlank()) {
                Text(
                    formatNote(a.body), style = ElintText.caption, color = HomeMuted,
                    maxLines = if (expanded) Int.MAX_VALUE else 2, overflow = TextOverflow.Ellipsis,
                    onTextLayout = { r -> if (!expanded) overflows = r.hasVisualOverflow },
                    modifier = Modifier.clickable(enabled = overflows || expanded) { expanded = !expanded }
                )
            }
            val showMore = overflows && !expanded
            val showAck = a.needsAck && !a.acknowledged
            if (showMore || showAck) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (showMore) Text(
                    s.readMore, style = ElintText.label.copy(fontWeight = FontWeight.Bold), color = c.announcementText,
                    modifier = Modifier.clickable { expanded = true }.padding(vertical = 4.dp)
                )
                Spacer(Modifier.weight(1f))
                if (a.needsAck && !a.acknowledged) {
                    Surface(
                        shape = ElintShapes.pill, color = ElintSurface, border = BorderStroke(1.dp, c.announcementText),
                        modifier = Modifier.clickable { onAck(a.id) }
                    ) {
                        Text(s.acknowledge, style = ElintText.label.copy(fontWeight = FontWeight.Bold), color = c.announcementText, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnouncementList(s: AppStrings, items: List<HomeAnnouncement>, onAck: (Int) -> Unit) {
    val c = ElintTheme.colors
    Column(
        Modifier.fillMaxWidth().navigationBarsPadding().verticalScroll(rememberScrollState())
            .padding(horizontal = ElintDimens.sheetPaddingHorizontal).padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(s.announcementsTitle, style = ElintText.sheetTitle, color = HomeInk)
        if (items.isEmpty()) {
            Text(s.noAnnouncements, style = ElintText.body, color = HomeMuted)
        }
        items.forEach { a ->
            Surface(shape = ElintShapes.tile, color = c.announcementSurface, border = BorderStroke(1.dp, c.announcementBorder), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (a.tag.isNotBlank()) {
                        Text(a.tag.uppercase(), style = ElintText.overline, color = c.announcementText)
                    }
                    Text(a.title, style = ElintText.cardTitle, color = c.announcementText)
                    if (a.body.isNotBlank()) {
                        Text(formatNote(a.body), style = ElintText.label, color = HomeMuted)
                    }
                    if (a.needsAck && !a.acknowledged) {
                        Surface(shape = ElintShapes.pill, color = ElintSurface, border = BorderStroke(1.dp, c.announcementText), modifier = Modifier.clickable { onAck(a.id) }) {
                            Text(s.acknowledge, style = ElintText.label.copy(fontWeight = FontWeight.Bold), color = c.announcementText, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThoughtCard(s: AppStrings, quote: String, liked: Boolean, onLike: () -> Unit) {
    val c = ElintTheme.colors
    Surface(shape = ElintShapes.card, color = c.quoteSurface, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.heightIn(min = 84.dp).padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(s.thoughtTitle, style = ElintText.overline, color = ElintBrandPrimary)
                Text(androidx.compose.ui.text.buildAnnotatedString { append("“"); append(formatNote(quote)); append("”") }, style = ElintText.quote.copy(fontSize = 15.sp, lineHeight = 20.sp), color = c.headingSerif, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.size(34.dp).clip(CircleShape).background(ElintSurface).clickable { onLike() }, contentAlignment = Alignment.Center) {
                    if (liked) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = ElintBrandPrimary, modifier = Modifier.size(18.dp))
                    } else {
                        Icon(painterResource(R.drawable.ic_heart), contentDescription = null, tint = ElintBrandPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsRow(s: AppStrings, daysWeek: Int, target: Int, streak: Int, monthPct: Int, onOpen: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        StatCard(null, "$daysWeek of $target", s.daysThisWeek, ElintBrandPrimary, Modifier.weight(1f), onOpen, ring = if (target > 0) daysWeek / target.toFloat() else 0f)
        StatCard(painterResource(R.drawable.ic_flame), "$streak", s.onTimeStreak, ElintTheme.colors.runningLate.icon, Modifier.weight(1f), onOpen)
        StatCard(painterResource(R.drawable.ic_records), "$monthPct%", s.thisMonth, ElintBrandPrimary, Modifier.weight(1f), onOpen)
    }
}

@Composable
private fun StatCard(icon: Painter?, value: String, label: String, tint: Color, modifier: Modifier, onClick: () -> Unit, ring: Float? = null) {
    Surface(shape = ElintShapes.smallCard, color = ElintSurface, modifier = modifier.clickable { onClick() }) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            if (ring != null) {
                CircularProgressIndicator(
                    progress = { ring.coerceIn(0f, 1f) }, color = tint, trackColor = ElintDivider,
                    strokeWidth = 3.dp, modifier = Modifier.size(18.dp)
                )
            } else if (icon != null) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Text(value, style = ElintText.cardTitle, color = HomeInk)
            Text(label, style = ElintText.caption, color = HomeMuted)
        }
    }
}

/** Guardrail: shows that location is only tracked between check-in and check-out; tap for what is collected. */
@Composable
private fun TrackingNote(title: String, detail: String?, on: Boolean, onClick: () -> Unit) {
    val c = ElintTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ElintShapes.statusLine)
            .background(if (on) ElintBrandPrimary.copy(alpha = 0.1f) else ElintSurfaceSubtle)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(if (on) c.success else c.neutralDot))
        Text(
            if (on) title else listOfNotNull(title, detail).joinToString(" · "),
            style = ElintText.caption.copy(fontWeight = FontWeight.SemiBold),
            color = if (on) ElintBrandPrimaryPressed else HomeMuted
        )
    }
}

private val DateCode = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }

/**
 * The shared "Update your day" sheet. [initial] preselects the option; switching keeps the typed note.
 * Sends the update to the server and closes on success.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun UpdateDaySheet(s: AppStrings, initial: QuickKind, uiState: AttendanceUiState, viewModel: AttendanceViewModel, onClose: () -> Unit) {
    val c = ElintTheme.colors
    var kind by remember { mutableStateOf(initial) }
    var lateMin by remember { mutableStateOf(30) }
    var reason by remember { mutableStateOf(0) }
    var cantReason by remember { mutableStateOf(0) }
    var whenIdx by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }
    var leaveType by remember { mutableStateOf(0) }
    var note by remember { mutableStateOf("") }
    var showPicker by remember { mutableStateOf(false) }
    var range by remember { mutableStateOf<Pair<String, String>?>(null) }

    val ctx = LocalContext.current
    var recorder by remember { mutableStateOf<android.media.MediaRecorder?>(null) }
    var recording by remember { mutableStateOf(false) }
    var voiceFile by remember { mutableStateOf<java.io.File?>(null) }
    fun stopRecording() {
        val r = recorder
        recorder = null
        recording = false
        if (r != null) {
            try { r.stop() } catch (_: Exception) { voiceFile = null }
            try { r.release() } catch (_: Exception) { }
        }
    }
    fun startRecording() {
        try {
            val file = java.io.File(ctx.cacheDir, "voice_${System.currentTimeMillis()}.m4a")
            val r = if (android.os.Build.VERSION.SDK_INT >= 31) android.media.MediaRecorder(ctx) else @Suppress("DEPRECATION") android.media.MediaRecorder()
            r.setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
            r.setOutputFormat(android.media.MediaRecorder.OutputFormat.MPEG_4)
            r.setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AAC)
            r.setAudioEncodingBitRate(32000)
            r.setAudioSamplingRate(16000)
            r.setMaxDuration(30000)
            r.setOutputFile(file.absolutePath)
            r.setOnInfoListener { _, what, _ ->
                if (what == android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) stopRecording()
            }
            r.prepare()
            r.start()
            recorder = r
            voiceFile = file
            recording = true
        } catch (_: Exception) {
            recorder = null
            recording = false
            voiceFile = null
        }
    }
    val micPermission = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startRecording() else viewModel.showToast(com.example.data.model.ToastType.ERROR, s.micNeeded, 3500L)
    }
    androidx.compose.runtime.DisposableEffect(Unit) { onDispose { stopRecording() } }

    val whens = listOf(s.qaToday, s.qaTomorrow)
    val durations = listOf("full" to s.qaFullDay, "first_half" to s.qaFirstHalf, "second_half" to s.qaSecondHalf)
    val reasons = listOf("traffic" to s.qaReasonTraffic, "personal" to s.qaReasonPersonal, "client" to s.qaReasonClient, "other" to s.qaReasonOther)
    val cantReasons = listOf("unwell" to s.qaUnwell, "family" to s.qaFamily, "travel" to s.qaTravel, "other" to s.qaReasonOther)
    val leaveCodes = listOf("casual", "sick", "earned")
    val lateOptions = listOf(15 to s.qaMin15, 30 to s.qaMin30, 45 to s.qaMin45, 60 to s.qaHour1)
    val balances = uiState.home?.leaveBalance.orEmpty().associate { it.type to it.remaining }
    val shiftStart = uiState.home?.shift?.start.orEmpty()
    val expected = remember(lateMin, shiftStart) {
        val parts = shiftStart.split(":")
        val sh = parts.getOrNull(0)?.toIntOrNull()
        val sm = parts.getOrNull(1)?.toIntOrNull()
        if (sh == null || sm == null) return@remember ""
        val total = sh * 60 + sm + lateMin
        val hh = (total / 60) % 24
        String.format(Locale.US, "%d:%02d %s", if (hh % 12 == 0) 12 else hh % 12, total % 60, if (hh < 12) "AM" else "PM")
    }
    val rangeLabel = range?.let { "${it.first} → ${it.second}" } ?: s.qaPickDates

    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).navigationBarsPadding()
            .padding(horizontal = ElintDimens.sheetPaddingHorizontal).padding(bottom = ElintDimens.sheetPaddingBottom),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(s.qaSheetTitle, style = ElintText.sheetTitle, color = HomeInk)
                Text(s.qaSheetSub, style = ElintText.caption, color = HomeMuted)
            }
            Box(
                Modifier.size(ElintDimens.iconButtonSize).clip(CircleShape).background(ElintSurfaceSubtle).clickable { viewModel.clearQuickUpdateError(); onClose() },
                contentAlignment = Alignment.Center
            ) { Icon(painterResource(R.drawable.ic_close), contentDescription = null, tint = HomeInk, modifier = Modifier.size(ElintDimens.iconMedium)) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Segment(painterResource(R.drawable.ic_clock), s.qaRunningLate, kind == QuickKind.LATE, Modifier.weight(1f)) { kind = QuickKind.LATE }
            Segment(painterResource(R.drawable.ic_home), s.qaWfh, kind == QuickKind.WFH, Modifier.weight(1f)) { kind = QuickKind.WFH }
            Segment(painterResource(R.drawable.ic_x_circle), s.qaCantCome, kind == QuickKind.CANT, Modifier.weight(1f)) { kind = QuickKind.CANT }
            Segment(painterResource(R.drawable.ic_calendar), s.qaTakeLeave, kind == QuickKind.LEAVE, Modifier.weight(1f)) { kind = QuickKind.LEAVE }
        }

        when (kind) {
            QuickKind.LATE -> {
                SectionLabel(s.qaHowLate)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    lateOptions.forEach { (min, label) -> Chip(label, lateMin == min, isDefault = min == 30) { lateMin = min } }
                }
                if (expected.isNotBlank()) Text(s.qaExpectedArrival.replace("{time}", expected), style = ElintText.label, color = ElintBrandPrimary)
                SectionLabel(s.qaReasonOptional)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    reasons.forEachIndexed { i, (_, label) -> Chip(label, reason == i, isDefault = i == 0) { reason = i } }
                }
            }
            QuickKind.WFH -> {
                SectionLabel(s.qaWhen)
                WhenChips(whens, whenIdx, rangeLabel, { whenIdx = it }) { whenIdx = 2; showPicker = true }
                SectionLabel(s.qaHowLong)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    durations.forEachIndexed { i, (_, label) -> Chip(label, duration == i, isDefault = i == 0) { duration = i } }
                }
                Row(
                    Modifier.fillMaxWidth().clip(ElintShapes.statusLine).background(c.workFromHome.border.copy(alpha = 0.35f)).padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_info), contentDescription = null, tint = c.workFromHome.icon, modifier = Modifier.size(ElintDimens.iconSmall))
                    Text(s.qaWfhInfo, style = ElintText.caption, color = c.workFromHome.label)
                }
            }
            QuickKind.CANT -> {
                SectionLabel(s.qaWhatHappened)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cantReasons.forEachIndexed { i, (_, label) -> Chip(label, cantReason == i, isDefault = i == 0) { cantReason = i } }
                }
                Text(s.qaUnplannedInfo, style = ElintText.caption, color = HomeMuted)
            }
            QuickKind.LEAVE -> {
                SectionLabel(s.qaWhen)
                WhenChips(whens, whenIdx, rangeLabel, { whenIdx = it }) { whenIdx = 2; showPicker = true }
                SectionLabel(s.qaLeaveType)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    leaveCodes.forEachIndexed { i, code ->
                        val left = balances[code]
                        val tail = if (left != null) " · ${if (left % 1.0 == 0.0) left.toInt() else left}" else ""
                        Chip(leaveLabel(code, s) + tail, leaveType == i, isDefault = i == 0) { leaveType = i }
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            placeholder = { Text(s.qaNoteHint, style = ElintText.body, color = HomeMuted) },
            textStyle = ElintText.body,
            singleLine = true,
            shape = ElintShapes.pill,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElintBrandPrimary,
                unfocusedBorderColor = ElintOutline,
                focusedContainerColor = c.inputBackground,
                unfocusedContainerColor = c.inputBackground
            ),
            modifier = Modifier.weight(1f).heightIn(min = ElintDimens.inputHeight)
        )
            Box(
                Modifier
                    .size(ElintDimens.iconButtonSize)
                    .clip(CircleShape)
                    .background(if (recording) ElintTheme.colors.cantCome.icon else ElintBrandPrimary)
                    .clickable {
                        if (recording) {
                            stopRecording()
                        } else if (androidx.core.content.ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            startRecording()
                        } else {
                            micPermission.launch(android.Manifest.permission.RECORD_AUDIO)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.ic_mic), contentDescription = null, tint = Color.White, modifier = Modifier.size(ElintDimens.iconMedium))
            }
        }
        if (recording) {
            Text(s.voiceRecording, style = ElintText.caption, color = ElintTheme.colors.cantCome.icon)
        } else if (voiceFile != null) {
            Text(s.voiceAdded, style = ElintText.caption, color = ElintBrandPrimary)
        }

        uiState.quickUpdateError?.let {
            Text(s.localizeToast(it), style = ElintText.label, color = ElintTheme.colors.cantCome.icon)
        }

        val sendLabel = when (kind) {
            QuickKind.WFH -> s.qaRequestWfh
            QuickKind.LEAVE -> s.qaRequestLeave
            else -> s.qaNotify
        }
        val needsRange = (kind == QuickKind.WFH || kind == QuickKind.LEAVE) && whenIdx == 2 && range == null
        Surface(
            shape = ElintShapes.pill,
            color = if (uiState.isSendingQuick || needsRange) ElintBrandPrimary.copy(alpha = 0.5f) else ElintBrandPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .height(ElintDimens.primaryButtonHeight)
                .clickable(enabled = !uiState.isSendingQuick && !needsRange) {
                    val f = mutableMapOf("kind" to kind.code, "note" to note.trim())
                    if (kind == QuickKind.WFH || kind == QuickKind.LEAVE) {
                        f["when"] = when (whenIdx) { 0 -> "today"; 1 -> "tomorrow"; else -> "dates" }
                        range?.takeIf { whenIdx == 2 }?.let { f["from_date"] = it.first; f["to_date"] = it.second }
                    }
                    when (kind) {
                        QuickKind.LATE -> { f["late_minutes"] = lateMin.toString(); f["reason"] = reasons[reason].first }
                        QuickKind.WFH -> f["duration"] = durations[duration].first
                        QuickKind.CANT -> f["reason"] = cantReasons[cantReason].first
                        QuickKind.LEAVE -> f["leave_type"] = leaveCodes[leaveType]
                    }
                    if (recording) stopRecording()
                    viewModel.sendQuickUpdate(f, voiceFile) { onClose() }
                }
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(if (uiState.isSendingQuick) s.qaSending else sendLabel, style = ElintText.buttonLarge, color = Color.White)
            }
        }
    }

    if (showPicker) {
        val pickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false; if (range == null) whenIdx = 0 },
            confirmButton = {
                TextButton(onClick = {
                    val a = pickerState.selectedStartDateMillis
                    val b = pickerState.selectedEndDateMillis ?: a
                    if (a != null && b != null) range = DateCode.format(Date(a)) to DateCode.format(Date(b))
                    showPicker = false
                    if (range == null) whenIdx = 0
                }) { Text("OK", color = ElintBrandPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false; if (range == null) whenIdx = 0 }) { Text("✕", color = HomeMuted) }
            }
        ) {
            DateRangePicker(state = pickerState, modifier = Modifier.height(480.dp), showModeToggle = false)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WhenChips(whens: List<String>, selected: Int, rangeLabel: String, onPick: (Int) -> Unit, onPickDates: () -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        whens.forEachIndexed { i, label -> Chip(label, selected == i, isDefault = i == 0) { onPick(i) } }
        Chip(rangeLabel, selected == 2) { onPickDates() }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = ElintText.cardTitle, color = HomeInk)
}

@Composable
private fun Chip(label: String, selected: Boolean, isDefault: Boolean = false, onClick: () -> Unit) {
    Surface(
        shape = ElintShapes.pill,
        color = if (selected) ElintBrandPrimary else ElintSurface,
        border = BorderStroke(1.dp, if (selected) ElintBrandPrimary else ElintOutline),
        modifier = Modifier.heightIn(min = ElintDimens.chipHeight).clickable { onClick() }
    ) {
        Box(Modifier.padding(horizontal = ElintDimens.chipPaddingHorizontal), contentAlignment = Alignment.Center) {
            Text(
                label,
                style = ElintText.label.copy(fontWeight = if (isDefault) FontWeight.ExtraBold else FontWeight.Medium, fontSize = 14.sp),
                color = if (selected) Color.White else HomeInk,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun Segment(icon: Painter, label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        shape = ElintShapes.tile,
        color = if (selected) ElintBrandPrimary else ElintSurface,
        border = BorderStroke(1.dp, if (selected) ElintBrandPrimary else ElintOutline),
        modifier = modifier.heightIn(min = ElintDimens.sheetOptionHeight).clickable { onClick() }
    ) {
        Column(
            Modifier.padding(horizontal = 4.dp, vertical = 8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) Color.White else ElintBrandPrimary, modifier = Modifier.size(ElintDimens.iconLarge))
            Spacer(Modifier.height(4.dp))
            Text(
                label, style = ElintText.tileLabel, color = if (selected) Color.White else HomeInk,
                textAlign = TextAlign.Center, maxLines = 2, minLines = 2
            )
        }
    }
}

/** Offline punch waiting for the server: marked pending until it syncs. */
@Composable
private fun PendingNote(text: String) {
    val c = ElintTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth().clip(ElintShapes.statusLine).background(c.statusLineSurface).padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(ElintTheme.colors.runningLate.icon))
        Text(text, style = ElintText.caption.copy(fontWeight = FontWeight.SemiBold), color = c.statusLineText)
    }
}

/** Opens the maps app on the phone's current position (fresh fix first, last known as fallback). */
private fun openLiveMap(context: android.content.Context, fallbackLat: Double?, fallbackLng: Double?) {
    fun launch(lat: Double, lng: Double) {
        try {
            val label = Uri.encode("You are here")
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$lat,$lng($label)")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (_: Exception) {
        }
    }
    fun fallback() {
        if (fallbackLat != null && fallbackLng != null) launch(fallbackLat, fallbackLng)
    }
    val granted = androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
        android.content.pm.PackageManager.PERMISSION_GRANTED ||
        androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
        android.content.pm.PackageManager.PERMISSION_GRANTED
    if (!granted) {
        fallback()
        return
    }
    try {
        com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
            .getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { loc -> if (loc != null) launch(loc.latitude, loc.longitude) else fallback() }
            .addOnFailureListener { fallback() }
    } catch (_: SecurityException) {
        fallback()
    }
}

/** Children keep 8dp between them and share spare height up to 16dp each, so the layout stays compact. */
private val SpreadToFill = object : Arrangement.Vertical {
    override val spacing = 8.dp
    override fun androidx.compose.ui.unit.Density.arrange(totalSize: Int, sizes: IntArray, outPositions: IntArray) {
        val min = 8.dp.roundToPx()
        val gap = if (sizes.size > 1) ((totalSize - sizes.sum()) / (sizes.size - 1)).coerceIn(min, 16.dp.roundToPx()) else 0
        var pos = 0
        sizes.forEachIndexed { i, size ->
            outPositions[i] = pos
            pos += size + gap
        }
    }
}
