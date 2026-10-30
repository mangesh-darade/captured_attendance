package com.example.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.lang.LangManager
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Gentle, capped nudges: one check-in reminder per working day (shift start + grace), only when the
 * person is not checked in and has not already told the manager; urgent announcements; and a
 * "looks like you've left" check-out prompt from the area tracker.
 */
object ReminderScheduler {
    private const val PREFS = "home_prefs"
    private const val CHANNEL = "reminders"
    private const val REQUEST_CODE = 4210
    private const val POLL_CODE = 4220
    const val ACTION_POLL = "com.example.reminder.POLL"
    const val DEFAULT_GRACE_MIN = 10

    /** Remembers the shift and language and sets the next weekday alarm. */
    fun schedule(context: Context, shiftStart: String, language: String, graceMin: Int = DEFAULT_GRACE_MIN, weeklyOff: List<Int> = listOf(6, 7)) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString("shift_start", shiftStart).putString("lang", language).putInt("reminder_grace_min", graceMin).putString("weekly_off", weeklyOff.joinToString(",")).apply()
        scheduleNext(context)
        schedulePoll(context)
    }

    fun scheduleNext(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val parts = (prefs.getString("shift_start", "") ?: "").split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: return
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: return
        val grace = prefs.getInt("reminder_grace_min", DEFAULT_GRACE_MIN)

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MINUTE, grace)
        }
        // Weekly off days come from the shift (ISO 1 = Monday ... 7 = Sunday).
        val off = (prefs.getString("weekly_off", "6,7") ?: "").split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
        fun isOff(c: Calendar): Boolean {
            val iso = if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 7 else c.get(Calendar.DAY_OF_WEEK) - 1
            return iso in off
        }
        var guard = 0
        while ((cal.timeInMillis <= System.currentTimeMillis() || isOff(cal)) && guard++ < 14) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getBroadcast(
            context, REQUEST_CODE, Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Inexact on purpose: no exact-alarm permission, and a nudge a few minutes late is fine.
        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi)
    }

    private fun pollIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(
        context, POLL_CODE, Intent(context, ReminderReceiver::class.java).setAction(ACTION_POLL),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    /** Next notification check in 10 minutes, only between 07:00 and 21:00. Stands in for push until FCM is added. */
    fun schedulePoll(context: Context) {
        val cal = Calendar.getInstance().apply { add(Calendar.MINUTE, 10) }
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        if (hour >= 21 || hour < 7) {
            cal.set(Calendar.HOUR_OF_DAY, 7)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            if (cal.timeInMillis <= System.currentTimeMillis()) cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pollIntent(context))
    }

    /** Checks the server right now (app opened); the work runs on a background thread. */
    fun pollNow(context: Context) {
        val app = context.applicationContext
        Thread { try { poll(app) } catch (_: Exception) { } }.start()
    }

    private fun kindName(kind: String, s: com.example.lang.AppStrings): String = when (kind) {
        "late" -> s.qaRunningLate
        "wfh" -> s.qaWfh
        "cant" -> s.qaCantCome
        "leave" -> s.qaTakeLeave
        else -> s.reqCorrection
    }

    /**
     * Shows what the server queued for this employee: request decisions, announcements and Home messages
     * (attendance_api/notifications), and tells a manager how many requests wait. Sound, vibration and mute
     * follow the phone's own settings for the notification channels.
     */
    fun poll(context: Context): Int {
        val fast = context.getSharedPreferences("attendance_fast_prefs", Context.MODE_PRIVATE)
        val token = fast.getString("device_token", "").orEmpty()
        val base = fast.getString("tenant_base_url", "").orEmpty()
        if (token.isBlank() || base.isBlank()) return 0
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val lang = language(context)
        val s = LangManager.getStrings(lang)

        var shown = 0
        val since = prefs.getInt("notif_last_id", -1)
        val json = fetchJson(base, "notifications" + if (since >= 0) "&since=$since" else "", token, lang, queryHasMark = since >= 0)
        if (json != null) {
            val items = json.optJSONArray("items")
            var lastId = since
            val count = items?.length() ?: 0
            for (i in 0 until count) {
                val n = items!!.getJSONObject(i)
                val id = n.optInt("id")
                val meta = n.optJSONObject("meta")
                when (n.optString("kind")) {
                    "request" -> {
                        val approved = meta?.optString("status") == "approved"
                        val label = kindName(meta?.optString("kind").orEmpty(), s)
                        val note = n.optString("body")
                        notify(context, 5000 + id, if (approved) s.pushApproved else s.pushRejected, if (note.isNotBlank()) "$label · $note" else label, CH_REQUESTS, count)
                    }
                    "announcement" -> notify(context, 5000 + id, s.pushNewAnnouncement + ": " + n.optString("title"), n.optString("body"), CH_NEWS, count)
                    else -> notify(context, 5000 + id, s.pushNewMessage, n.optString("body"), CH_NEWS, count)
                }
                lastId = maxOf(lastId, id)
                shown++
            }
            val max = json.optInt("max_id")
            prefs.edit().putInt("notif_last_id", maxOf(lastId, if (since < 0) max else lastId)).apply()
        }

        val inbox = fetchJson(base, "manager_inbox", token, lang)
        if (inbox != null) {
            val count = inbox.optInt("pending_count")
            val latest = inbox.optInt("latest_id")
            if (count > 0 && latest > prefs.getInt("last_inbox_notified", 0)) {
                prefs.edit().putInt("last_inbox_notified", latest).apply()
                notify(context, 4215, s.pushWaiting.replace("{n}", count.toString()), "", CH_REQUESTS, count)
                shown++
            }
        }
        return shown
    }

    fun cancel(context: Context) {
        val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pollIntent(context))
        val pi = PendingIntent.getBroadcast(
            context, REQUEST_CODE, Intent(context, ReminderReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarm.cancel(pi)
    }

    private fun language(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("lang", "en") ?: "en"

    const val CH_REMINDERS = "reminders"
    const val CH_REQUESTS = "requests"
    const val CH_NEWS = "news"

    private fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val manager = context.getSystemService(NotificationManager::class.java)
        val s = LangManager.getStrings(language(context))
        // Default sound + vibration; the user can mute or change each channel in Android settings.
        manager.createNotificationChannel(NotificationChannel(CH_REMINDERS, "Reminders", NotificationManager.IMPORTANCE_DEFAULT))
        manager.createNotificationChannel(NotificationChannel(CH_REQUESTS, "Requests", NotificationManager.IMPORTANCE_HIGH))
        manager.createNotificationChannel(NotificationChannel(CH_NEWS, s.pushNewAnnouncement + " / " + s.pushNewMessage, NotificationManager.IMPORTANCE_DEFAULT))
    }

    fun notify(context: Context, id: Int, title: String, body: String, channel: String = CH_REMINDERS, count: Int = 1) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        ensureChannels(context)
        val manager = context.getSystemService(NotificationManager::class.java)
        val open = PendingIntent.getActivity(
            context, id, Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val n = NotificationCompat.Builder(context, channel)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setNumber(count)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setGroup("elint_updates")
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(open)
            .build()
        manager.notify(id, n)
    }

    /** Area tracker saw the person leave the site while still checked in. */
    fun notifyLeftSite(context: Context, siteName: String) {
        val s = LangManager.getStrings(language(context))
        notify(context, 4212, s.reminderLeftTitle.replace("{site}", siteName), s.reminderLeftBody)
    }

    /** Runs on a background thread from the alarm. */
    fun check(context: Context) {
        val fast = context.getSharedPreferences("attendance_fast_prefs", Context.MODE_PRIVATE)
        val token = fast.getString("device_token", "").orEmpty()
        val base = fast.getString("tenant_base_url", "").orEmpty()
        if (token.isBlank() || base.isBlank()) return
        val home = fetchJson(base, "home_summary", token, language(context)) ?: return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val s = LangManager.getStrings(language(context))
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        val state = home.optJSONObject("today")?.optString("state").orEmpty()
        val req = home.optJSONObject("request")
        val reqStatus = req?.optString("status").orEmpty()
        val told = req != null && reqStatus in listOf("sent", "pending", "approved")
        if (state == "not_in" && !told && prefs.getString("last_reminder_day", "") != today) {
            prefs.edit().putString("last_reminder_day", today).apply()
            val start = home.optJSONObject("shift")?.optString("start").orEmpty()
            notify(context, 4211, s.reminderCheckInTitle, s.reminderCheckInBody.replace("{time}", start))
        }
    }

    private fun fetchJson(base: String, path: String, token: String, lang: String = "en", queryHasMark: Boolean = false): JSONObject? = try {
        val route = path.substringBefore("&")
        val extra = if (path.contains("&")) "&" + path.substringAfter("&") else ""
        val url = URL(base.trimEnd('/') + "/attendance_api/" + route + "?token=" + java.net.URLEncoder.encode(token, "UTF-8") + extra)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("X-App-Lang", lang)
        }
        conn.inputStream.bufferedReader().use { r ->
            val json = JSONObject(r.readText())
            if (json.optString("status") == "success") json else null
        }
    } catch (_: Exception) {
        null
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pending = goAsync()
        val poll = intent?.action == ReminderScheduler.ACTION_POLL
        Thread {
            val app = context.applicationContext
            try {
                if (poll) ReminderScheduler.poll(app) else ReminderScheduler.check(app)
            } catch (_: Exception) {
            } finally {
                if (poll) ReminderScheduler.schedulePoll(app) else ReminderScheduler.scheduleNext(app)
                pending.finish()
            }
        }.start()
    }
}
