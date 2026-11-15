package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.dialer.ClaimDialer
import com.example.myapplication.dialer.OutreachHelpers
import com.example.myapplication.pleos.NaviLocationClient
import com.example.myapplication.pleos.VehicleSafetyClient
import com.example.myapplication.shared.CallSummary
import com.example.myapplication.shared.data.IncidentRepository
import com.example.myapplication.shared.data.InsuranceCatalog
import com.example.myapplication.shared.data.ProfileRepository
import com.example.myapplication.shared.model.AccidentKind
import com.example.myapplication.shared.model.DataSource
import com.example.myapplication.shared.model.FieldRecord
import com.example.myapplication.shared.model.FuelKind
import com.example.myapplication.shared.model.Incident
import com.example.myapplication.shared.model.LookupStatus
import com.example.myapplication.shared.model.ResponseMode
import com.example.myapplication.shared.model.TriState
import com.example.myapplication.shared.model.UserProfile
import com.example.myapplication.ux.DrivingRestrictionMonitor
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FlowStep {
    Intake
}

data class AppUiState(
    val profile: UserProfile = UserProfile(),
    val incident: Incident? = null,
    val drivingRestricted: Boolean = false,
    val statusMessage: String? = null,
    val sdkReady: Boolean = false,
    val vehicleApiHint: String = "",
    val flowStep: FlowStep = FlowStep.Intake,
    val responseMode: ResponseMode = ResponseMode.NONE
)

class SagohanjangViewModel(application: Application) : AndroidViewModel(application) {
    private val profileRepository = ProfileRepository(application)
    private val incidentRepository = IncidentRepository(application)
    private val naviClient = NaviLocationClient(application)
    private val vehicleClient = VehicleSafetyClient(application)
    private val uxMonitor = DrivingRestrictionMonitor(application)

    private val statusMessage = MutableStateFlow<String?>(null)
    private val sdkReady = MutableStateFlow(false)
    private val vehicleApiHint = MutableStateFlow("")
    private val flowStep = MutableStateFlow(FlowStep.Intake)
    private val responseMode = MutableStateFlow(ResponseMode.NONE)

    private data class Core(
        val profile: UserProfile,
        val incident: Incident?,
        val restricted: Boolean,
        val status: String?,
        val ready: Boolean
    )

    val uiState: StateFlow<AppUiState> = combine(
        combine(
            profileRepository.profile,
            incidentRepository.activeIncident,
            uxMonitor.restricted,
            statusMessage,
            sdkReady
        ) { profile, incident, restricted, status, ready ->
            Core(profile, incident, restricted, status, ready)
        },
        vehicleApiHint,
        flowStep,
        responseMode
    ) { core, hint, step, mode ->
        AppUiState(
            profile = core.profile,
            incident = core.incident,
            drivingRestricted = core.restricted,
            statusMessage = core.status,
            sdkReady = core.ready,
            vehicleApiHint = hint,
            flowStep = step,
            responseMode = mode
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppUiState())

    init {
        uxMonitor.start()
        viewModelScope.launch {
            val naviOk = naviClient.initialize()
            val vehicleOk = vehicleClient.initialize()
            sdkReady.value = naviOk || vehicleOk
            if (vehicleOk) refreshVehicleDefaultsFromApi()
        }
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            profileRepository.save(mergeApiDefaults(profile).copy(onboardingCompleted = true))
        }
    }

    fun clearProfile() {
        viewModelScope.launch {
            profileRepository.clear()
            incidentRepository.clearActive()
            flowStep.value = FlowStep.Intake
            responseMode.value = ResponseMode.NONE
            statusMessage.value = null
        }
    }

    fun startResponse(mode: ResponseMode) {
        viewModelScope.launch {
            responseMode.value = mode
            flowStep.value = FlowStep.Intake
            statusMessage.value = null
            val incident = Incident(
                id = UUID.randomUUID().toString(),
                openedAtEpochMs = System.currentTimeMillis(),
                mode = mode,
                accidentKind = when (mode) {
                    ResponseMode.FIRE_119 -> AccidentKind.VEHICLE_FIRE
                    else -> AccidentKind.UNKNOWN
                }
            )
            incidentRepository.saveActive(incident)
            launchLookups(incident)
        }
    }

    fun selectAccidentKind(kind: AccidentKind) {
        viewModelScope.launch {
            val current = uiState.value.incident ?: return@launch
            incidentRepository.saveActive(current.copy(accidentKind = kind))
        }
    }

    fun updateLocationNote(
        isHighway: Boolean? = null,
        addressLine: String? = null,
        nearbyPlace: String? = null,
        roadName: String? = null,
        direction: String? = null,
        kilometerPost: String? = null,
        spokenSummary: String? = null
    ) {
        viewModelScope.launch {
            val current = uiState.value.incident ?: return@launch
            val loc = current.location.copy(
                isHighway = isHighway ?: current.location.isHighway,
                addressLine = addressLine ?: current.location.addressLine,
                nearbyPlace = nearbyPlace ?: current.location.nearbyPlace,
                roadName = roadName ?: current.location.roadName,
                direction = direction ?: current.location.direction,
                kilometerPost = kilometerPost ?: current.location.kilometerPost,
                spokenSummary = spokenSummary ?: current.location.spokenSummary,
                status = LookupStatus.SUCCESS,
                source = DataSource.USER
            )
            val highway = loc.isHighway
            incidentRepository.saveActive(
                current.copy(
                    location = loc,
                    highwayOrExpressway = highway
                )
            )
        }
    }

    fun confirmLocationAndContinue() {
        flowStep.value = FlowStep.Intake
    }

    fun confirmVehicleAndContinue() {
        flowStep.value = FlowStep.Intake
    }

    fun updateMovableTow(movable: TriState? = null, tow: TriState? = null) {
        viewModelScope.launch {
            val current = uiState.value.incident ?: return@launch
            incidentRepository.saveActive(
                current.copy(
                    movable = movable ?: current.movable,
                    towNeeded = tow ?: current.towNeeded
                )
            )
        }
    }

    fun dialInsurerRepresentative() {
        viewModelScope.launch {
            val profile = uiState.value.profile
            val phone = profile.representativePhone(InsuranceCatalog.companies)
            val company = profile.insurerName(InsuranceCatalog.companies)
            if (phone.isBlank()) {
                statusMessage.value = "선택한 보험사 대표번호가 없습니다."
                return@launch
            }
            val dial = ClaimDialer.dial(getApplication(), phone)
            statusMessage.value = "$company $phone\n${dial.message}\n통화창이 덮으면 이 앱으로 돌아와 접수 요약을 읽으세요."
            uiState.value.incident?.let {
                incidentRepository.saveActive(it.copy(dialAttempted = true))
            }
        }
    }

    fun dial119() {
        viewModelScope.launch {
            val dial = ClaimDialer.dial(getApplication(), "119")
            statusMessage.value = "119 전화 연결\n${dial.message}"
            uiState.value.incident?.let {
                incidentRepository.saveActive(it.copy(dialAttempted = true))
            }
        }
    }

    fun send119EmergencyMessage() {
        viewModelScope.launch {
            val state = uiState.value
            val incident = state.incident ?: return@launch
            val body = CallSummary.emergency119Message(state.profile, incident)
            val result = OutreachHelpers.sendSms(getApplication(), "119", body)
            statusMessage.value = result.message
        }
    }

    fun send119FireMessage() {
        viewModelScope.launch {
            val state = uiState.value
            val incident = state.incident ?: return@launch
            val body = CallSummary.fire119Message(state.profile, incident)
            val result = OutreachHelpers.sendSms(getApplication(), "119", body)
            statusMessage.value = result.message
        }
    }

    fun openOnlineIntakePlaceholder() {
        statusMessage.value = "온라인 접수: 준비 중 (보험사 API 연동 후 활성화)"
    }

    fun openMessageIntakePlaceholder() {
        viewModelScope.launch {
            val body = messagePreview()
            val result = OutreachHelpers.shareText(getApplication(), "H-safe 접수 요약", body)
            statusMessage.value = if (result.launched) {
                "메시지 접수용 공유를 열었습니다. (정식 보험사 문자 접수는 준비 중)"
            } else result.message
        }
    }

    fun applyDemoHighway() {
        viewModelScope.launch {
            updateLocationNote(
                isHighway = true,
                roadName = "경부고속도로",
                direction = "서울 방면",
                kilometerPost = "50km",
                spokenSummary = "경부고속도로 서울 방면 · 50km 지점"
            )
            val current = uiState.value.incident ?: return@launch
            val demoSafety = current.vehicleSafety.copy(
                telltalesSummary = "airbag crash collision hood battery overheat",
                suspectedKinds = listOf(AccidentKind.REAR_END, AccidentKind.SIDE_CONTACT, AccidentKind.EV_BATTERY_FIRE),
                status = LookupStatus.SUCCESS
            )
            incidentRepository.saveActive(
                current.copy(
                    vehicleSafety = demoSafety,
                    accidentKind = AccidentKind.REAR_END,
                    movable = TriState.NO,
                    towNeeded = TriState.YES
                )
            )
        }
    }

    fun updateFieldRecord(record: FieldRecord) {
        viewModelScope.launch {
            val current = uiState.value.incident ?: return@launch
            incidentRepository.saveActive(current.copy(fieldRecord = record))
        }
    }

    fun messagePreview(): String {
        val state = uiState.value
        val incident = state.incident ?: return ""
        return when (state.responseMode) {
            ResponseMode.EMERGENCY_119 -> CallSummary.emergency119Message(state.profile, incident)
            ResponseMode.FIRE_119 -> CallSummary.fire119Message(state.profile, incident)
            ResponseMode.TRAFFIC -> CallSummary.trafficClaimPreview(state.profile, incident)
            ResponseMode.NONE -> ""
        }
    }

    fun insurerPhone(): String =
        uiState.value.profile.representativePhone(InsuranceCatalog.companies)

    private fun launchLookups(incident: Incident) {
        viewModelScope.launch {
            launch {
                val location = naviClient.fetchCurrentLocation()
                val latest = uiState.value.incident ?: incident
                incidentRepository.saveActive(latest.copy(location = location))
            }
            launch {
                val safety = vehicleClient.fetchSafetySnapshot()
                val latest = uiState.value.incident ?: incident
                val kind = if (latest.accidentKind == AccidentKind.UNKNOWN && safety.suspectedKinds.size == 1) {
                    safety.suspectedKinds.first()
                } else latest.accidentKind
                incidentRepository.saveActive(latest.copy(vehicleSafety = safety, accidentKind = kind))
            }
            launch { refreshVehicleDefaultsFromApi() }
        }
    }

    private suspend fun refreshVehicleDefaultsFromApi() {
        val identity = vehicleClient.fetchVehicleIdentity()
        vehicleApiHint.value = identity.detail
        val profile = uiState.value.profile
        val merged = mergeApiDefaults(profile, identity)
        if (merged != profile) profileRepository.save(merged)
    }

    private fun mergeApiDefaults(
        profile: UserProfile,
        identity: VehicleSafetyClient.VehicleIdentity? = null
    ): UserProfile {
        var next = profile
        identity?.vin
            ?.takeIf { profile.vehicleVin.isBlank() && it.isNotBlank() }
            ?.let { next = next.copy(vehicleVin = it) }
        if (next.vehicleVin.equals("invalid", ignoreCase = true) ||
            next.vehicleVin.equals("unknown", ignoreCase = true)
        ) {
            next = next.copy(vehicleVin = "")
        }
        if (identity != null && profile.fuelKind == FuelKind.UNKNOWN && identity.fuelKind != FuelKind.UNKNOWN) {
            next = next.copy(fuelKind = identity.fuelKind)
        }
        // DriveKind은 현재 Vehicle SDK에 공개 제원 API가 없어 수동 선택 유지
        return next
    }

    override fun onCleared() {
        uxMonitor.stop()
        naviClient.release()
        vehicleClient.release()
        super.onCleared()
    }
}
