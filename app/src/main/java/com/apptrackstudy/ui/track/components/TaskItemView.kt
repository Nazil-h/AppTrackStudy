package com.apptrackstudy.ui.track.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.apptrackstudy.domain.model.StudyTask

/**
 * Item checklist Task (Materi Inti).
 * Dilengkapi animasi centang, teks dicoret dan memudar saat selesai,
 * tombol pintas timer fokus, dan target tap minimal 48dp.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItemView(
    task: StudyTask,
    accentColor: Color,
    onToggleDone: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onStartTimerClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    // Animasi pemudaran teks dan warna
    val taskAlpha by animateFloatAsState(
        targetValue = if (task.isDone) 0.5f else 1f,
        animationSpec = tween(durationMillis = 240),
        label = "taskAlpha"
    )

    val textColor by animateColorAsState(
        targetValue = if (task.isDone) {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 240),
        label = "taskTextColor"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = { onToggleDone(!task.isDone) },
                onLongClick = { showMenu = true }
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .alpha(taskAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox dengan touch target yang ramah
        Checkbox(
            checked = task.isDone,
            onCheckedChange = { onToggleDone(it) },
            colors = CheckboxDefaults.colors(
                checkedColor = accentColor,
                checkmarkColor = Color.White,
                uncheckedColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Judul Task & Status dicoret
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                    fontWeight = if (task.isDone) FontWeight.Normal else FontWeight.Medium
                ),
                color = textColor
            )
        }

        // Tombol Langsung Mulai Timer Fokus
        IconButton(
            onClick = onStartTimerClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = "Mulai timer untuk ${task.title}",
                tint = if (task.isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f) else accentColor,
                modifier = Modifier.size(18.dp)
            )
        }

        // Menu Titik Tiga (Edit / Hapus)
        Box {
            IconButton(
                onClick = { showMenu = true },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu opsi materi",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Mulai Timer Fokus") },
                    onClick = {
                        showMenu = false
                        onStartTimerClick()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Edit Materi") },
                    onClick = {
                        showMenu = false
                        onEditClick()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Hapus Materi", color = MaterialTheme.colorScheme.error) },
                    onClick = {
                        showMenu = false
                        onDeleteClick()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }
    }
}
