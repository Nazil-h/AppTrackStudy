package com.apptrackstudy.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private val ConfettiColors = listOf(
    Color(0xFFFFD700), // Gold
    Color(0xFF10B981), // Emerald
    Color(0xFF6366F1), // Indigo
    Color(0xFFF43F5E), // Rose
    Color(0xFF06B6D4), // Cyan
    Color(0xFFFF7A59), // Coral
    Color(0xFF8B5CF6), // Violet
    Color(0xFFFBBF24)  // Amber
)

private enum class ParticleShape {
    RECTANGLE, CIRCLE, STRIP
}

private class ConfettiParticle(
    val initialX: Float, // 0..1 fraction of screen width
    val initialY: Float, // 0..1 fraction of screen height
    val vx: Float,
    val vy: Float,
    val gravity: Float,
    val size: Float,
    val aspectRatio: Float,
    val color: Color,
    val shape: ParticleShape,
    val rotationSpeed: Float,
    val wobbleSpeed: Float
)

/**
 * Efek partikel konfeti halus berbasis Canvas Compose.
 * Menyembur dan melayang dengan rotasi & efek gravitasi saat perayaan 100%.
 */
@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    durationMillis: Int = 3500,
    particleCount: Int = 75,
    onAnimationEnd: (() -> Unit)? = null
) {
    val progress = remember { Animatable(0f) }

    val particles = remember {
        List(particleCount) {
            val startFromCenter = Random.nextBoolean()
            ConfettiParticle(
                initialX = if (startFromCenter) Random.nextFloat() * 0.4f + 0.3f else Random.nextFloat(),
                initialY = if (startFromCenter) Random.nextFloat() * 0.2f + 0.15f else Random.nextFloat() * 0.15f,
                vx = (Random.nextFloat() - 0.5f) * 700f,
                vy = -Random.nextFloat() * 450f - 150f,
                gravity = Random.nextFloat() * 350f + 650f,
                size = Random.nextFloat() * 12f + 14f,
                aspectRatio = if (Random.nextBoolean()) 0.4f else 0.85f,
                color = ConfettiColors.random(),
                shape = ParticleShape.entries.random(),
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                wobbleSpeed = Random.nextFloat() * 8f + 4f
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            )
        )
        onAnimationEnd?.invoke()
    }

    val currentProgress = progress.value
    // Efek memudar menjelang akhir animasi
    val alpha = if (currentProgress > 0.75f) {
        ((1f - currentProgress) / 0.25f).coerceIn(0f, 1f)
    } else {
        1f
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val t = currentProgress * (durationMillis / 1000f)

        particles.forEach { p ->
            val x = (p.initialX * width) + (p.vx * t) + (kotlin.math.sin(t * p.wobbleSpeed) * 35f)
            val y = (p.initialY * height) + (p.vy * t) + (0.5f * p.gravity * t * t)

            // Render hanya jika masih berada dalam area layar
            if (y in -50f..(height + 50f) && x in -50f..(width + 50f)) {
                val currentRotation = p.rotationSpeed * t
                val particleColor = p.color.copy(alpha = alpha)

                rotate(degrees = currentRotation, pivot = Offset(x, y)) {
                    drawParticle(
                        shape = p.shape,
                        center = Offset(x, y),
                        size = p.size,
                        aspectRatio = p.aspectRatio,
                        color = particleColor
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawParticle(
    shape: ParticleShape,
    center: Offset,
    size: Float,
    aspectRatio: Float,
    color: Color
) {
    when (shape) {
        ParticleShape.CIRCLE -> {
            drawCircle(
                color = color,
                radius = size / 2,
                center = center
            )
        }
        ParticleShape.RECTANGLE -> {
            val width = size
            val height = size * aspectRatio
            drawRect(
                color = color,
                topLeft = Offset(center.x - width / 2, center.y - height / 2),
                size = Size(width, height)
            )
        }
        ParticleShape.STRIP -> {
            val width = size * 0.35f
            val height = size * 1.5f
            drawRoundRect(
                color = color,
                topLeft = Offset(center.x - width / 2, center.y - height / 2),
                size = Size(width, height)
            )
        }
    }
}
