package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Response of attendance_api/home_summary: everything the companion app home screen shows. */
@JsonClass(generateAdapter = true)
data class HomeSummaryResponse(
    val status: String? = null,
    val message: String? = null,
    val user: HomeUser? = null,
    val shift: HomeShift? = null,
    val today: HomeToday? = null,
    val wrapup: HomeWrapup? = null,
    val request: HomeRequest? = null,
    val announcements: List<HomeAnnouncement>? = null,
    @Json(name = "unread_count") val unreadCount: Int? = null,
    val stats: HomeStats? = null,
    @Json(name = "leave_balance") val leaveBalance: List<LeaveBalance>? = null,
    val manager: HomeManager? = null,
    val thought: String? = null,
    val hero: String? = null,
    @Json(name = "missed_punch") val missedPunch: List<MissedPunch>? = null
)

@JsonClass(generateAdapter = true)
data class HomeUser(
    val id: Int = 0,
    val name: String = "",
    @Json(name = "is_admin") val isAdmin: Boolean = false,
    @Json(name = "all_rights") val allRights: Boolean = false,
    @Json(name = "photo_url") val photoUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class HomeShift(
    val name: String = "",
    val start: String = "",
    val end: String = "",
    @Json(name = "grace_min") val graceMin: Int = 0,
    @Json(name = "weekly_off") val weeklyOff: List<Int> = emptyList(),
    val overnight: Boolean = false,
    val text: String = ""
)

/** state: not_in, in or out. */
@JsonClass(generateAdapter = true)
data class HomeToday(
    val state: String = "not_in",
    @Json(name = "first_in") val firstIn: String? = null,
    @Json(name = "last_out") val lastOut: String? = null,
    @Json(name = "worked_minutes") val workedMinutes: Int = 0,
    val visits: Int = 0,
    val location: String = "",
    @Json(name = "outside_minutes") val outsideMinutes: Int = 0,
    @Json(name = "outside_count") val outsideCount: Int = 0
)

@JsonClass(generateAdapter = true)
data class HomeWrapup(
    val note: String = "",
    val mood: Int? = null
)

/** kind: late, wfh, cant, leave. status: sent, pending, approved, rejected. */
@JsonClass(generateAdapter = true)
data class HomeRequest(
    val id: Int = 0,
    val kind: String = "",
    val status: String = "",
    @Json(name = "late_minutes") val lateMinutes: Int? = null,
    @Json(name = "expected_arrival") val expectedArrival: String = "",
    val reason: String = "",
    @Json(name = "leave_type") val leaveType: String = "",
    val duration: String = "",
    @Json(name = "from_date") val fromDate: String = "",
    @Json(name = "to_date") val toDate: String = "",
    val `when`: String = "today",
    val note: String = "",
    @Json(name = "manager_note") val managerNote: String = ""
)

@JsonClass(generateAdapter = true)
data class HomeAnnouncement(
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val tag: String = "",
    @Json(name = "needs_ack") val needsAck: Boolean = true,
    val acknowledged: Boolean = false
)

@JsonClass(generateAdapter = true)
data class HomeStats(
    @Json(name = "days_week") val daysWeek: Int = 0,
    @Json(name = "days_week_target") val daysWeekTarget: Int = 5,
    val streak: Int = 0,
    @Json(name = "month_pct") val monthPct: Int = 0
)

@JsonClass(generateAdapter = true)
data class LeaveBalance(
    val type: String = "",
    val allotted: Double = 0.0,
    val used: Double = 0.0,
    val remaining: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class HomeManager(
    val name: String = "",
    @Json(name = "has_login") val hasLogin: Boolean = false
)

@JsonClass(generateAdapter = true)
data class QuickUpdateResponse(
    val status: String? = null,
    val message: String? = null,
    val request: HomeRequest? = null
)

@JsonClass(generateAdapter = true)
data class SimpleResponse(
    val status: String? = null,
    val message: String? = null
)

@JsonClass(generateAdapter = true)
data class MissedPunch(
    @Json(name = "attendance_id") val attendanceId: Int = 0,
    val date: String = "",
    @Json(name = "check_in") val checkIn: String = "",
    val location: String = ""
)

@JsonClass(generateAdapter = true)
data class RecordDay(
    val date: String = "",
    @Json(name = "first_in") val firstIn: String? = null,
    @Json(name = "last_out") val lastOut: String? = null,
    @Json(name = "worked_minutes") val workedMinutes: Int = 0,
    @Json(name = "overtime_minutes") val overtimeMinutes: Int = 0,
    val late: Boolean = false,
    @Json(name = "missed_checkout") val missedCheckout: Boolean = false,
    val location: String = ""
)

@JsonClass(generateAdapter = true)
data class RecordTotals(
    @Json(name = "days_present") val daysPresent: Int = 0,
    @Json(name = "worked_minutes") val workedMinutes: Int = 0,
    @Json(name = "overtime_minutes") val overtimeMinutes: Int = 0,
    @Json(name = "late_days") val lateDays: Int = 0
)

@JsonClass(generateAdapter = true)
data class RecordsResponse(
    val status: String? = null,
    val message: String? = null,
    val month: String = "",
    val days: List<RecordDay>? = null,
    val totals: RecordTotals? = null,
    val shift: HomeShift? = null,
    @Json(name = "leave_balance") val leaveBalance: List<LeaveBalance>? = null,
    val requests: List<HomeRequest>? = null,
    @Json(name = "missed_punch") val missedPunch: List<MissedPunch>? = null
)

@JsonClass(generateAdapter = true)
data class DayPunch(
    val id: Int = 0,
    @Json(name = "check_in") val checkIn: String = "",
    @Json(name = "check_out") val checkOut: String? = null,
    val location: String = "",
    val open: Boolean = false
)

@JsonClass(generateAdapter = true)
data class DayBreak(
    val from: String = "",
    val to: String = "",
    val minutes: Int = 0
)

@JsonClass(generateAdapter = true)
data class MyDayResponse(
    val status: String? = null,
    val message: String? = null,
    val date: String = "",
    val shift: HomeShift? = null,
    val today: HomeToday? = null,
    val punches: List<DayPunch>? = null,
    val breaks: List<DayBreak>? = null,
    val request: HomeRequest? = null
)

@JsonClass(generateAdapter = true)
data class VisitPlanItem(
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val time: String = "",
    val state: String = "todo"
)

@JsonClass(generateAdapter = true)
data class VisitCurrent(
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val task: String = "",
    @Json(name = "open_orders") val openOrders: Int = 0,
    @Json(name = "last_visit") val lastVisit: String = "",
    @Json(name = "tasks_due") val tasksDue: Int = 0
)

@JsonClass(generateAdapter = true)
data class VisitContext(
    val status: String? = null,
    val items: List<VisitPlanItem> = emptyList(),
    val done: Int = 0,
    val total: Int = 0,
    val current: VisitCurrent? = null
)

@JsonClass(generateAdapter = true)
data class VisitNoteItem(
    val kind: String = "note",
    val text: String = "",
    val file: String = "",
    val time: String = ""
)

@JsonClass(generateAdapter = true)
data class VisitCustomer(
    val name: String = "",
    val address: String = "",
    val phone: String = ""
)

@JsonClass(generateAdapter = true)
data class VisitDetails(
    val status: String? = null,
    val current: VisitCurrent? = null,
    val customer: VisitCustomer? = null,
    val notes: List<VisitNoteItem> = emptyList()
)
