package com.example.myapplication.shared.model

import kotlinx.serialization.Serializable

enum class FuelKind {
    GASOLINE,
    DIESEL,
    HYBRID,
    EV,
    UNKNOWN
}

enum class DriveKind {
    FWD,   // 전륜구동
    RWD,   // 후륜구동
    AWD,   // 사륜구동(AWD/4WD)
    UNKNOWN
}

enum class TriState {
    YES,
    NO,
    UNKNOWN
}

enum class BloodType {
    A, B, O, AB, UNKNOWN
}

enum class RhType {
    POSITIVE,
    NEGATIVE,
    UNKNOWN
}

/**
 * 모드별 신고 유형.
 * - 119응급: 구급 현장 전달용 (의식/호흡/출혈/끼임 등)
 * - 119소방: 화재·위험물
 * - 교통사고: 보험 접수 유형 (추돌/접촉/단독/대인·대물 등)
 */
enum class AccidentKind {
    // 119 응급
    UNCONSCIOUS,
    BREATHING_ISSUE,
    HEAVY_BLEEDING,
    ENTRAPMENT,
    CHEST_PAIN,
    MULTI_CASUALTY,
    // 119 소방
    VEHICLE_FIRE,
    SMOKE,
    EV_BATTERY_FIRE,
    FUEL_LEAK,
    ENGINE_OVERHEAT,
    // 교통사고(보험)
    REAR_END,
    SIDE_CONTACT,
    HEAD_ON,
    SINGLE_VEHICLE,
    PROPERTY_DAMAGE,
    PERSON_INJURY,
    HIT_AND_RUN,
    // 공통
    OTHER,
    UNKNOWN
}

enum class ResponseMode {
    NONE,
    TRAFFIC,
    EMERGENCY_119,
    FIRE_119
}

enum class LookupStatus {
    IDLE,
    LOADING,
    SUCCESS,
    FAILED,
    UNSUPPORTED
}

enum class DataSource {
    USER,
    SDK,
    REGISTERED
}

@Serializable
data class InsuranceCompany(
    val id: String,
    val name: String,
    val shortLabel: String,
    val claimPhone: String,
    val logoColorArgb: Long,
    val arsClaimMenu: String? = null,
    val arsRoadsideMenu: String? = null,
    val sourceNote: String? = null,
    val verifiedOn: String? = null
)

@Serializable
data class EmergencyMedicalInfo(
    val bloodType: BloodType = BloodType.UNKNOWN,
    val rhType: RhType = RhType.UNKNOWN,
    val hypertension: Boolean = false,
    val diabetes: Boolean = false,
    val heartDisease: Boolean = false,
    val allergyNotes: String = "",
    val medicationNotes: String = "",
    val otherNotes: String = ""
) {
    fun summaryLine(): String {
        val blood = when {
            bloodType == BloodType.UNKNOWN -> "혈액형 미등록"
            rhType == RhType.UNKNOWN -> bloodType.name
            else -> "${bloodType.name} Rh${if (rhType == RhType.POSITIVE) "+" else "-"}"
        }
        val conditions = buildList {
            if (hypertension) add("고혈압")
            if (diabetes) add("당뇨")
            if (heartDisease) add("심장질환")
        }.joinToString(", ").ifBlank { "기저질환 없음/미확인" }
        return "$blood · $conditions"
    }
}

@Serializable
data class UserProfile(
    val insurerId: String = "",
    val customClaimPhone: String = "",
    val vehicleNumber: String = "",
    val vehicleName: String = "",
    val vehicleType: String = "",
    val vehicleYear: String = "",
    val vehicleVin: String = "",
    val fuelKind: FuelKind = FuelKind.UNKNOWN,
    val driveKind: DriveKind = DriveKind.UNKNOWN,
    val insuredName: String = "",
    val insuredPhone: String = "",
    val driverName: String = "",
    val driverPhone: String = "",
    val driverLicenseNumber: String = "",
    val driverBirthDate: String = "",
    val policyNumber: String = "",
    val policyHolderName: String = "",
    val medical: EmergencyMedicalInfo = EmergencyMedicalInfo(),
    val onboardingCompleted: Boolean = false
) {
    fun representativePhone(companies: List<InsuranceCompany>): String {
        if (insurerId == "custom") return customClaimPhone
        val companyPhone = companies.firstOrNull { it.id == insurerId }?.claimPhone.orEmpty()
        return companyPhone.ifBlank { customClaimPhone }
    }

    fun insurerName(companies: List<InsuranceCompany>): String =
        companies.firstOrNull { it.id == insurerId }?.name ?: "직접 입력"

    fun fuelLabel(): String = when (fuelKind) {
        FuelKind.GASOLINE -> "가솔린"
        FuelKind.DIESEL -> "디젤"
        FuelKind.HYBRID -> "하이브리드"
        FuelKind.EV -> "전기"
        FuelKind.UNKNOWN -> "미확인"
    }

    fun driveLabel(): String = when (driveKind) {
        DriveKind.FWD -> "전륜(FWD)"
        DriveKind.RWD -> "후륜(RWD)"
        DriveKind.AWD -> "사륜(AWD)"
        DriveKind.UNKNOWN -> "구동 미확인"
    }

    fun vehicleSummary(): String = buildString {
        append(vehicleName.ifBlank { "차명 미등록" })
        if (vehicleType.isNotBlank()) append(" · $vehicleType")
        if (vehicleYear.isNotBlank()) append(" · ${vehicleYear}년식")
        append(" · ${vehicleNumber.ifBlank { "번호 미등록" }}")
        append(" · ${fuelLabel()}")
        append(" · ${driveLabel()}")
        if (vehicleVin.isNotBlank()) append(" · 차대 ${vehicleVin.takeLast(8)}")
    }
}

@Serializable
data class LocationNote(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val spokenSummary: String = "",
    val roadName: String = "",
    val direction: String = "",
    val landmark: String = "",
    val kilometerPost: String = "",
    val laneOrShoulder: String = "",
    val isHighway: Boolean = false,
    val addressLine: String = "",
    val nearbyPlace: String = "",
    val status: LookupStatus = LookupStatus.IDLE,
    val source: DataSource = DataSource.USER,
    val queriedAtEpochMs: Long? = null,
    val errorMessage: String? = null
) {
    fun displayLine(): String {
        if (spokenSummary.isNotBlank()) return spokenSummary
        return if (isHighway) {
            buildString {
                append(roadName.ifBlank { "고속도로" })
                if (direction.isNotBlank()) append(" $direction")
                if (kilometerPost.isNotBlank()) append(" · ${kilometerPost} 지점")
                if (isEmpty()) append("고속도로 위치 미확인")
            }
        } else {
            addressLine.ifBlank { roadName }.ifBlank { "일반도로 위치 미확인" }
        }
    }
}

@Serializable
data class VehicleSafetySnapshot(
    val telltalesSummary: String = "",
    val airbagSummary: String = "",
    val suspectedKinds: List<AccidentKind> = emptyList(),
    val status: LookupStatus = LookupStatus.IDLE,
    val source: DataSource = DataSource.SDK,
    val queriedAtEpochMs: Long? = null,
    val errorMessage: String? = null
)

@Serializable
data class FieldRecord(
    val hasOtherParty: Boolean = false,
    val otherVehicleNumber: String = "",
    val otherPartyContact: String = "",
    val injuryNotes: String = "",
    val propertyDamageNotes: String = "",
    val photoReminderChecked: Boolean = false,
    val blackBoxReminderChecked: Boolean = false,
    val claimNumber: String = "",
    val towRequested: TriState = TriState.UNKNOWN,
    val memo: String = ""
)

@Serializable
data class Incident(
    val id: String,
    val openedAtEpochMs: Long,
    val mode: ResponseMode = ResponseMode.TRAFFIC,
    val accidentKind: AccidentKind = AccidentKind.UNKNOWN,
    val injuryOrFire: Boolean = false,
    val highwayOrExpressway: Boolean = false,
    val movable: TriState = TriState.UNKNOWN,
    val towNeeded: TriState = TriState.UNKNOWN,
    val damageNotes: String = "",
    val location: LocationNote = LocationNote(),
    val vehicleSafety: VehicleSafetySnapshot = VehicleSafetySnapshot(),
    val fieldRecord: FieldRecord = FieldRecord(),
    val dialAttempted: Boolean = false
)
