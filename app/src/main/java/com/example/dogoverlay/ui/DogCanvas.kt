package com.example.dogoverlay.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.dogoverlay.DogState
import kotlin.math.sin

// --- PLACEHOLDER PALETTE -----------------------------------------------
// This whole file is a lightweight, dependency-free stand-in renderer:
// the dog is drawn with basic shapes instead of real artwork, so the app
// has zero missing assets out of the box. Swap in sprite-sheet frames or
// a Lottie file for the real look — see README "Replacing the dog art".
private val Coat = Color(0xFFC98A4B)
private val CoatDark = Color(0xFFA9682F)
private val Ear = Color(0xFF8A5A2E)
private val Belly = Color(0xFFF1D9B5)
private val Nose = Color(0xFF3E2723)
private val Shadow = Color(0x33000000)

@Composable
fun DogCanvas(state: DogState, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "dog_transition")

    // Continuous 0..1 walk-cycle phase, ~1.8 strides/sec.
    val walkPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(550, easing = LinearEasing)),
        label = "walkPhase"
    )
    // Slow back-and-forth for idle breathing / tail sway while sitting.
    val idlePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idlePhase"
    )
    // Fast pulse driving the bark's head jerk / mouth flap.
    val barkPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(140, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barkPhase"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Ground contact shadow.
        drawOval(
            color = Shadow,
            topLeft = Offset(w * 0.20f, h * 0.90f),
            size = Size(w * 0.55f, h * 0.08f)
        )

        val sitLift = if (state == DogState.SIT) h * 0.05f else 0f
        val walkBob = if (state == DogState.WALK) {
            sin(walkPhase * 2 * Math.PI).toFloat() * h * 0.02f
        } else 0f
        val breathe = if (state == DogState.SIT) {
            sin(idlePhase * Math.PI).toFloat() * h * 0.01f
        } else 0f
        val bodyLift = sitLift + walkBob + breathe

        // --- Legs ---
        val legTopY = h * 0.62f - bodyLift
        val legBottomY = if (state == DogState.SIT) h * 0.80f - bodyLift else h * 0.90f
        val legW = w * 0.07f
        val walkSwing = if (state == DogState.WALK) {
            sin(walkPhase * 2 * Math.PI).toFloat() * (w * 0.05f)
        } else 0f

        drawRoundRect(
            color = CoatDark,
            topLeft = Offset(w * 0.32f - legW / 2 - walkSwing, legTopY),
            size = Size(legW, legBottomY - legTopY),
            cornerRadius = CornerRadius(legW / 2, legW / 2)
        )
        drawRoundRect(
            color = CoatDark,
            topLeft = Offset(w * 0.62f - legW / 2 + walkSwing, legTopY),
            size = Size(legW, legBottomY - legTopY),
            cornerRadius = CornerRadius(legW / 2, legW / 2)
        )

        // --- Tail ---
        val tailWag = when (state) {
            DogState.WALK -> sin(walkPhase * 4 * Math.PI).toFloat() * 18f
            DogState.BARK -> sin(barkPhase * 2 * Math.PI).toFloat() * 26f
            DogState.SIT -> sin(idlePhase * Math.PI).toFloat() * 8f
        }
        val tailBase = Offset(w * 0.24f, h * 0.52f - bodyLift)
        rotate(degrees = tailWag, pivot = tailBase) {
            drawLine(
                color = CoatDark,
                start = tailBase,
                end = Offset(tailBase.x - w * 0.14f, tailBase.y - h * 0.16f),
                strokeWidth = w * 0.035f,
                cap = StrokeCap.Round
            )
        }

        // --- Body ---
        val bodyTop = h * 0.42f - bodyLift
        val bodyBottom = h * 0.66f - bodyLift
        drawRoundRect(
            color = Coat,
            topLeft = Offset(w * 0.24f, bodyTop),
            size = Size(w * 0.42f, bodyBottom - bodyTop),
            cornerRadius = CornerRadius(w * 0.12f, w * 0.12f)
        )

        // --- Head ---
        val headCx = w * 0.72f
        val headBounce = if (state == DogState.BARK) {
            sin(barkPhase * 2 * Math.PI).toFloat() * h * 0.02f
        } else 0f
        val headCy = h * 0.36f - bodyLift + headBounce
        val headR = w * 0.16f
        val earLift = if (state == DogState.BARK) h * 0.02f else 0f

        drawEarTriangle(headCx - headR * 0.6f, headCy - headR * 0.6f - earLift, headR * 0.9f, Ear)
        drawEarTriangle(headCx + headR * 0.1f, headCy - headR * 0.7f - earLift, headR * 0.9f, Ear)

        drawCircle(color = Coat, radius = headR, center = Offset(headCx, headCy))

        val snoutCx = headCx + headR * 0.85f
        drawOval(
            color = Belly,
            topLeft = Offset(snoutCx - headR * 0.5f, headCy - headR * 0.25f),
            size = Size(headR * 0.85f, headR * 0.55f)
        )

        // Mouth flaps open for the second half of every bark pulse.
        if (state == DogState.BARK && barkPhase > 0.5f) {
            drawOval(
                color = Nose,
                topLeft = Offset(snoutCx - headR * 0.18f, headCy + headR * 0.05f),
                size = Size(headR * 0.36f, headR * 0.26f)
            )
        }

        drawCircle(
            color = Nose,
            radius = headR * 0.12f,
            center = Offset(snoutCx + headR * 0.28f, headCy - headR * 0.02f)
        )
        drawCircle(
            color = Nose,
            radius = headR * 0.09f,
            center = Offset(headCx + headR * 0.05f, headCy - headR * 0.25f)
        )
    }
}

private fun DrawScope.drawEarTriangle(x: Float, y: Float, size: Float, color: Color) {
    val path = Path().apply {
        moveTo(x, y)
        lineTo(x - size * 0.35f, y - size)
        lineTo(x + size * 0.35f, y - size * 0.5f)
        close()
    }
    drawPath(path, color)
}
