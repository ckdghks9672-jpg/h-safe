package com.example.myapplication.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private fun DrawScope.sw(sizePx: Float) =
    Stroke(width = sizePx * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)

/** 측면 차체 실루엣 (AAOS 픽토그램 톤) */
private fun DrawScope.drawCarSide(
    color: Color,
    stroke: Stroke,
    left: Float,
    top: Float,
    w: Float,
    h: Float,
    hoodOpen: Boolean = false,
    trunkOpen: Boolean = false
) {
    val body = Path().apply {
        moveTo(left + w * 0.08f, top + h * 0.62f)
        lineTo(left + w * 0.18f, top + h * 0.62f)
        if (hoodOpen) {
            lineTo(left + w * 0.22f, top + h * 0.38f)
            lineTo(left + w * 0.34f, top + h * 0.28f)
        } else {
            lineTo(left + w * 0.28f, top + h * 0.38f)
            lineTo(left + w * 0.40f, top + h * 0.28f)
        }
        lineTo(left + w * 0.68f, top + h * 0.28f)
        if (trunkOpen) {
            lineTo(left + w * 0.78f, top + h * 0.18f)
            lineTo(left + w * 0.90f, top + h * 0.30f)
        } else {
            lineTo(left + w * 0.82f, top + h * 0.38f)
        }
        lineTo(left + w * 0.92f, top + h * 0.62f)
        lineTo(left + w * 0.08f, top + h * 0.62f)
        close()
    }
    drawPath(body, color, style = stroke)
    // wheels
    drawCircle(color, radius = h * 0.14f, center = Offset(left + w * 0.28f, top + h * 0.78f), style = stroke)
    drawCircle(color, radius = h * 0.14f, center = Offset(left + w * 0.72f, top + h * 0.78f), style = stroke)
}

private fun DrawScope.drawCurvedUpArrow(color: Color, stroke: Stroke, cx: Float, cy: Float, r: Float) {
    val path = Path().apply {
        moveTo(cx - r * 0.55f, cy + r * 0.25f)
        quadraticTo(cx - r * 0.1f, cy - r * 0.85f, cx + r * 0.65f, cy - r * 0.15f)
    }
    drawPath(path, color, style = stroke)
    // arrow head
    drawLine(color, Offset(cx + r * 0.35f, cy - r * 0.45f), Offset(cx + r * 0.65f, cy - r * 0.15f), strokeWidth = stroke.width)
    drawLine(color, Offset(cx + r * 0.65f, cy - r * 0.55f), Offset(cx + r * 0.65f, cy - r * 0.15f), strokeWidth = stroke.width)
}

@Composable
fun IconHoodOpen(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCarSide(color, s, w * 0.06f, w * 0.08f, w * 0.88f, w * 0.82f, hoodOpen = true)
        drawCurvedUpArrow(color, s, w * 0.34f, w * 0.22f, w * 0.22f)
    }
}

@Composable
fun IconTrunkOpen(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCarSide(color, s, w * 0.06f, w * 0.08f, w * 0.88f, w * 0.82f, trunkOpen = true)
        drawCurvedUpArrow(color, s, w * 0.78f, w * 0.18f, w * 0.2f)
    }
}

@Composable
fun IconChargePort(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawRoundRect(
            color, Offset(w * 0.18f, w * 0.22f), Size(w * 0.44f, w * 0.56f),
            CornerRadius(w * 0.08f, w * 0.08f), style = s
        )
        // lightning
        val bolt = Path().apply {
            moveTo(w * 0.42f, w * 0.30f)
            lineTo(w * 0.30f, w * 0.50f)
            lineTo(w * 0.40f, w * 0.50f)
            lineTo(w * 0.32f, w * 0.70f)
            lineTo(w * 0.48f, w * 0.46f)
            lineTo(w * 0.38f, w * 0.46f)
            close()
        }
        drawPath(bolt, color)
        drawCurvedUpArrow(color, s, w * 0.78f, w * 0.42f, w * 0.2f)
    }
}

@Composable
fun IconWindowDown(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        // door frame
        drawRoundRect(
            color, Offset(w * 0.16f, w * 0.18f), Size(w * 0.48f, w * 0.64f),
            CornerRadius(w * 0.06f, w * 0.06f), style = s
        )
        // glass lowered
        drawLine(color, Offset(w * 0.22f, w * 0.42f), Offset(w * 0.58f, w * 0.42f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.22f, w * 0.42f), Offset(w * 0.22f, w * 0.72f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.58f, w * 0.42f), Offset(w * 0.58f, w * 0.72f), strokeWidth = s.width)
        // down arrow
        drawLine(color, Offset(w * 0.78f, w * 0.28f), Offset(w * 0.78f, w * 0.68f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.66f, w * 0.54f), Offset(w * 0.78f, w * 0.68f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.90f, w * 0.54f), Offset(w * 0.78f, w * 0.68f), strokeWidth = s.width)
    }
}

@Composable
fun IconAirbag(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCarSide(color, s, w * 0.05f, w * 0.18f, w * 0.7f, w * 0.7f)
        // airbag burst
        drawCircle(color, radius = w * 0.16f, center = Offset(w * 0.78f, w * 0.38f), style = s)
        drawCircle(color, radius = w * 0.08f, center = Offset(w * 0.78f, w * 0.38f))
    }
}

@Composable
fun IconRearEnd(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        // front car
        drawCarSide(color, s, w * 0.02f, w * 0.18f, w * 0.52f, w * 0.62f)
        // rear car impacting
        drawCarSide(color, s, w * 0.42f, w * 0.28f, w * 0.52f, w * 0.62f)
        // impact marks
        drawLine(color, Offset(w * 0.48f, w * 0.22f), Offset(w * 0.58f, w * 0.12f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.50f, w * 0.30f), Offset(w * 0.62f, w * 0.28f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.48f, w * 0.40f), Offset(w * 0.58f, w * 0.48f), strokeWidth = s.width)
    }
}

@Composable
fun IconSideContact(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCarSide(color, s, w * 0.08f, w * 0.08f, w * 0.84f, w * 0.55f)
        // side impact arrow from below-left
        drawLine(color, Offset(w * 0.18f, w * 0.88f), Offset(w * 0.42f, w * 0.62f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.30f, w * 0.66f), Offset(w * 0.42f, w * 0.62f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.38f, w * 0.74f), Offset(w * 0.42f, w * 0.62f), strokeWidth = s.width)
    }
}

@Composable
fun IconHeadOn(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        // left facing right
        drawCarSide(color, s, w * 0.02f, w * 0.22f, w * 0.42f, w * 0.58f)
        // mirrored right car (simple body facing left)
        val right = Path().apply {
            val l = w * 0.56f; val t = w * 0.22f; val bw = w * 0.42f; val bh = w * 0.58f
            moveTo(l + bw * 0.92f, t + bh * 0.62f)
            lineTo(l + bw * 0.82f, t + bh * 0.62f)
            lineTo(l + bw * 0.72f, t + bh * 0.38f)
            lineTo(l + bw * 0.60f, t + bh * 0.28f)
            lineTo(l + bw * 0.32f, t + bh * 0.28f)
            lineTo(l + bw * 0.18f, t + bh * 0.38f)
            lineTo(l + bw * 0.08f, t + bh * 0.62f)
            close()
        }
        drawPath(right, color, style = s)
        drawCircle(color, radius = w * 0.07f, center = Offset(w * 0.68f, w * 0.72f), style = s)
        drawCircle(color, radius = w * 0.07f, center = Offset(w * 0.86f, w * 0.72f), style = s)
        drawLine(color, Offset(w * 0.48f, w * 0.40f), Offset(w * 0.52f, w * 0.40f), strokeWidth = s.width)
    }
}

@Composable
fun IconSingleVehicle(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCarSide(color, s, w * 0.05f, w * 0.22f, w * 0.7f, w * 0.62f)
        // barrier
        drawLine(color, Offset(w * 0.78f, w * 0.18f), Offset(w * 0.78f, w * 0.82f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.78f, w * 0.30f), Offset(w * 0.92f, w * 0.22f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.78f, w * 0.50f), Offset(w * 0.92f, w * 0.42f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.78f, w * 0.70f), Offset(w * 0.92f, w * 0.62f), strokeWidth = s.width)
    }
}

@Composable
fun IconPropertyDamage(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawRoundRect(color, Offset(w * 0.18f, w * 0.22f), Size(w * 0.64f, w * 0.56f), CornerRadius(8f, 8f), style = s)
        drawLine(color, Offset(w * 0.28f, w * 0.22f), Offset(w * 0.42f, w * 0.78f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.58f, w * 0.22f), Offset(w * 0.72f, w * 0.78f), strokeWidth = s.width)
    }
}

@Composable
fun IconPersonInjury(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCircle(color, radius = w * 0.12f, center = Offset(w * 0.5f, w * 0.22f), style = s)
        drawLine(color, Offset(w * 0.5f, w * 0.34f), Offset(w * 0.5f, w * 0.58f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.32f, w * 0.44f), Offset(w * 0.68f, w * 0.44f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.5f, w * 0.58f), Offset(w * 0.34f, w * 0.82f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.5f, w * 0.58f), Offset(w * 0.66f, w * 0.82f), strokeWidth = s.width)
    }
}

@Composable
fun IconHitAndRun(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCarSide(color, s, w * 0.05f, w * 0.28f, w * 0.55f, w * 0.55f)
        // dashed flee arrows
        drawLine(color, Offset(w * 0.62f, w * 0.42f), Offset(w * 0.92f, w * 0.28f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.62f, w * 0.58f), Offset(w * 0.92f, w * 0.58f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.62f, w * 0.74f), Offset(w * 0.92f, w * 0.88f), strokeWidth = s.width)
    }
}

@Composable
fun IconUnconscious(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCircle(color, radius = w * 0.16f, center = Offset(w * 0.38f, w * 0.32f), style = s)
        // reclined body
        drawLine(color, Offset(w * 0.38f, w * 0.48f), Offset(w * 0.72f, w * 0.62f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.50f, w * 0.54f), Offset(w * 0.42f, w * 0.78f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.58f, w * 0.58f), Offset(w * 0.78f, w * 0.78f), strokeWidth = s.width)
        // Zzz
        drawLine(color, Offset(w * 0.62f, w * 0.18f), Offset(w * 0.78f, w * 0.18f), strokeWidth = s.width * 0.8f)
        drawLine(color, Offset(w * 0.78f, w * 0.18f), Offset(w * 0.62f, w * 0.30f), strokeWidth = s.width * 0.8f)
        drawLine(color, Offset(w * 0.62f, w * 0.30f), Offset(w * 0.78f, w * 0.30f), strokeWidth = s.width * 0.8f)
    }
}

@Composable
fun IconBreathing(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCircle(color, radius = w * 0.14f, center = Offset(w * 0.32f, w * 0.28f), style = s)
        drawLine(color, Offset(w * 0.32f, w * 0.42f), Offset(w * 0.32f, w * 0.72f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.32f, w * 0.52f), Offset(w * 0.18f, w * 0.62f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.32f, w * 0.52f), Offset(w * 0.48f, w * 0.62f), strokeWidth = s.width)
        // breath waves
        drawPath(Path().apply {
            moveTo(w * 0.55f, w * 0.30f)
            quadraticTo(w * 0.68f, w * 0.18f, w * 0.82f, w * 0.30f)
            quadraticTo(w * 0.90f, w * 0.38f, w * 0.78f, w * 0.46f)
        }, color, style = s)
    }
}

@Composable
fun IconBleeding(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCircle(color, radius = w * 0.14f, center = Offset(w * 0.5f, w * 0.24f), style = s)
        drawLine(color, Offset(w * 0.5f, w * 0.38f), Offset(w * 0.5f, w * 0.62f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.34f, w * 0.48f), Offset(w * 0.66f, w * 0.48f), strokeWidth = s.width)
        // drop
        val drop = Path().apply {
            moveTo(w * 0.72f, w * 0.42f)
            cubicTo(w * 0.86f, w * 0.58f, w * 0.86f, w * 0.78f, w * 0.72f, w * 0.86f)
            cubicTo(w * 0.58f, w * 0.78f, w * 0.58f, w * 0.58f, w * 0.72f, w * 0.42f)
            close()
        }
        drawPath(drop, color, style = s)
    }
}

@Composable
fun IconEntrapment(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCarSide(color, s, w * 0.05f, w * 0.2f, w * 0.9f, w * 0.65f)
        // crush marks on cabin
        drawLine(color, Offset(w * 0.42f, w * 0.18f), Offset(w * 0.48f, w * 0.42f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.58f, w * 0.18f), Offset(w * 0.52f, w * 0.42f), strokeWidth = s.width)
    }
}

@Composable
fun IconChestPain(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        val heart = Path().apply {
            moveTo(w * 0.5f, w * 0.72f)
            cubicTo(w * 0.18f, w * 0.52f, w * 0.18f, w * 0.22f, w * 0.5f, w * 0.36f)
            cubicTo(w * 0.82f, w * 0.22f, w * 0.82f, w * 0.52f, w * 0.5f, w * 0.72f)
            close()
        }
        drawPath(heart, color, style = s)
        drawLine(color, Offset(w * 0.32f, w * 0.48f), Offset(w * 0.42f, w * 0.48f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.42f, w * 0.48f), Offset(w * 0.48f, w * 0.38f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.48f, w * 0.38f), Offset(w * 0.56f, w * 0.58f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.56f, w * 0.58f), Offset(w * 0.68f, w * 0.48f), strokeWidth = s.width)
    }
}

@Composable
fun IconMultiCasualty(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        fun person(cx: Float, scale: Float) {
            drawCircle(color, radius = w * 0.08f * scale, center = Offset(cx, w * 0.28f), style = s)
            drawLine(color, Offset(cx, w * 0.36f), Offset(cx, w * 0.58f), strokeWidth = s.width)
            drawLine(color, Offset(cx - w * 0.12f * scale, w * 0.44f), Offset(cx + w * 0.12f * scale, w * 0.44f), strokeWidth = s.width)
            drawLine(color, Offset(cx, w * 0.58f), Offset(cx - w * 0.1f * scale, w * 0.78f), strokeWidth = s.width)
            drawLine(color, Offset(cx, w * 0.58f), Offset(cx + w * 0.1f * scale, w * 0.78f), strokeWidth = s.width)
        }
        person(w * 0.28f, 1f)
        person(w * 0.5f, 1f)
        person(w * 0.72f, 1f)
    }
}

@Composable
fun IconFire(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        val flame = Path().apply {
            moveTo(w * 0.5f, w * 0.12f)
            cubicTo(w * 0.72f, w * 0.32f, w * 0.78f, w * 0.48f, w * 0.68f, w * 0.62f)
            cubicTo(w * 0.62f, w * 0.72f, w * 0.55f, w * 0.78f, w * 0.5f, w * 0.88f)
            cubicTo(w * 0.45f, w * 0.78f, w * 0.38f, w * 0.72f, w * 0.32f, w * 0.62f)
            cubicTo(w * 0.22f, w * 0.48f, w * 0.28f, w * 0.32f, w * 0.5f, w * 0.12f)
            close()
        }
        drawPath(flame, color, style = s)
    }
}

@Composable
fun IconSmoke(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCarSide(color, s, w * 0.08f, w * 0.38f, w * 0.84f, w * 0.55f)
        drawCircle(color, radius = w * 0.1f, center = Offset(w * 0.35f, w * 0.28f), style = s)
        drawCircle(color, radius = w * 0.13f, center = Offset(w * 0.52f, w * 0.22f), style = s)
        drawCircle(color, radius = w * 0.09f, center = Offset(w * 0.68f, w * 0.28f), style = s)
    }
}

@Composable
fun IconBatteryFire(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawRoundRect(color, Offset(w * 0.18f, w * 0.30f), Size(w * 0.54f, w * 0.42f), CornerRadius(10f, 10f), style = s)
        drawLine(color, Offset(w * 0.32f, w * 0.22f), Offset(w * 0.32f, w * 0.30f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.58f, w * 0.22f), Offset(w * 0.58f, w * 0.30f), strokeWidth = s.width)
        val bolt = Path().apply {
            moveTo(w * 0.48f, w * 0.36f)
            lineTo(w * 0.36f, w * 0.52f)
            lineTo(w * 0.46f, w * 0.52f)
            lineTo(w * 0.38f, w * 0.66f)
            lineTo(w * 0.56f, w * 0.48f)
            lineTo(w * 0.46f, w * 0.48f)
            close()
        }
        drawPath(bolt, color)
        drawLine(color, Offset(w * 0.78f, w * 0.28f), Offset(w * 0.88f, w * 0.18f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.78f, w * 0.42f), Offset(w * 0.92f, w * 0.42f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.78f, w * 0.56f), Offset(w * 0.88f, w * 0.66f), strokeWidth = s.width)
    }
}

@Composable
fun IconFuelLeak(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCarSide(color, s, w * 0.05f, w * 0.12f, w * 0.8f, w * 0.6f)
        val drop = Path().apply {
            moveTo(w * 0.78f, w * 0.55f)
            cubicTo(w * 0.92f, w * 0.68f, w * 0.92f, w * 0.86f, w * 0.78f, w * 0.92f)
            cubicTo(w * 0.64f, w * 0.86f, w * 0.64f, w * 0.68f, w * 0.78f, w * 0.55f)
            close()
        }
        drawPath(drop, color, style = s)
    }
}

@Composable
fun IconOverheat(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCarSide(color, s, w * 0.05f, w * 0.32f, w * 0.9f, w * 0.58f, hoodOpen = true)
        drawLine(color, Offset(w * 0.28f, w * 0.12f), Offset(w * 0.28f, w * 0.28f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.40f, w * 0.08f), Offset(w * 0.40f, w * 0.26f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.52f, w * 0.12f), Offset(w * 0.52f, w * 0.28f), strokeWidth = s.width)
    }
}

@Composable
fun IconCollision(color: Color, size: Dp = 48.dp) = IconRearEnd(color, size)

@Composable
fun IconAmbulance(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawRoundRect(color, Offset(w * 0.1f, w * 0.32f), Size(w * 0.8f, w * 0.36f), CornerRadius(10f, 10f), style = s)
        drawLine(color, Offset(w * 0.5f, w * 0.40f), Offset(w * 0.5f, w * 0.60f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.38f, w * 0.50f), Offset(w * 0.62f, w * 0.50f), strokeWidth = s.width)
        drawCircle(color, radius = w * 0.08f, center = Offset(w * 0.28f, w * 0.78f), style = s)
        drawCircle(color, radius = w * 0.08f, center = Offset(w * 0.72f, w * 0.78f), style = s)
    }
}

@Composable
fun IconBreakdown(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        val tri = Path().apply {
            moveTo(w * 0.5f, w * 0.12f)
            lineTo(w * 0.88f, w * 0.82f)
            lineTo(w * 0.12f, w * 0.82f)
            close()
        }
        drawPath(tri, color, style = s)
        drawLine(color, Offset(w * 0.5f, w * 0.36f), Offset(w * 0.5f, w * 0.56f), strokeWidth = s.width)
        drawCircle(color, radius = s.width * 0.7f, center = Offset(w * 0.5f, w * 0.68f))
    }
}

@Composable
fun IconOther(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        val c = Offset(w / 2f, w / 2f)
        drawCircle(color, radius = w * 0.34f, center = c, style = s)
        drawCircle(color, radius = w * 0.05f, center = Offset(c.x, c.y + w * 0.16f))
        drawLine(color, Offset(c.x, c.y - w * 0.14f), Offset(c.x, c.y + w * 0.02f), strokeWidth = s.width)
    }
}

@Composable
fun IconSensor(color: Color, size: Dp = 32.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = Stroke(width = w * 0.1f, cap = StrokeCap.Round)
        val c = Offset(w / 2f, w / 2f)
        drawCircle(color, radius = w * 0.12f, center = c)
        drawCircle(color, radius = w * 0.28f, center = c, style = s)
        drawCircle(color, radius = w * 0.42f, center = c, style = s)
    }
}

@Composable
fun IconTire(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        val c = Offset(w / 2f, w / 2f)
        drawCircle(color, radius = w * 0.32f, center = c, style = s)
        drawCircle(color, radius = w * 0.12f, center = c, style = s)
    }
}
