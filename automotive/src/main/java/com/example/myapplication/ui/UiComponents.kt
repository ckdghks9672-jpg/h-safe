package com.example.myapplication.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.shared.model.InsuranceCompany
import com.example.myapplication.ui.theme.HSafeColors

@Composable
fun CabinScreen(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HSafeColors.CabinGray)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

@Composable
fun ZoneCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    accent: Color = HSafeColors.HyundaiBlue,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = HSafeColors.SoftCard,
        border = BorderStroke(1.dp, HSafeColors.Line)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (title != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .width(5.dp)
                            .height(22.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(accent)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(title, style = MaterialTheme.typography.titleMedium, color = HSafeColors.Ink)
                }
            }
            content()
        }
    }
}

@Composable
fun BigAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    container: Color = HSafeColors.HyundaiBlue,
    content: Color = Color.White
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(76.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = HSafeColors.Line,
            disabledContentColor = HSafeColors.Muted
        )
    ) {
        Text(text, fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GhostAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp)
    ) { Text(text, fontWeight = FontWeight.SemiBold, fontSize = 20.sp) }
}

fun insurerLogoRes(id: String): Int = when (id) {
    "samsungfire" -> R.drawable.logo_samsungfire
    "hyundai" -> R.drawable.logo_hyundai
    "db" -> R.drawable.logo_db
    "kb" -> R.drawable.logo_kb
    "meritz" -> R.drawable.logo_meritz
    "hanwha" -> R.drawable.logo_hanwha
    "lotte" -> R.drawable.logo_lotte
    "heungkuk" -> R.drawable.logo_heungkuk
    "axa" -> R.drawable.logo_axa
    "hana" -> R.drawable.logo_hana
    "carrot" -> R.drawable.logo_carrot
    "nh" -> R.drawable.logo_nh
    "yebyeol" -> R.drawable.logo_yebyeol
    "aig" -> R.drawable.logo_aig
    "kakao" -> R.drawable.logo_kakao
    else -> R.drawable.logo_custom
}

@Composable
fun InsurerLogoBadge(company: InsuranceCompany, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(148.dp)
            .clip(shape)
            .clickable(onClick = onClick)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) HSafeColors.HyundaiBlue else HSafeColors.Line,
                shape = shape
            )
            .background(Color.White)
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(insurerLogoRes(company.id)),
            contentDescription = company.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(104.dp).clip(RoundedCornerShape(16.dp))
        )
        Text(
            company.shortLabel,
            fontSize = 20.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

/** 홈용: AAOS 네이티브처럼 좌측 액센트 + 큰 아이콘/타이포 */
@Composable
fun ModeTile(
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(20.dp),
        color = HSafeColors.SoftCard,
        border = BorderStroke(1.dp, HSafeColors.Line),
        shadowElevation = 0.dp
    ) {
        Row(
            Modifier.fillMaxSize().padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Box(
                Modifier
                    .padding(start = 18.dp, end = 16.dp)
                    .size(120.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) { icon() }
            Column(
                Modifier.weight(1f).padding(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(title, color = HSafeColors.Ink, fontSize = 46.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(subtitle, color = HSafeColors.Muted, fontSize = 24.sp, lineHeight = 32.sp)
            }
        }
    }
}

@Composable
fun KindTile(
    title: String,
    subtitle: String,
    selected: Boolean,
    recommended: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    val bg = when {
        selected -> accent.copy(alpha = 0.12f)
        recommended -> HSafeColors.SafetyYellow.copy(alpha = 0.28f)
        else -> HSafeColors.SoftCard
    }
    val border = when {
        selected -> accent
        recommended -> HSafeColors.SafetyAmber
        else -> HSafeColors.Line
    }
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 108.dp),
        shape = RoundedCornerShape(20.dp),
        color = bg,
        border = BorderStroke(if (selected || recommended) 2.dp else 1.dp, border)
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) { icon() }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = HSafeColors.Ink, maxLines = 1)
                    if (recommended) {
                        Text(
                            "센서",
                            color = HSafeColors.Ink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(HSafeColors.SafetyYellow)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(subtitle, color = HSafeColors.Muted, fontSize = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

/** 센서 보안/상태 경고 — 글자 코드 대신 AAOS 픽토그램 카드 */
enum class SensorAlertKind {
    HOOD, TRUNK, CHARGE, WINDOW, AIRBAG, COLLISION, FIRE, BATTERY, OVERHEAT
}

data class SensorAlert(
    val kind: SensorAlertKind,
    val title: String,
    val accent: Color
)

fun parseSensorAlerts(telltales: String, suspected: List<com.example.myapplication.shared.model.AccidentKind>): List<SensorAlert> {
    val text = telltales.lowercase()
    val out = linkedMapOf<SensorAlertKind, SensorAlert>()
    fun add(kind: SensorAlertKind, title: String, accent: Color) {
        out.putIfAbsent(kind, SensorAlert(kind, title, accent))
    }
    if (listOf("hood", "bonnet", "후드", "본넷").any { text.contains(it) }) {
        add(SensorAlertKind.HOOD, "후드 열림", HSafeColors.SafetyAmber)
    }
    if (listOf("trunk", "tailgate", "트렁크", "테일").any { text.contains(it) }) {
        add(SensorAlertKind.TRUNK, "트렁크 열림", HSafeColors.SafetyAmber)
    }
    if (listOf("window", "glass", "윈도우", "창문").any { text.contains(it) }) {
        add(SensorAlertKind.WINDOW, "창문 열림", HSafeColors.HyundaiSky)
    }
    if (listOf("charge", "charging", "port", "충전").any { text.contains(it) }) {
        add(SensorAlertKind.CHARGE, "충전구", HSafeColors.HyundaiSky)
    }
    if (listOf("airbag", "에어백").any { text.contains(it) } ||
        suspected.any { it == com.example.myapplication.shared.model.AccidentKind.REAR_END || it == com.example.myapplication.shared.model.AccidentKind.SIDE_CONTACT || it == com.example.myapplication.shared.model.AccidentKind.HEAD_ON }
    ) {
        add(SensorAlertKind.AIRBAG, "에어백", HSafeColors.EmergencyRed)
        add(SensorAlertKind.COLLISION, "충돌 감지", HSafeColors.EmergencyRed)
    }
    if (listOf("fire", "smoke", "화재", "연기").any { text.contains(it) } ||
        suspected.any { it == com.example.myapplication.shared.model.AccidentKind.VEHICLE_FIRE || it == com.example.myapplication.shared.model.AccidentKind.SMOKE }
    ) {
        add(SensorAlertKind.FIRE, "화재 경고", HSafeColors.FireOrange)
    }
    if (listOf("battery", "고전압", "ev").any { text.contains(it) } ||
        suspected.contains(com.example.myapplication.shared.model.AccidentKind.EV_BATTERY_FIRE)
    ) {
        add(SensorAlertKind.BATTERY, "배터리", HSafeColors.FireOrange)
    }
    if (listOf("overheat", "temp", "과열").any { text.contains(it) } ||
        suspected.contains(com.example.myapplication.shared.model.AccidentKind.ENGINE_OVERHEAT)
    ) {
        add(SensorAlertKind.OVERHEAT, "과열", HSafeColors.FireOrange)
    }
    return out.values.toList()
}

@Composable
fun SensorWarningStrip(alerts: List<SensorAlert>, modifier: Modifier = Modifier) {
    if (alerts.isEmpty()) {
        Text("센서 경고 없음 · 유형을 직접 선택하세요", color = HSafeColors.Muted, fontSize = 22.sp)
        return
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        alerts.take(4).forEach { alert ->
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = alert.accent.copy(alpha = 0.12f),
                border = BorderStroke(2.dp, alert.accent)
            ) {
                Column(
                    Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        when (alert.kind) {
                            SensorAlertKind.HOOD -> IconHoodOpen(alert.accent, 44.dp)
                            SensorAlertKind.TRUNK -> IconTrunkOpen(alert.accent, 44.dp)
                            SensorAlertKind.CHARGE -> IconChargePort(alert.accent, 44.dp)
                            SensorAlertKind.WINDOW -> IconWindowDown(alert.accent, 44.dp)
                            SensorAlertKind.AIRBAG -> IconAirbag(alert.accent, 44.dp)
                            SensorAlertKind.COLLISION -> IconRearEnd(alert.accent, 44.dp)
                            SensorAlertKind.FIRE -> IconFire(alert.accent, 44.dp)
                            SensorAlertKind.BATTERY -> IconBatteryFire(alert.accent, 44.dp)
                            SensorAlertKind.OVERHEAT -> IconOverheat(alert.accent, 44.dp)
                        }
                    }
                    Text(alert.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HSafeColors.Ink, maxLines = 1)
                }
            }
        }
    }
}
