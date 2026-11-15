package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.AppUiState
import com.example.myapplication.BuildConfig
import com.example.myapplication.shared.CallSummary
import com.example.myapplication.shared.data.InsuranceCatalog
import com.example.myapplication.shared.model.AccidentKind
import com.example.myapplication.shared.model.BloodType
import com.example.myapplication.shared.model.DriveKind
import com.example.myapplication.shared.model.EmergencyMedicalInfo
import com.example.myapplication.shared.model.FuelKind
import com.example.myapplication.shared.model.ResponseMode
import com.example.myapplication.shared.model.RhType
import com.example.myapplication.shared.model.TriState
import com.example.myapplication.shared.model.UserProfile
import com.example.myapplication.ui.theme.HSafeColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    initial: UserProfile,
    restricted: Boolean,
    vehicleApiHint: String,
    onSave: (UserProfile) -> Unit
) {
    var vehicleNumber by remember { mutableStateOf(initial.vehicleNumber) }
    var vehicleName by remember { mutableStateOf(initial.vehicleName) }
    var vehicleType by remember { mutableStateOf(initial.vehicleType) }
    var vehicleYear by remember { mutableStateOf(initial.vehicleYear) }
    var vehicleVin by remember { mutableStateOf(initial.vehicleVin) }
    var fuelKind by remember { mutableStateOf(initial.fuelKind) }
    var driveKind by remember { mutableStateOf(initial.driveKind) }
    var insurerId by remember { mutableStateOf(initial.insurerId.ifBlank { "samsungfire" }) }
    var customPhone by remember { mutableStateOf(initial.customClaimPhone) }
    var policyNumber by remember { mutableStateOf(initial.policyNumber) }
    var driverName by remember { mutableStateOf(initial.driverName.ifBlank { initial.insuredName }) }
    var driverPhone by remember { mutableStateOf(initial.driverPhone.ifBlank { initial.insuredPhone }) }
    var driverLicenseNumber by remember { mutableStateOf(initial.driverLicenseNumber) }
    var driverBirthDate by remember { mutableStateOf(initial.driverBirthDate) }
    var bloodType by remember { mutableStateOf(initial.medical.bloodType) }
    var rhType by remember { mutableStateOf(initial.medical.rhType) }
    var hypertension by remember { mutableStateOf(initial.medical.hypertension) }
    var diabetes by remember { mutableStateOf(initial.medical.diabetes) }
    var heartDisease by remember { mutableStateOf(initial.medical.heartDisease) }
    var allergyNotes by remember { mutableStateOf(initial.medical.allergyNotes) }
    var medicationNotes by remember { mutableStateOf(initial.medical.medicationNotes) }
    var otherNotes by remember { mutableStateOf(initial.medical.otherNotes) }

    val vinLocked = remember(initial.vehicleVin) {
        val vin = initial.vehicleVin.trim().trim('"')
        vin.length >= 5 && !vin.equals("invalid", true) && !vin.equals("unknown", true)
    }
    val fuelLocked = remember(initial.fuelKind) { initial.fuelKind != FuelKind.UNKNOWN }

    LaunchedEffect(initial.fuelKind, initial.vehicleVin) {
        if (fuelKind == FuelKind.UNKNOWN && initial.fuelKind != FuelKind.UNKNOWN) fuelKind = initial.fuelKind
        val vin = initial.vehicleVin.trim().trim('"')
        if (vehicleVin.isBlank() && vin.length >= 5 && !vin.equals("invalid", true)) vehicleVin = vin
    }

    fun fillSample() {
        vehicleNumber = "12가3456"
        vehicleName = "아이오닉 5"
        vehicleType = "SUV"
        vehicleYear = "2024"
        if (vehicleVin.isBlank()) vehicleVin = "KWDEMO1234567890"
        fuelKind = if (initial.fuelKind != FuelKind.UNKNOWN) initial.fuelKind else FuelKind.EV
        driveKind = DriveKind.AWD
        insurerId = "hyundai"
        customPhone = InsuranceCatalog.find("hyundai")?.claimPhone.orEmpty()
        policyNumber = "DEMO-2026-001234"
        driverName = "김시연"
        driverPhone = "010-1234-5678"
        driverLicenseNumber = "11-12-345678-90"
        driverBirthDate = "1994-05-12"
        bloodType = BloodType.A
        rhType = RhType.POSITIVE
        allergyNotes = "페니실린"
        medicationNotes = "없음"
        otherNotes = "시연용 샘플"
    }

    fun buildProfile() = UserProfile(
        insurerId = insurerId,
        customClaimPhone = customPhone,
        vehicleNumber = vehicleNumber,
        vehicleName = vehicleName,
        vehicleType = vehicleType,
        vehicleYear = vehicleYear,
        vehicleVin = vehicleVin,
        fuelKind = fuelKind,
        driveKind = driveKind,
        insuredName = driverName,
        insuredPhone = driverPhone,
        driverName = driverName,
        driverPhone = driverPhone,
        driverLicenseNumber = driverLicenseNumber,
        driverBirthDate = driverBirthDate,
        policyNumber = policyNumber,
        medical = EmergencyMedicalInfo(
            bloodType, rhType, hypertension, diabetes, heartDisease, allergyNotes, medicationNotes, otherNotes
        ),
        onboardingCompleted = true
    )

    val pager = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    CabinScreen {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("H-safe", style = MaterialTheme.typography.headlineMedium, color = HSafeColors.HyundaiBlue)
                Text("차량·보험·운전자·응급정보 등록", color = HSafeColors.Muted, fontSize = 22.sp)
            }
            GhostAction("샘플 입력", onClick = { fillSample() })
        }
        if (restricted) {
            Text("주행 중 입력 제한 · 정차 후 등록", color = HSafeColors.EmergencyRed, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }

        HorizontalPager(state = pager, modifier = Modifier.weight(1f).fillMaxWidth(), userScrollEnabled = !restricted) { page ->
            if (page == 0) {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ZoneCard(modifier = Modifier.weight(1f).fillMaxHeight(), title = "차량") {
                        CompactField("차량번호", vehicleNumber, { vehicleNumber = it }, !restricted)
                        CompactField("차량명", vehicleName, { vehicleName = it }, !restricted)
                        CompactField("차종", vehicleType, { vehicleType = it }, !restricted)
                        CompactField("년식", vehicleYear, { vehicleYear = it }, !restricted)
                        CompactField(
                            if (vinLocked) "차대번호(VIN) · API" else "차대번호(VIN)",
                            vehicleVin,
                            { vehicleVin = it },
                            enabled = !restricted && !vinLocked
                        )
                        CompactField("증권번호", policyNumber, { policyNumber = it }, !restricted)
                        Text("연료", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                FuelKind.GASOLINE to "가솔린",
                                FuelKind.DIESEL to "디젤",
                                FuelKind.HYBRID to "하이브리드",
                                FuelKind.EV to "전기"
                            ).forEach { (v, l) ->
                                SelectChip(
                                    if (fuelLocked && fuelKind == v) "$l · API" else l,
                                    fuelKind == v,
                                    !restricted && !fuelLocked
                                ) { fuelKind = v }
                            }
                        }
                        Text("구동방식", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SelectChip("전륜(FWD)", driveKind == DriveKind.FWD, !restricted) { driveKind = DriveKind.FWD }
                            SelectChip("후륜(RWD)", driveKind == DriveKind.RWD, !restricted) { driveKind = DriveKind.RWD }
                            SelectChip("사륜(AWD)", driveKind == DriveKind.AWD, !restricted) { driveKind = DriveKind.AWD }
                        }
                        if (vehicleApiHint.isNotBlank()) {
                            Text(vehicleApiHint, color = HSafeColors.Muted, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                        if (vinLocked || fuelLocked) {
                            Text("API로 확인된 항목은 참고용으로 잠겨 있습니다.", color = HSafeColors.HyundaiSky, fontSize = 16.sp)
                        }
                    }
                    ZoneCard(modifier = Modifier.weight(1.25f).fillMaxHeight(), title = "보험사") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            InsuranceCatalog.companies.forEach { company ->
                                InsurerLogoBadge(company, insurerId == company.id) {
                                    insurerId = company.id
                                    if (company.id != "custom") customPhone = company.claimPhone
                                }
                            }
                        }
                        if (insurerId == "custom") {
                            CompactField("대표번호", customPhone, { customPhone = it }, !restricted)
                        }
                    }
                }
            } else {
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ZoneCard(modifier = Modifier.weight(1f).fillMaxHeight(), title = "운전자") {
                        CompactField("이름", driverName, { driverName = it }, !restricted)
                        CompactField("연락처", driverPhone, { driverPhone = it }, !restricted)
                        CompactField("운전면허번호", driverLicenseNumber, { driverLicenseNumber = it }, !restricted)
                        CompactField("생년월일", driverBirthDate, { driverBirthDate = it }, !restricted)
                    }
                    ZoneCard(modifier = Modifier.weight(1f).fillMaxHeight(), title = "응급정보", accent = HSafeColors.EmergencyRed) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            BloodType.entries.filter { it != BloodType.UNKNOWN }.forEach {
                                SelectChip(it.name, bloodType == it, !restricted) { bloodType = it }
                            }
                        }
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SelectChip("Rh+", rhType == RhType.POSITIVE, !restricted) { rhType = RhType.POSITIVE }
                            SelectChip("Rh-", rhType == RhType.NEGATIVE, !restricted) { rhType = RhType.NEGATIVE }
                            SelectChip("고혈압", hypertension, !restricted) { hypertension = !hypertension }
                            SelectChip("당뇨", diabetes, !restricted) { diabetes = !diabetes }
                            SelectChip("심장질환", heartDisease, !restricted) { heartDisease = !heartDisease }
                        }
                        CompactField("알레르기", allergyNotes, { allergyNotes = it }, !restricted)
                        CompactField("복용약", medicationNotes, { medicationNotes = it }, !restricted)
                        CompactField("기타", otherNotes, { otherNotes = it }, !restricted)
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${pager.currentPage + 1} / 2", color = HSafeColors.Muted, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(Modifier.weight(1f))
            if (pager.currentPage == 0) {
                BigAction("다음", onClick = { scope.launch { pager.animateScrollToPage(1) } }, modifier = Modifier.width(220.dp))
            } else {
                GhostAction("이전", onClick = { scope.launch { pager.animateScrollToPage(0) } })
                val ok = !restricted && vehicleNumber.isNotBlank() && driverName.isNotBlank() &&
                    (insurerId != "custom" || customPhone.isNotBlank())
                BigAction("저장하고 시작", enabled = ok, onClick = { onSave(buildProfile()) }, modifier = Modifier.width(280.dp))
            }
        }
    }
}

@Composable
fun HomeScreen(
    state: AppUiState,
    onEmergency119: () -> Unit,
    onFire119: () -> Unit,
    onTraffic: () -> Unit,
    onSettings: () -> Unit,
    onResume: () -> Unit
) {
    CabinScreen {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(HSafeColors.HyundaiBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text("H", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("H-safe", style = MaterialTheme.typography.headlineLarge, color = HSafeColors.HyundaiNavy)
                    Text("차량 응급 · 보험 접수", color = HSafeColors.Muted, fontSize = 26.sp)
                }
            }
            GhostAction("설정", onClick = onSettings)
        }

        ZoneCard(accent = HSafeColors.HyundaiBlue) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(state.profile.insurerName(InsuranceCatalog.companies), fontWeight = FontWeight.Bold, fontSize = 26.sp)
                    Text(state.profile.vehicleSummary(), fontSize = 22.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text(
                    state.profile.medical.summaryLine(),
                    color = HSafeColors.Muted,
                    fontSize = 20.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ModeTile(
                title = "119 응급",
                subtitle = "의식·출혈·끼임\n구급 문자 신고",
                accent = HSafeColors.EmergencyRed,
                onClick = onEmergency119,
                modifier = Modifier.weight(1f),
                icon = { IconAmbulance(HSafeColors.EmergencyRed, 72.dp) }
            )
            ModeTile(
                title = "119 소방",
                subtitle = "화재·연기·배터리\n소방 문자 신고",
                accent = HSafeColors.FireOrange,
                onClick = onFire119,
                modifier = Modifier.weight(1f),
                icon = { IconFire(HSafeColors.FireOrange, 72.dp) }
            )
            ModeTile(
                title = "교통사고",
                subtitle = "추돌·접촉·대인대물\n보험 접수",
                accent = HSafeColors.HyundaiBlue,
                onClick = onTraffic,
                modifier = Modifier.weight(1f),
                icon = { IconRearEnd(HSafeColors.HyundaiBlue, 72.dp) }
            )
        }

        if (state.incident != null) {
            ZoneCard(accent = HSafeColors.SafetyAmber) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("진행 중 사고 이어가기", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "전화·문자 앱이 화면을 덮었거나 홈으로 나갔을 때, 작성 중이던 접수 화면으로 바로 복귀합니다.",
                            color = HSafeColors.Muted,
                            fontSize = 18.sp
                        )
                    }
                    BigAction("이어가기", onClick = onResume, modifier = Modifier.width(220.dp), container = HSafeColors.SafetyAmber, content = HSafeColors.Ink)
                }
            }
        }
    }
}

@Composable
fun ResponseFlowScreen(
    state: AppUiState,
    messagePreview: String,
    insurerPhone: String,
    onBack: () -> Unit,
    onSelectKind: (AccidentKind) -> Unit,
    onUpdateMovableTow: (TriState?, TriState?) -> Unit,
    onDial: () -> Unit,
    onDial119: () -> Unit,
    onOnline: () -> Unit,
    onMessageIntake: () -> Unit,
    onSend119Emergency: () -> Unit,
    onSend119Fire: () -> Unit,
    onDemo: () -> Unit
) {
    val suspected = state.incident?.vehicleSafety?.suspectedKinds.orEmpty()
    val selected = state.incident?.accidentKind ?: AccidentKind.UNKNOWN
    val loc = state.incident?.location
    val options = kindOptionsFor(state.responseMode)

    CabinScreen {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            GhostAction("홈", onClick = onBack)
            Spacer(Modifier.width(12.dp))
            Text(
                when (state.responseMode) {
                    ResponseMode.EMERGENCY_119 -> "119 응급 신고"
                    ResponseMode.FIRE_119 -> "119 소방 신고"
                    else -> "교통사고 접수"
                },
                style = MaterialTheme.typography.headlineMedium,
                color = HSafeColors.HyundaiNavy
            )
            Spacer(Modifier.weight(1f))
            if (state.responseMode == ResponseMode.TRAFFIC) {
                GhostAction("시연 샘플", onClick = onDemo)
            }
        }

        Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ZoneCard(modifier = Modifier.weight(1.25f).fillMaxHeight(), title = "사고 유형", accent = HSafeColors.SafetyYellow) {
                val telltales = state.incident?.vehicleSafety?.telltalesSummary.orEmpty()
                val alerts = parseSensorAlerts(telltales, suspected)
                SensorWarningStrip(alerts)
                options.chunked(2).forEach { row ->
                    Row(
                        Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { opt ->
                            KindTile(
                                title = opt.title,
                                subtitle = opt.subtitle,
                                selected = selected == opt.kind,
                                recommended = suspected.contains(opt.kind),
                                accent = opt.accent,
                                onClick = { onSelectKind(opt.kind) },
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                icon = { opt.icon() }
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ZoneCard(modifier = Modifier.weight(0.72f).fillMaxWidth(), title = "위치 · 차량", accent = HSafeColors.HyundaiSky) {
                    Text("위치(내비)", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = HSafeColors.Muted)
                    Text(
                        loc?.displayLine()?.ifBlank { "위치 조회 중…" } ?: "위치 조회 중…",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text("내 차량", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = HSafeColors.Muted)
                    Text(state.profile.vehicleSummary(), fontSize = 26.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(
                        "${state.profile.insurerName(InsuranceCatalog.companies)} · ${state.profile.representativePhone(InsuranceCatalog.companies)}",
                        color = HSafeColors.Muted,
                        fontSize = 22.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (state.responseMode == ResponseMode.TRAFFIC) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("이동", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            SelectChip("가능", state.incident?.movable == TriState.YES) { onUpdateMovableTow(TriState.YES, null) }
                            SelectChip("불가", state.incident?.movable == TriState.NO) { onUpdateMovableTow(TriState.NO, null) }
                            Text("견인", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            SelectChip("필요", state.incident?.towNeeded == TriState.YES) { onUpdateMovableTow(null, TriState.YES) }
                            SelectChip("불필요", state.incident?.towNeeded == TriState.NO) { onUpdateMovableTow(null, TriState.NO) }
                        }
                    }
                }

                ZoneCard(
                    modifier = Modifier.weight(1.15f).fillMaxWidth(),
                    title = "신고 메시지 미리보기",
                    accent = when (state.responseMode) {
                        ResponseMode.FIRE_119 -> HSafeColors.FireOrange
                        ResponseMode.EMERGENCY_119 -> HSafeColors.EmergencyRed
                        else -> HSafeColors.HyundaiBlue
                    }
                ) {
                    Surface(
                        color = HSafeColors.CabinGray,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    ) {
                        Text(
                            messagePreview.ifBlank { "사고 유형을 선택하면 메시지가 표시됩니다." },
                            modifier = Modifier.padding(14.dp),
                            fontSize = 22.sp,
                            lineHeight = 30.sp,
                            color = HSafeColors.Ink
                        )
                    }
                    state.statusMessage?.let { msg ->
                        Text(msg, color = HSafeColors.HyundaiBlue, fontWeight = FontWeight.SemiBold, maxLines = 2, fontSize = 16.sp)
                    }
                    when (state.responseMode) {
                        ResponseMode.EMERGENCY_119 -> {
                            BigAction(
                                "119 전화 연결",
                                onClick = onDial119,
                                container = HSafeColors.EmergencyRed
                            )
                            BigAction(
                                "119 응급 메시지 보내기",
                                onClick = onSend119Emergency,
                                enabled = selected != AccidentKind.UNKNOWN,
                                container = HSafeColors.EmergencyRed
                            )
                        }
                        ResponseMode.FIRE_119 -> {
                            BigAction(
                                "119 전화 연결",
                                onClick = onDial119,
                                container = HSafeColors.FireOrange
                            )
                            BigAction(
                                "119 소방 메시지 보내기",
                                onClick = onSend119Fire,
                                enabled = selected != AccidentKind.UNKNOWN,
                                container = HSafeColors.FireOrange
                            )
                        }
                        else -> {
                            Text(insurerPhone, fontSize = 34.sp, fontWeight = FontWeight.Bold, color = HSafeColors.HyundaiBlue)
                            BigAction(
                                "보험사 전화 연결",
                                onClick = onDial,
                                enabled = selected != AccidentKind.UNKNOWN && insurerPhone.isNotBlank()
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                GhostAction("메시지 보내기", onClick = onMessageIntake, enabled = false, modifier = Modifier.weight(1f))
                                GhostAction("온라인 접수", onClick = onOnline, enabled = false, modifier = Modifier.weight(1f))
                            }
                            Text("메시지·온라인 접수는 준비 중", color = HSafeColors.Muted, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

private class KindOption(
    val kind: AccidentKind,
    val title: String,
    val subtitle: String,
    val accent: Color,
    val icon: @Composable () -> Unit
)

@Composable
private fun kindOptionsFor(mode: ResponseMode): List<KindOption> = when (mode) {
    ResponseMode.EMERGENCY_119 -> listOf(
        KindOption(AccidentKind.UNCONSCIOUS, "의식 없음", "실신·반응 없음", HSafeColors.EmergencyRed) { IconUnconscious(HSafeColors.EmergencyRed, 48.dp) },
        KindOption(AccidentKind.BREATHING_ISSUE, "호흡 이상", "호흡곤란·무호흡", HSafeColors.EmergencyRed) { IconBreathing(HSafeColors.EmergencyRed, 48.dp) },
        KindOption(AccidentKind.HEAVY_BLEEDING, "대량 출혈", "지혈 필요", HSafeColors.EmergencyRed) { IconBleeding(HSafeColors.EmergencyRed, 48.dp) },
        KindOption(AccidentKind.ENTRAPMENT, "끼임·구조", "차체 압박·탈출 불가", HSafeColors.EmergencyRed) { IconEntrapment(HSafeColors.EmergencyRed, 48.dp) },
        KindOption(AccidentKind.CHEST_PAIN, "흉통·심정지", "가슴통증·심정지 의심", HSafeColors.EmergencyRed) { IconChestPain(HSafeColors.EmergencyRed, 48.dp) },
        KindOption(AccidentKind.MULTI_CASUALTY, "다수 사상", "부상자 2명 이상", HSafeColors.EmergencyRed) { IconMultiCasualty(HSafeColors.EmergencyRed, 48.dp) }
    )
    ResponseMode.FIRE_119 -> listOf(
        KindOption(AccidentKind.VEHICLE_FIRE, "차량 화재", "불꽃·발화", HSafeColors.FireOrange) { IconFire(HSafeColors.FireOrange, 48.dp) },
        KindOption(AccidentKind.SMOKE, "연기 발생", "연무·냄새", HSafeColors.FireOrange) { IconSmoke(HSafeColors.FireOrange, 48.dp) },
        KindOption(AccidentKind.EV_BATTERY_FIRE, "배터리 화재", "전기차·하이브리드", HSafeColors.FireOrange) { IconBatteryFire(HSafeColors.FireOrange, 48.dp) },
        KindOption(AccidentKind.FUEL_LEAK, "연료 누출", "휘발유·경유 누출", HSafeColors.FireOrange) { IconFuelLeak(HSafeColors.FireOrange, 48.dp) },
        KindOption(AccidentKind.ENGINE_OVERHEAT, "엔진 과열", "엔진룸 과열·스팀", HSafeColors.FireOrange) { IconOverheat(HSafeColors.FireOrange, 48.dp) },
        KindOption(AccidentKind.OTHER, "기타 위험", "폭발·누출 의심", HSafeColors.FireOrange) { IconOther(HSafeColors.FireOrange, 48.dp) }
    )
    else -> listOf(
        KindOption(AccidentKind.REAR_END, "후미 추돌", "뒤에서 받음·밀림", HSafeColors.HyundaiBlue) { IconRearEnd(HSafeColors.HyundaiBlue, 48.dp) },
        KindOption(AccidentKind.SIDE_CONTACT, "측면 접촉", "사이드·차선 변경", HSafeColors.HyundaiBlue) { IconSideContact(HSafeColors.HyundaiBlue, 48.dp) },
        KindOption(AccidentKind.HEAD_ON, "정면 충돌", "마주 보고 충돌", HSafeColors.HyundaiBlue) { IconHeadOn(HSafeColors.HyundaiBlue, 48.dp) },
        KindOption(AccidentKind.SINGLE_VEHICLE, "단독 사고", "가드레일·시설물", HSafeColors.HyundaiSky) { IconSingleVehicle(HSafeColors.HyundaiSky, 48.dp) },
        KindOption(AccidentKind.PROPERTY_DAMAGE, "대물", "상대차·시설 파손", HSafeColors.HyundaiSky) { IconPropertyDamage(HSafeColors.HyundaiSky, 48.dp) },
        KindOption(AccidentKind.PERSON_INJURY, "대인", "보행자·탑승자 부상", HSafeColors.EmergencyRed) { IconPersonInjury(HSafeColors.EmergencyRed, 48.dp) },
        KindOption(AccidentKind.HIT_AND_RUN, "뺑소니", "가해자 도주", HSafeColors.EmergencyRed) { IconHitAndRun(HSafeColors.EmergencyRed, 48.dp) },
        KindOption(AccidentKind.OTHER, "기타", "복합·분류 어려움", HSafeColors.Muted) { IconOther(HSafeColors.Muted, 48.dp) }
    )
}

@Composable
fun SettingsScreen(
    state: AppUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onClear: () -> Unit
) {
    CabinScreen {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            GhostAction("홈", onClick = onBack)
            Spacer(Modifier.width(12.dp))
            Text("설정", style = MaterialTheme.typography.headlineMedium)
        }
        ZoneCard(title = "등록 정보", accent = HSafeColors.HyundaiBlue) {
            Text(state.profile.insurerName(InsuranceCatalog.companies), fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(state.profile.representativePhone(InsuranceCatalog.companies), fontSize = 22.sp)
            Text(state.profile.vehicleSummary(), fontSize = 22.sp)
            Text(
                "운전자 ${state.profile.driverName.ifBlank { state.profile.insuredName }} · ${state.profile.driverPhone.ifBlank { state.profile.insuredPhone }}",
                fontSize = 22.sp
            )
            if (state.profile.driverLicenseNumber.isNotBlank()) {
                Text("면허 ${state.profile.driverLicenseNumber}", fontSize = 20.sp, color = HSafeColors.Muted)
            }
            Text(state.profile.medical.summaryLine(), fontSize = 20.sp, color = HSafeColors.Muted)
            Text("Pleos ${BuildConfig.PLEOS_PROJECT_ID}", color = HSafeColors.Muted, fontSize = 16.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BigAction("정보 수정", onClick = onEdit, modifier = Modifier.weight(1f))
            GhostAction("등록 초기화", onClick = onClear, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CompactField(label: String, value: String, onChange: (String) -> Unit, enabled: Boolean) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, fontSize = 18.sp) },
        textStyle = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.fillMaxWidth().height(76.dp),
        enabled = enabled,
        singleLine = true
    )
}

@Composable
private fun SelectChip(label: String, selected: Boolean, enabled: Boolean = true, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        label = { Text(label, fontWeight = FontWeight.SemiBold, fontSize = 20.sp) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = HSafeColors.HyundaiBlue,
            selectedLabelColor = Color.White
        )
    )
}

@Composable
private fun SensorChip(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(36.dp)
            .padding(end = 0.dp)
    ) {
        Surface(color = HSafeColors.SafetyYellow.copy(alpha = 0.35f), shape = RoundedCornerShape(10.dp)) {
            Row(
                Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconSensor(HSafeColors.Ink, 16.dp)
                Text(text, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
            }
        }
    }
}

private fun kindLabel(kind: AccidentKind): String = CallSummary.kindLabel(kind)
