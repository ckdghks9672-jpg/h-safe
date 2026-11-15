package com.example.myapplication.pleos

import ai.pleos.playground.navi.data.CurrentLocationInfo
import ai.pleos.playground.navi.helper.NaviHelper
import ai.pleos.playground.navi.helper.listener.NaviHelperEventListener
import ai.pleos.playground.vehicle.Vehicle
import android.content.Context
import android.util.Log
import com.example.myapplication.shared.model.AccidentKind
import com.example.myapplication.shared.model.DataSource
import com.example.myapplication.shared.model.FuelKind
import com.example.myapplication.shared.model.LocationNote
import com.example.myapplication.shared.model.LookupStatus
import com.example.myapplication.shared.model.VehicleSafetySnapshot
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

private const val TAG = "SagohanjangPleos"

class NaviLocationClient(context: Context) {
    private val appContext = context.applicationContext
    private var helper: NaviHelper? = null

    fun initialize(): Boolean = runCatching {
        val navi = NaviHelper(appContext)
        navi.initialize()
        helper = navi
        true
    }.getOrElse {
        Log.e(TAG, "NaviHelper initialize failed", it)
        false
    }

    fun release() {
        runCatching { helper?.release() }
        helper = null
    }

    suspend fun fetchCurrentLocation(timeoutMs: Long = 8_000): LocationNote {
        val navi = helper ?: return LocationNote(
            status = LookupStatus.FAILED,
            source = DataSource.SDK,
            queriedAtEpochMs = System.currentTimeMillis(),
            errorMessage = "NaviHelper 미초기화",
            spokenSummary = "현재 위치 후보 · 내비 미연결"
        )

        val result = withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine { cont ->
                val resumed = AtomicBoolean(false)
                val listener = object : NaviHelperEventListener {
                    override fun onCurrentLocationInfo(currentLocationInfo: CurrentLocationInfo) {
                        if (!resumed.compareAndSet(false, true)) return
                        runCatching { navi.removeListener(this) }
                        cont.resume(currentLocationInfo.toLocationNote())
                    }
                }
                cont.invokeOnCancellation {
                    runCatching { navi.removeListener(listener) }
                }
                runCatching {
                    navi.addListener(listener)
                    navi.getCurrentLocationInfo()
                }.onFailure { error ->
                    if (resumed.compareAndSet(false, true)) {
                        runCatching { navi.removeListener(listener) }
                        cont.resume(
                            LocationNote(
                                status = LookupStatus.FAILED,
                                source = DataSource.SDK,
                                queriedAtEpochMs = System.currentTimeMillis(),
                                errorMessage = error.message,
                                spokenSummary = "현재 위치 후보 · 조회 실패"
                            )
                        )
                    }
                }
            }
        }

        return result ?: LocationNote(
            status = LookupStatus.FAILED,
            source = DataSource.SDK,
            queriedAtEpochMs = System.currentTimeMillis(),
            errorMessage = "위치 조회 타임아웃",
            spokenSummary = "현재 위치 후보 · 응답 없음"
        )
    }
}

private fun CurrentLocationInfo.toLocationNote(): LocationNote {
    val addressText = address
    val looksHighway = addressText.contains("고속") || addressText.contains("IC") || addressText.contains("JC")
    val summary = when {
        addressText.isNotBlank() -> addressText
        else -> "좌표 $latitude, $longitude · 진행방향 ${heading.toInt()}도"
    }
    return LocationNote(
        latitude = latitude,
        longitude = longitude,
        spokenSummary = summary,
        roadName = addressText,
        addressLine = addressText,
        isHighway = looksHighway,
        status = LookupStatus.SUCCESS,
        source = DataSource.SDK,
        queriedAtEpochMs = System.currentTimeMillis()
    )
}

class VehicleSafetyClient(context: Context) {
    private val appContext = context.applicationContext
    private var vehicle: Vehicle? = null

    fun initialize(): Boolean = runCatching {
        val created = Vehicle(appContext)
        created.initialize()
        vehicle = created
        true
    }.getOrElse {
        Log.e(TAG, "Vehicle initialize failed", it)
        vehicle = null
        false
    }

    fun release() {
        runCatching { vehicle?.release() }
        vehicle = null
    }

    suspend fun fetchSafetySnapshot(timeoutMs: Long = 8_000): VehicleSafetySnapshot {
        val sdk = vehicle ?: return VehicleSafetySnapshot(
            status = LookupStatus.FAILED,
            queriedAtEpochMs = System.currentTimeMillis(),
            errorMessage = "Vehicle SDK 미초기화",
            telltalesSummary = "조회 실패"
        )

        val result = withTimeoutOrNull(timeoutMs) {
            val safety = sdk.getSafety()
            val telltalesText = suspendCancellableCoroutine { cont ->
                val done = AtomicBoolean(false)
                safety.getTelltalesInfo(
                    onComplete = { telltales ->
                        if (done.compareAndSet(false, true)) {
                            cont.resume(telltales.toString().take(200))
                        }
                    },
                    onFailed = { error ->
                        if (done.compareAndSet(false, true)) {
                            cont.resume("경고등 조회 실패: ${error.message}")
                        }
                    }
                )
            }

            val airbagText = suspendCancellableCoroutine { cont ->
                val done = AtomicBoolean(false)
                safety.checkAirbagDeployedCapabilities(
                    onComplete = { _, _ ->
                        if (done.compareAndSet(false, true)) cont.resume("에어백 API 지원")
                    },
                    onFailed = { error ->
                        if (done.compareAndSet(false, true)) {
                            cont.resume("에어백 미지원/미확인 (${error.message})")
                        }
                    }
                )
            }

            val suspected = suspectKinds(telltalesText)
            val failed = telltalesText.startsWith("경고등 조회 실패")
            VehicleSafetySnapshot(
                telltalesSummary = telltalesText,
                airbagSummary = airbagText,
                suspectedKinds = suspected,
                status = if (failed) LookupStatus.FAILED else LookupStatus.SUCCESS,
                queriedAtEpochMs = System.currentTimeMillis(),
                errorMessage = if (failed) telltalesText else null
            )
        }

        return result ?: VehicleSafetySnapshot(
            status = LookupStatus.FAILED,
            queriedAtEpochMs = System.currentTimeMillis(),
            errorMessage = "차량 상태 조회 타임아웃",
            telltalesSummary = "응답 없음"
        )
    }

    data class VehicleIdentity(
        val vin: String? = null,
        val fuelKind: FuelKind = FuelKind.UNKNOWN,
        val status: LookupStatus = LookupStatus.IDLE,
        val detail: String = ""
    )

    suspend fun fetchVehicleIdentity(timeoutMs: Long = 8_000): VehicleIdentity {
        val sdk = vehicle ?: return VehicleIdentity(
            status = LookupStatus.FAILED,
            detail = "Vehicle SDK 미초기화"
        )

        return withTimeoutOrNull(timeoutMs) {
            val vin = suspendCancellableCoroutine { cont ->
                val done = AtomicBoolean(false)
                sdk.getCarInfo().getVINInfo(
                    onComplete = { value ->
                        if (done.compareAndSet(false, true)) cont.resume(value)
                    },
                    onFailed = {
                        if (done.compareAndSet(false, true)) cont.resume(null)
                    }
                )
            }

            val evSupported = suspendCancellableCoroutine { cont ->
                val done = AtomicBoolean(false)
                sdk.getEvBattery().checkEvBatteryStateOfChargeCapabilities(
                    onComplete = { if (done.compareAndSet(false, true)) cont.resume(true) },
                    onFailed = { if (done.compareAndSet(false, true)) cont.resume(false) }
                )
            }

            val fuelSupported = suspendCancellableCoroutine { cont ->
                val done = AtomicBoolean(false)
                sdk.getFuel().checkFuelPercentCapabilities(
                    onComplete = { if (done.compareAndSet(false, true)) cont.resume(true) },
                    onFailed = { if (done.compareAndSet(false, true)) cont.resume(false) }
                )
            }

            val fuelKind = when {
                evSupported && fuelSupported -> FuelKind.HYBRID
                evSupported -> FuelKind.EV
                fuelSupported -> FuelKind.GASOLINE // 세부는 사용자가 가솔린/디젤 선택
                else -> FuelKind.UNKNOWN
            }

            val cleanVin = sanitizeVin(vin)
            VehicleIdentity(
                vin = cleanVin,
                fuelKind = fuelKind,
                status = LookupStatus.SUCCESS,
                detail = buildString {
                    append(if (cleanVin == null) "VIN 미제공/무효" else "VIN 조회됨")
                    append(" · 연료추정 ${fuelKind.name}")
                    append(" (구동방식 FWD/RWD/AWD는 제원 API 미제공 시 수동 선택)")
                }
            )
        } ?: VehicleIdentity(
            status = LookupStatus.FAILED,
            detail = "차량 식별 정보 타임아웃"
        )
    }

    private fun sanitizeVin(raw: String?): String? {
        val trimmed = raw?.trim()?.trim('"')?.trim('\'')?.trim().orEmpty()
        if (trimmed.isBlank()) return null
        val blocked = setOf("invalid", "unknown", "null", "none", "n/a", "na")
        if (trimmed.lowercase() in blocked) return null
        if (trimmed.length < 5) return null
        return trimmed
    }

    private fun suspectKinds(telltales: String): List<AccidentKind> {
        val text = telltales.lowercase()
        val result = linkedSetOf<AccidentKind>()
        if (listOf("airbag", "crash", "collision", "impact", "에어백", "충돌", "추돌").any { text.contains(it) }) {
            result += AccidentKind.REAR_END
            result += AccidentKind.SIDE_CONTACT
        }
        if (listOf("fire", "smoke", "화재", "연기").any { text.contains(it) }) {
            result += AccidentKind.VEHICLE_FIRE
            result += AccidentKind.SMOKE
        }
        if (listOf("battery", "ev", "배터리", "고전압", "charge").any { text.contains(it) }) {
            result += AccidentKind.EV_BATTERY_FIRE
        }
        if (listOf("temp", "overheat", "과열", "엔진", "hood").any { text.contains(it) }) {
            result += AccidentKind.ENGINE_OVERHEAT
        }
        if (listOf("fuel", "leak", "연료", "누출").any { text.contains(it) }) {
            result += AccidentKind.FUEL_LEAK
        }
        return result.toList()
    }
}
