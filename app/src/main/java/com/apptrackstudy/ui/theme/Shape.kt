package com.apptrackstudy.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Konfigurasi bentuk kurva sudut (Shapes) Jejak Ilmu:
 * Kartu berlekuk lembut 20-24dp, tombol pill, dan dialog melengkung ramah.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

val PillShape = RoundedCornerShape(100.dp)
