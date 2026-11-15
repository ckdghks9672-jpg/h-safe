package com.example.myapplication.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

/** 제공받은 라인 픽토그램(흰색 알파) + tint */
@Composable
private fun PictoIcon(
    @DrawableRes resId: Int,
    color: Color,
    size: Dp,
    contentDescription: String? = null
) {
    Image(
        painter = painterResource(resId),
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(color)
    )
}

/* ---------- 사고 유형 / 모드 (이미지 픽토) ---------- */

@Composable
fun IconSingleVehicle(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_single_vehicle, color, size)

@Composable
fun IconPropertyDamage(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_property_damage, color, size)

@Composable
fun IconPersonInjury(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_person_injury, color, size)

@Composable
fun IconHitAndRun(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_hit_and_run, color, size)

@Composable
fun IconOther(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_other, color, size)

@Composable
fun IconUnconscious(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_unconscious, color, size)

@Composable
fun IconBreathing(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_breathing, color, size)

@Composable
fun IconAmbulance(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_ambulance, color, size)

@Composable
fun IconFire(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_fire, color, size)

@Composable
fun IconRearEnd(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_rear_end, color, size)

@Composable
fun IconHeadOn(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_head_on, color, size)

@Composable
fun IconSideContact(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_side_contact, color, size)

@Composable
fun IconMultiCasualty(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_multi_casualty, color, size)

@Composable
fun IconBatteryFire(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_battery_fire, color, size)

@Composable
fun IconOverheat(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_overheat, color, size)

@Composable
fun IconSmoke(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_smoke, color, size)

@Composable
fun IconBleeding(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_bleeding, color, size)

@Composable
fun IconFuelLeak(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_fuel_leak, color, size)

@Composable
fun IconChestPain(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_chest_pain, color, size)

@Composable
fun IconEntrapment(color: Color, size: Dp = 56.dp) =
    PictoIcon(R.drawable.picto_entrapment, color, size)

@Composable
fun IconCollision(color: Color, size: Dp = 56.dp) = IconRearEnd(color, size)

/* ---------- 센서 스트립 (아직 이미지 없음 → Canvas 유지) ---------- */

private fun DrawScope.sw(sizePx: Float) =
    Stroke(width = sizePx * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)

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
    drawCircle(color, radius = h * 0.14f, center = Offset(left + w * 0.28f, top + h * 0.78f), style = stroke)
    drawCircle(color, radius = h * 0.14f, center = Offset(left + w * 0.72f, top + h * 0.78f), style = stroke)
}

private fun DrawScope.drawCurvedUpArrow(color: Color, stroke: Stroke, cx: Float, cy: Float, r: Float) {
    val path = Path().apply {
        moveTo(cx - r * 0.55f, cy + r * 0.25f)
        quadraticTo(cx - r * 0.1f, cy - r * 0.85f, cx + r * 0.65f, cy - r * 0.15f)
    }
    drawPath(path, color, style = stroke)
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
        val bolt = Path().apply {
            moveTo(w * 0.42f, w * 0.30f)
            lineTo(w * 0.30f, w * 0.50f)
            lineTo(w * 0.40f, w * 0.50f)
            lineTo(w * 0.34f, w * 0.70f)
            lineTo(w * 0.52f, w * 0.44f)
            lineTo(w * 0.42f, w * 0.44f)
            close()
        }
        drawPath(bolt, color)
        drawCurvedUpArrow(color, s, w * 0.78f, w * 0.36f, w * 0.18f)
    }
}

@Composable
fun IconWindowDown(color: Color, size: Dp = 48.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawRoundRect(
            color, Offset(w * 0.16f, w * 0.18f), Size(w * 0.48f, w * 0.64f),
            CornerRadius(w * 0.06f, w * 0.06f), style = s
        )
        drawLine(color, Offset(w * 0.22f, w * 0.42f), Offset(w * 0.58f, w * 0.42f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.22f, w * 0.42f), Offset(w * 0.22f, w * 0.72f), strokeWidth = s.width)
        drawLine(color, Offset(w * 0.58f, w * 0.42f), Offset(w * 0.58f, w * 0.72f), strokeWidth = s.width)
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
        drawCircle(color, radius = w * 0.16f, center = Offset(w * 0.78f, w * 0.38f), style = s)
        drawCircle(color, radius = w * 0.08f, center = Offset(w * 0.78f, w * 0.38f))
    }
}

@Composable
fun IconBreakdown(color: Color, size: Dp = 56.dp) = IconPropertyDamage(color, size)

@Composable
fun IconSensor(color: Color, size: Dp = 32.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCircle(color, radius = w * 0.28f, center = Offset(w * 0.5f, w * 0.5f), style = s)
        drawCircle(color, radius = w * 0.08f, center = Offset(w * 0.5f, w * 0.5f))
    }
}

@Composable
fun IconTire(color: Color, size: Dp = 56.dp) {
    Canvas(Modifier.size(size)) {
        val w = size.toPx(); val s = sw(w)
        drawCircle(color, radius = w * 0.36f, center = Offset(w * 0.5f, w * 0.5f), style = s)
        drawCircle(color, radius = w * 0.14f, center = Offset(w * 0.5f, w * 0.5f), style = s)
    }
}
