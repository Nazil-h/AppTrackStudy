package com.apptrackstudy.ui.profile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apptrackstudy.data.preferences.UserPreferencesRepository

/**
 * Dialog pemilihan mode tema aplikasi (Sistem / Terang / Gelap).
 */
@Composable
fun ThemeSelectionDialog(
    currentTheme: String,
    onSelectTheme: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val themeOptions = listOf(
        ThemeOption(
            id = UserPreferencesRepository.THEME_SYSTEM,
            title = "Ikuti Sistem",
            description = "Menyesuaikan dengan pengaturan perangkat",
            icon = Icons.Default.BrightnessAuto
        ),
        ThemeOption(
            id = UserPreferencesRepository.THEME_LIGHT,
            title = "Mode Terang",
            description = "Tampilan bersih & cerah",
            icon = Icons.Default.LightMode
        ),
        ThemeOption(
            id = UserPreferencesRepository.THEME_DARK,
            title = "Mode Gelap",
            description = "Nyaman untuk mata di malam hari",
            icon = Icons.Default.DarkMode
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Pilih Mode Tema",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                themeOptions.forEach { option ->
                    val isSelected = currentTheme == option.id

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectTheme(option.id) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectTheme(option.id) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = option.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = option.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

private data class ThemeOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)
