package com.example.myapplication.shared

import com.example.myapplication.shared.data.InsuranceCatalog
import com.example.myapplication.shared.model.AccidentKind
import com.example.myapplication.shared.model.FuelKind
import com.example.myapplication.shared.model.Incident
import com.example.myapplication.shared.model.TriState
import com.example.myapplication.shared.model.UserProfile

object CallSummary {
    fun kindLabel(kind: AccidentKind): String = when (kind) {
        AccidentKind.UNCONSCIOUS -> "의식 없음"
        AccidentKind.BREATHING_ISSUE -> "호흡곤란·무호흡"
        AccidentKind.HEAVY_BLEEDING -> "대량 출혈"
        AccidentKind.ENTRAPMENT -> "끼임·구조"
        AccidentKind.CHEST_PAIN -> "흉통·심정지 의심"
        AccidentKind.MULTI_CASUALTY -> "다수 부상자"
        AccidentKind.VEHICLE_FIRE -> "차량 화재(불꽃)"
        AccidentKind.SMOKE -> "연기 발생"
        AccidentKind.EV_BATTERY_FIRE -> "전기차·배터리 화재"
        AccidentKind.FUEL_LEAK -> "연료 누출"
        AccidentKind.ENGINE_OVERHEAT -> "엔진룸 과열"
        AccidentKind.REAR_END -> "후미 추돌"
        AccidentKind.SIDE_CONTACT -> "측면 접촉"
        AccidentKind.HEAD_ON -> "정면 충돌"
        AccidentKind.SINGLE_VEHICLE -> "단독 사고"
        AccidentKind.PROPERTY_DAMAGE -> "대물(시설물)"
        AccidentKind.PERSON_INJURY -> "대인(인명)"
        AccidentKind.HIT_AND_RUN -> "뺑소니"
        AccidentKind.OTHER -> "기타"
        AccidentKind.UNKNOWN -> "유형 미선택"
    }

    fun intakeBlocks(profile: UserProfile, incident: Incident): List<Pair<String, String>> {
        val kind = buildString {
            append(kindLabel(incident.accidentKind))
            if (incident.vehicleSafety.suspectedKinds.isNotEmpty()) {
                append("\n차량 경고 의심: ")
                append(incident.vehicleSafety.suspectedKinds.joinToString { kindLabel(it) })
            }
            append("\n이동 ${tri(incident.movable)} · 견인 ${tri(incident.towNeeded)}")
        }
        val vehicle = buildString {
            append(profile.vehicleSummary())
            append("\n운전자 ${profile.driverName.ifBlank { profile.insuredName }}")
            val phone = profile.driverPhone.ifBlank { profile.insuredPhone }
            if (phone.isNotBlank()) append(" · $phone")
            if (profile.driverLicenseNumber.isNotBlank()) append("\n면허 ${profile.driverLicenseNumber}")
            if (profile.driverBirthDate.isNotBlank()) append(" · 생년 ${profile.driverBirthDate}")
            if (profile.policyNumber.isNotBlank()) append("\n증권번호 ${profile.policyNumber}")
            append("\n보험사 ${profile.insurerName(InsuranceCatalog.companies)}")
        }
        return listOf(
            "1. 무슨 사고인지" to kind,
            "2. 어디인지" to incident.location.displayLine(),
            "3. 차량·계약 정보" to vehicle
        )
    }

    fun emergency119Message(profile: UserProfile, incident: Incident): String = buildString {
        appendLine("[H-safe 119 응급]")
        appendLine("상황: ${kindLabel(incident.accidentKind)}")
        appendLine("위치: ${incident.location.displayLine()}")
        appendLine("차량: ${profile.vehicleSummary()}")
        appendLine("연락: ${profile.driverName.ifBlank { profile.insuredName }} / ${profile.driverPhone.ifBlank { profile.insuredPhone }}")
        appendLine("응급: ${profile.medical.summaryLine()}")
        if (profile.medical.allergyNotes.isNotBlank()) appendLine("알레르기: ${profile.medical.allergyNotes}")
        if (profile.medical.medicationNotes.isNotBlank()) appendLine("복용약: ${profile.medical.medicationNotes}")
        if (profile.medical.otherNotes.isNotBlank()) appendLine("기타: ${profile.medical.otherNotes}")
    }

    fun fire119Message(profile: UserProfile, incident: Incident): String = buildString {
        appendLine("[H-safe 119 소방]")
        appendLine("상황: ${kindLabel(incident.accidentKind)}")
        appendLine("위치: ${incident.location.displayLine()}")
        appendLine("차량: ${profile.vehicleSummary()}")
        appendLine("연락처: ${profile.driverPhone.ifBlank { profile.insuredPhone }}")
        if (profile.fuelKind == FuelKind.EV || profile.fuelKind == FuelKind.HYBRID ||
            incident.accidentKind == AccidentKind.EV_BATTERY_FIRE
        ) {
            appendLine("주의: 전기/하이브리드·배터리 관련")
        }
        appendLine("연료: ${profile.fuelLabel()}")
    }

    fun trafficClaimPreview(profile: UserProfile, incident: Incident): String = buildString {
        appendLine("[H-safe 보험 접수 요약]")
        appendLine("1. 사고: ${kindLabel(incident.accidentKind)}")
        if (incident.vehicleSafety.suspectedKinds.isNotEmpty()) {
            appendLine("   센서의심: ${incident.vehicleSafety.suspectedKinds.joinToString { kindLabel(it) }}")
        }
        appendLine("2. 위치: ${incident.location.displayLine()}")
        appendLine("3. 차량: ${profile.vehicleSummary()}")
        appendLine("   운전자 ${profile.driverName.ifBlank { profile.insuredName }} ${profile.driverPhone.ifBlank { profile.insuredPhone }}")
        if (profile.driverLicenseNumber.isNotBlank()) appendLine("   면허 ${profile.driverLicenseNumber}")
        if (profile.driverBirthDate.isNotBlank()) appendLine("   생년 ${profile.driverBirthDate}")
        if (profile.policyNumber.isNotBlank()) appendLine("   증권 ${profile.policyNumber}")
        appendLine("   보험 ${profile.insurerName(InsuranceCatalog.companies)}")
        appendLine("이동 ${tri(incident.movable)} · 견인 ${tri(incident.towNeeded)}")
        appendLine("ARS: ${arsGuide(profile)}")
    }

    fun arsGuide(profile: UserProfile): String {
        val company = InsuranceCatalog.find(profile.insurerId)
        return company?.arsClaimMenu
            ?: "실제 ARS에서 자동차 사고접수 또는 긴급출동 메뉴를 선택하세요."
    }

    private fun tri(value: TriState): String = when (value) {
        TriState.YES -> "가능/필요"
        TriState.NO -> "불가/불필요"
        TriState.UNKNOWN -> "미확인"
    }
}
