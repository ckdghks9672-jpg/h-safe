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

