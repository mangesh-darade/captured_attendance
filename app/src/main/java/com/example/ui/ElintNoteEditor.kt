package com.example.ui

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lang.AppStrings
import com.example.ui.theme.ElintBrandPrimary
import com.example.ui.theme.ElintBrandPrimaryContainer
import com.example.ui.theme.ElintShapes
import com.example.ui.theme.ElintText

/**
 * Notes are stored as plain text with light markup: **bold**, //italic//, __underline__ and "• " bullets.
 * The editor shows the formatting live (markers dimmed); readers get it rendered by [formatNote].
 */
private val Markers = listOf("**", "//", "__")

private fun styleOf(marker: String): SpanStyle = when (marker) {
    "**" -> SpanStyle(fontWeight = FontWeight.Bold)
    "//" -> SpanStyle(fontStyle = FontStyle.Italic)
    else -> SpanStyle(textDecoration = TextDecoration.Underline)
}

private object MarkupTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val b = AnnotatedString.Builder(text.text)
        for (m in Markers) {
            Regex(Regex.escape(m) + "(.+?)" + Regex.escape(m)).findAll(text.text).forEach { r ->
                val s = r.range.first
                val e = r.range.last + 1
                b.addStyle(SpanStyle(color = Color(0x55000000)), s, s + m.length)
                b.addStyle(SpanStyle(color = Color(0x55000000)), e - m.length, e)
                b.addStyle(styleOf(m), s + m.length, e - m.length)
            }
        }
        return TransformedText(b.toAnnotatedString(), OffsetMapping.Identity)
    }
}

/** Renders the stored markup for reading (markers removed, nesting supported). */
internal fun formatNote(text: String): AnnotatedString = AnnotatedString.Builder().apply { appendMarked(text) }.toAnnotatedString()

private fun AnnotatedString.Builder.appendMarked(text: String) {
    val regex = Regex("(\\*\\*|//|__)(.+?)\\1")
    var last = 0
    for (m in regex.findAll(text)) {
        append(text.substring(last, m.range.first))
        pushStyle(styleOf(m.groupValues[1]))
        appendMarked(m.groupValues[2])
        pop()
        last = m.range.last + 1
    }
    append(text.substring(last))
}

@Composable
private fun FmtButton(label: String, style: SpanStyle, onClick: () -> Unit) {
    Box(
        Modifier.size(36.dp).clip(ElintShapes.tile).background(ElintBrandPrimaryContainer.copy(alpha = 0.5f)).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(AnnotatedString(label, style), fontSize = 16.sp, color = ElintBrandPrimary)
    }
}

@Composable
internal fun NoteEditorDialog(s: AppStrings, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var tf by remember { mutableStateOf(TextFieldValue("")) }

    fun wrap(m: String) {
        val t = tf.text
        val sel = tf.selection
        val a = minOf(sel.start, sel.end)
        val b = maxOf(sel.start, sel.end)
        tf = if (a == b) {
            TextFieldValue(t.substring(0, a) + m + m + t.substring(b), TextRange(a + m.length))
        } else {
            val inner = t.substring(a, b)
            if (inner.length >= m.length * 2 && inner.startsWith(m) && inner.endsWith(m)) {
                val plain = inner.substring(m.length, inner.length - m.length)
                TextFieldValue(t.substring(0, a) + plain + t.substring(b), TextRange(a, a + plain.length))
            } else {
                TextFieldValue(t.substring(0, a) + m + inner + m + t.substring(b), TextRange(a, b + m.length * 2))
            }
        }
    }

    fun bullet() {
        val t = tf.text
        val pos = tf.selection.start
        val lineStart = t.lastIndexOf('\n', (pos - 1).coerceAtLeast(0)).let { if (pos == 0) 0 else it + 1 }
        tf = TextFieldValue(t.substring(0, lineStart) + "• " + t.substring(lineStart), TextRange(pos + 2))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(s.addNoteLbl, style = ElintText.sheetTitle, color = HomeInk) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FmtButton("B", SpanStyle(fontWeight = FontWeight.Bold)) { wrap("**") }
                    FmtButton("I", SpanStyle(fontStyle = FontStyle.Italic)) { wrap("//") }
                    FmtButton("U", SpanStyle(textDecoration = TextDecoration.Underline)) { wrap("__") }
                    FmtButton("•", SpanStyle()) { bullet() }
                }
                BasicTextField(
                    value = tf,
                    onValueChange = { if (it.text.length <= 500) tf = it },
                    textStyle = ElintText.body.copy(color = HomeInk),
                    cursorBrush = SolidColor(ElintBrandPrimary),
                    visualTransformation = MarkupTransformation,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 110.dp)
                        .clip(ElintShapes.tile)
                        .background(Color(0xFFF4F3EF))
                        .padding(12.dp)
                )
            }
        },
        confirmButton = {
            TextButton(enabled = tf.text.isNotBlank(), onClick = { onSave(tf.text.trim()); onDismiss() }) {
                Text(s.vSave, color = ElintBrandPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(s.vCancel, color = HomeMuted) } }
    )
}
