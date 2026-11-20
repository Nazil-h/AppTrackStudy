package com.apptrackstudy.ui.assignment.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apptrackstudy.domain.model.Assignment
import com.apptrackstudy.ui.profile.components.ReminderTimePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Bottom Sheet Material 3 untuk menambah atau mengedit tugas kuliah.
 * Dilengkapi input judul tugas, DatePicker M3, dan TimePicker M3.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAssignmentSheet(
    assignment: Assignment?,
    onDismiss: () -> Unit,
    onSave: (title: String, deadlineMillis: Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(assignment?.title.orEmpty()) }
    var titleError by remember { mutableStateOf(false) }

    // Inisialisasi kalender batas waktu (default: besok jam 23:59 jika tambah baru)
    val initialCalendar = remember(assignment) {
        Calendar.getInstance().apply {
            if (assignment != null) {
                timeInMillis = assignment.deadline
            } else {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        }
    }

    var selectedDeadlineMillis by remember { mutableLongStateOf(initialCalendar.timeInMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")) }
    val timeFormat = remember { SimpleDateFormat("HH:mm 'WIB'", Locale.getDefault()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = if (assignment == null) "Tambah Tugas Baru" else "Edit Tugas Kuliah",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (titleError && it.isNotBlank()) titleError = false
                },
                label = { Text("Nama / Judul Tugas") },
                placeholder = { Text("Misal: Makalah Metodologi Penelitian") },
                isError = titleError,
                supportingText = {
                    if (titleError) {
                        Text("Judul tugas tidak boleh kosong", color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tenggat Waktu (Deadline)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Pemilih Tanggal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tanggal",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dateFormat.format(Date(selectedDeadlineMillis)),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pemilih Jam
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePicker = true },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Jam",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = timeFormat.format(Date(selectedDeadlineMillis)),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Batal")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            titleError = true
                        } else {
                            onSave(title.trim(), selectedDeadlineMillis)
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (assignment == null) "Tambah Tugas" else "Simpan Perubahan")
                }
            }
        }
    }

    // Dialog Pemilih Tanggal (Material 3 DatePicker)
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDeadlineMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val pickedDateMillis = datePickerState.selectedDateMillis
                        if (pickedDateMillis != null) {
                            val pickedCal = Calendar.getInstance().apply { timeInMillis = pickedDateMillis }
                            val currentCal = Calendar.getInstance().apply { timeInMillis = selectedDeadlineMillis }
                            currentCal.set(Calendar.YEAR, pickedCal.get(Calendar.YEAR))
                            currentCal.set(Calendar.MONTH, pickedCal.get(Calendar.MONTH))
                            currentCal.set(Calendar.DAY_OF_MONTH, pickedCal.get(Calendar.DAY_OF_MONTH))
                            selectedDeadlineMillis = currentCal.timeInMillis
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Dialog Pemilih Jam (Material 3 TimePicker)
    if (showTimePicker) {
        val currentCal = remember(selectedDeadlineMillis) {
            Calendar.getInstance().apply { timeInMillis = selectedDeadlineMillis }
        }
        ReminderTimePickerDialog(
            initialHour = currentCal.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentCal.get(Calendar.MINUTE),
            onConfirm = { hour, minute ->
                val cal = Calendar.getInstance().apply { timeInMillis = selectedDeadlineMillis }
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                selectedDeadlineMillis = cal.timeInMillis
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}
