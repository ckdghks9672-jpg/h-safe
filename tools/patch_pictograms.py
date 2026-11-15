# -*- coding: utf-8 -*-
from pathlib import Path

p = Path(r"C:/Users/ckdgh/AndroidStudioProjects/MyApplication/automotive/src/main/java/com/example/myapplication/ui/Screens.kt")
text = p.read_text(encoding="utf-8")

old_sensor = '''            ZoneCard(modifier = Modifier.weight(1.25f).fillMaxHeight(), title = "사고 유형", accent = HSafeColors.SafetyYellow) {
                if (suspected.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        suspected.forEach { SensorChip(kindLabel(it)) }
                    }
                } else {
                    Text("센서 경고 없음 · 직접 선택", color = HSafeColors.Muted, fontSize = 18.sp)
                }'''

new_sensor = '''            ZoneCard(modifier = Modifier.weight(1.25f).fillMaxHeight(), title = "사고 유형", accent = HSafeColors.SafetyYellow) {
                val telltales = state.incident?.vehicleSafety?.telltalesSummary.orEmpty()
                val alerts = parseSensorAlerts(telltales, suspected)
                SensorWarningStrip(alerts)'''

if old_sensor not in text:
    raise SystemExit("sensor block not found")
text = text.replace(old_sensor, new_sensor)

old_opts = '''@Composable
private fun kindOptionsFor(mode: ResponseMode): List<KindOption> = when (mode) {
    ResponseMode.EMERGENCY_119 -> listOf(
        KindOption(AccidentKind.UNCONSCIOUS, "의식 없음", "실신·반응 없음", HSafeColors.EmergencyRed) { IconAmbulance(HSafeColors.EmergencyRed, 30.dp) },
        KindOption(AccidentKind.BREATHING_ISSUE, "호흡 이상", "호흡곤란·무호흡", HSafeColors.EmergencyRed) { IconAmbulance(HSafeColors.EmergencyRed, 30.dp) },
        KindOption(AccidentKind.HEAVY_BLEEDING, "대량 출혈", "지혈 필요", HSafeColors.EmergencyRed) { IconAmbulance(HSafeColors.EmergencyRed, 30.dp) },
        KindOption(AccidentKind.ENTRAPMENT, "끼임·구조", "차체 압박·탈출 불가", HSafeColors.EmergencyRed) { IconBreakdown(HSafeColors.EmergencyRed, 30.dp) },
        KindOption(AccidentKind.CHEST_PAIN, "흉통·심정지", "가슴통증·심정지 의심", HSafeColors.EmergencyRed) { IconAmbulance(HSafeColors.EmergencyRed, 30.dp) },
        KindOption(AccidentKind.MULTI_CASUALTY, "다수 사상", "부상자 2명 이상", HSafeColors.EmergencyRed) { IconAmbulance(HSafeColors.EmergencyRed, 30.dp) }
    )
    ResponseMode.FIRE_119 -> listOf(
        KindOption(AccidentKind.VEHICLE_FIRE, "차량 화재", "불꽃·발화", HSafeColors.FireOrange) { IconFire(HSafeColors.FireOrange, 30.dp) },
        KindOption(AccidentKind.SMOKE, "연기 발생", "연무·냄새", HSafeColors.FireOrange) { IconFire(HSafeColors.FireOrange, 30.dp) },
        KindOption(AccidentKind.EV_BATTERY_FIRE, "배터리 화재", "전기차·하이브리드", HSafeColors.FireOrange) { IconFire(HSafeColors.FireOrange, 30.dp) },
        KindOption(AccidentKind.FUEL_LEAK, "연료 누출", "휘발유·경유 누출", HSafeColors.FireOrange) { IconBreakdown(HSafeColors.FireOrange, 30.dp) },
        KindOption(AccidentKind.ENGINE_OVERHEAT, "엔진 과열", "엔진룸 과열·스팀", HSafeColors.FireOrange) { IconFire(HSafeColors.FireOrange, 30.dp) },'''

# Read rest of fire/traffic from file after finding - replace whole function body via marker
start = text.index("@Composable\nprivate fun kindOptionsFor(mode: ResponseMode)")
end = text.index("\n@Composable\nprivate fun CompactField(")
new_opts = r'''@Composable
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

'''

text = text[:start] + new_opts + text[end:]

# bump a few hard-coded small fonts in response/home
repls = [
    ('fontSize = 18.sp, color = HSafeColors.Muted)', 'fontSize = 22.sp, color = HSafeColors.Muted)'),
    ('fontSize = 24.sp,\n                        fontWeight = FontWeight.Bold,', 'fontSize = 28.sp,\n                        fontWeight = FontWeight.Bold,'),
    ('fontSize = 22.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)', 'fontSize = 26.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)'),
    ('fontSize = 18.sp,\n                        maxLines = 1,', 'fontSize = 22.sp,\n                        maxLines = 1,'),
    ('Text("이동", fontWeight = FontWeight.Bold, fontSize = 18.sp)', 'Text("이동", fontWeight = FontWeight.Bold, fontSize = 22.sp)'),
    ('Text("견인", fontWeight = FontWeight.Bold, fontSize = 18.sp)', 'Text("견인", fontWeight = FontWeight.Bold, fontSize = 22.sp)'),
    ('fontSize = 18.sp,\n                            lineHeight = 26.sp,', 'fontSize = 22.sp,\n                            lineHeight = 30.sp,'),
    ('fontSize = 28.sp, fontWeight = FontWeight.Bold, color = HSafeColors.HyundaiBlue)', 'fontSize = 34.sp, fontWeight = FontWeight.Bold, color = HSafeColors.HyundaiBlue)'),
    ('Text("차량 응급 · 보험 접수", color = HSafeColors.Muted, fontSize = 22.sp)', 'Text("차량 응급 · 보험 접수", color = HSafeColors.Muted, fontSize = 26.sp)'),
    ('Text("진행 중 사고 이어가기", fontSize = 26.sp, fontWeight = FontWeight.Bold)', 'Text("진행 중 사고 이어가기", fontSize = 30.sp, fontWeight = FontWeight.Bold)'),
]
for a,b in repls:
    text = text.replace(a,b)

p.write_text(text, encoding="utf-8")
print("patched Screens.kt")
