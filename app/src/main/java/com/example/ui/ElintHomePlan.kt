package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.os.Build
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.model.ToastType
import com.example.data.model.VisitContext
import com.example.lang.AppStrings
import com.example.ui.theme.ElintBrandPrimary
import com.example.ui.theme.ElintBrandPrimaryContainer
import com.example.ui.theme.ElintShapes
import com.example.ui.theme.ElintSurface
import com.example.ui.theme.ElintText
import java.io.ByteArrayOutputStream
import java.io.File

/** Add note / Photo / Voice note / Details tiles shown while checked in, with their dialogs. */
@Composable
internal fun VisitTools(s: AppStrings, uiState: AttendanceUiState, viewModel: AttendanceViewModel) {
    val ctx = LocalContext.current
    var showNote by remember { mutableStateOf(false) }
    var showVoice by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    val saved = s.noteSavedLbl

    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
        if (bmp != null) {
            val out = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, out)
            viewModel.sendVisitNote("photo", "", Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP), saved)
        }
    }
    val camPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) photoLauncher.launch(null)
    }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionTile(Icons.Default.Edit, s.addNoteLbl, Modifier.weight(1f)) { showNote = true }
        ActionTile(Icons.Default.PhotoCamera, s.photoLbl, Modifier.weight(1f)) {
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) photoLauncher.launch(null)
            else camPermission.launch(Manifest.permission.CAMERA)
        }
        ActionTile(Icons.Default.Mic, s.voiceNoteLbl, Modifier.weight(1f)) { showVoice = true }
        ActionTile(Icons.Default.Description, s.detailsLbl, Modifier.weight(1f)) {
            viewModel.loadVisitDetails()
            showDetails = true
        }
    }

    if (showNote) {
        NoteEditorDialog(s, onDismiss = { showNote = false }) { txt -> viewModel.sendVisitNote("note", txt, null, saved) }
    }

    if (showVoice) {
        VoiceNoteDialog(s, viewModel, onClose = { showVoice = false })
    }

    if (showDetails) {
        val d = uiState.visitDetails
        AlertDialog(
            onDismissRequest = { showDetails = false },
            title = { Text(d?.customer?.name?.takeIf { it.isNotBlank() } ?: d?.current?.name ?: s.detailsLbl, style = ElintText.sheetTitle, color = HomeInk) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    d?.customer?.address?.takeIf { it.isNotBlank() }?.let { Text(it, style = ElintText.body, color = HomeMuted) }
                    d?.customer?.phone?.takeIf { it.isNotBlank() }?.let { Text(it, style = ElintText.label, color = ElintBrandPrimary) }
                    d?.current?.task?.takeIf { it.isNotBlank() }?.let { Text("• $it", style = ElintText.label, color = HomeInk) }
                    Text(s.notesTodayLbl, style = ElintText.overline, color = ElintBrandPrimary, modifier = Modifier.padding(top = 8.dp))
                    d?.notes.orEmpty().forEach { n ->
                        val line = androidx.compose.ui.text.buildAnnotatedString {
                            append("${n.time}  ·  ")
                            when (n.kind) {
                                "photo" -> append(s.photoLbl)
                                "voice" -> append(s.voiceNoteLbl)
                                else -> append(formatNote(n.text))
                            }
                        }
                        Text(line, style = ElintText.caption, color = HomeInk)
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showDetails = false }) { Text(s.vCancel, color = ElintBrandPrimary, fontWeight = FontWeight.Bold) } }
        )
    }
}

@Composable
private fun ActionTile(icon: ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(shape = ElintShapes.tile, color = ElintSurface, modifier = modifier.clickable { onClick() }) {
        Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = null, tint = ElintBrandPrimary, modifier = Modifier.size(24.dp))
            Text(label, style = ElintText.caption.copy(fontWeight = FontWeight.SemiBold), color = HomeInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun VoiceNoteDialog(s: AppStrings, viewModel: AttendanceViewModel, onClose: () -> Unit) {
    val ctx = LocalContext.current
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var recording by remember { mutableStateOf(false) }
    var file by remember { mutableStateOf<File?>(null) }

    fun stop() {
        val r = recorder
        recorder = null
        recording = false
        if (r != null) {
            try { r.stop() } catch (_: Exception) { file = null }
            try { r.release() } catch (_: Exception) { }
        }
    }
    fun start() {
        try {
            val f = File(ctx.cacheDir, "visit_${System.currentTimeMillis()}.m4a")
            val r = if (Build.VERSION.SDK_INT >= 31) MediaRecorder(ctx) else @Suppress("DEPRECATION") MediaRecorder()
            r.setAudioSource(MediaRecorder.AudioSource.MIC)
            r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            r.setAudioEncodingBitRate(32000)
            r.setAudioSamplingRate(16000)
            r.setMaxDuration(60000)
            r.setOutputFile(f.absolutePath)
            r.setOnInfoListener { _, what, _ -> if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) stop() }
            r.prepare()
            r.start()
            recorder = r
            file = f
            recording = true
        } catch (_: Exception) {
            recorder = null
            recording = false
            file = null
        }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) start() else viewModel.showToast(ToastType.ERROR, s.micNeeded, 3500L)
    }
    DisposableEffect(Unit) { onDispose { stop() } }

    AlertDialog(
        onDismissRequest = { stop(); onClose() },
        title = { Text(s.voiceNoteLbl, style = ElintText.sheetTitle, color = HomeInk) },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(if (recording) Color(0xFFDC2626) else ElintBrandPrimary)
                        .clickable {
                            if (recording) stop()
                            else if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) start()
                            else permission.launch(Manifest.permission.RECORD_AUDIO)
                        },
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp)) }
                Text(if (recording) s.stopRecLbl else if (file != null) s.recordedLbl else s.recordLbl, style = ElintText.body, color = HomeInk)
            }
        },
        confirmButton = {
            TextButton(enabled = !recording && file != null, onClick = {
                val f = file
                if (f != null) viewModel.sendVisitNote("voice", "", viewModel.encodeVoiceFile(f), s.noteSavedLbl)
                onClose()
            }) { Text(s.vSave, color = ElintBrandPrimary, fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = { stop(); onClose() }) { Text(s.vCancel, color = HomeMuted) } }
    )
}

/** Today's plan: the planned stops with done / now / to-do state. */
@Composable
internal fun PlanCard(s: AppStrings, plan: VisitContext) {
    if (plan.items.isEmpty()) return
    var all by remember { mutableStateOf(false) }
    val shown = if (all) plan.items else plan.items.take(4)
    Surface(shape = ElintShapes.card, color = ElintSurface, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(s.todaysPlan, style = ElintText.cardTitle, color = HomeInk, modifier = Modifier.weight(1f))
                Text(s.planDoneOf.replace("{a}", plan.done.toString()).replace("{b}", plan.total.toString()), style = ElintText.caption, color = HomeMuted)
            }
            shown.forEach { item ->
                val now = item.state == "now"
                Row(
                    modifier = Modifier.fillMaxWidth().clip(ElintShapes.tile)
                        .background(if (now) ElintBrandPrimaryContainer.copy(alpha = 0.45f) else Color.Transparent)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                            .background(if (item.state == "done") ElintBrandPrimaryContainer else if (now) ElintBrandPrimary else Color.Transparent)
                            .then(if (item.state == "todo") Modifier.background(Color.Transparent) else Modifier),
                        contentAlignment = Alignment.Center
                    ) {
                        when (item.state) {
                            "done" -> Icon(androidx.compose.ui.res.painterResource(com.example.R.drawable.ic_check), contentDescription = null, tint = ElintBrandPrimary, modifier = Modifier.size(14.dp))
                            "now" -> Box(Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                            else -> Surface(shape = CircleShape, color = Color.Transparent, border = BorderStroke(1.5.dp, Color(0xFFD8D6CE)), modifier = Modifier.size(24.dp)) {}
                        }
                    }
                    Column(Modifier.weight(1f)) {
                        Text(item.name, style = ElintText.label.copy(fontWeight = FontWeight.Bold), color = if (item.state == "done") HomeMuted else HomeInk, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (item.address.isNotBlank()) Text(item.address, style = ElintText.caption, color = HomeMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    if (now) {
                        Surface(shape = ElintShapes.pill, color = ElintBrandPrimaryContainer) {
                            Text(s.nowLbl, style = ElintText.label.copy(fontWeight = FontWeight.Bold), color = ElintBrandPrimary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                        }
                    } else if (item.time.isNotBlank()) {
                        Text(item.time, style = ElintText.caption, color = HomeMuted)
                    }
                }
            }
            if (plan.total > 4) {
                Text(
                    if (all) s.showLessLbl else s.seeAllVisits.replace("{total}", plan.total.toString()),
                    style = ElintText.label.copy(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline),
                    color = ElintBrandPrimary,
                    modifier = Modifier.align(Alignment.CenterHorizontally).clickable { all = !all }.padding(vertical = 4.dp)
                )
            }
        }
    }
}
