package com.example.myapplication.shared.data

import com.example.myapplication.shared.model.InsuranceCompany

object InsuranceCatalog {
    val companies: List<InsuranceCompany> = listOf(
        company("samsungfire", "삼성화재", "삼성", "1588-5114", 0xFF1428A0, "자동차 사고접수 1번", "긴급출동·고장출동 메뉴 확인"),
        company("hyundai", "현대해상", "현대", "1588-5656", 0xFFC8102E, "자동차 사고접수 2번", "긴급출동·고장출동 메뉴 확인"),
        company("db", "DB손해보험", "DB", "1588-0100", 0xFF00A3E0, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("kb", "KB손해보험", "KB", "1544-0114", 0xFFFFCC00, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("meritz", "메리츠화재", "메리츠", "1566-7711", 0xFFEE3124, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("hanwha", "한화손해보험", "한화", "1566-8000", 0xFFF37321, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("lotte", "롯데손해보험", "롯데", "1588-3344", 0xFFE60012, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("heungkuk", "흥국화재", "흥국", "1688-1688", 0xFF004B87, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("axa", "AXA손해보험", "AXA", "1566-1566", 0xFF00008F, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("hana", "하나손해보험", "하나", "1566-3000", 0xFF008485, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("carrot", "캐롯손해보험", "캐롯", "1566-0300", 0xFFFF6A00, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("nh", "농협손해보험", "NH", "1644-9000", 0xFF0B6E4F, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("yebyeol", "예별손해보험(舊MG)", "예별", "1588-5959", 0xFF5B2C6F, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("aig", "AIG손해보험", "AIG", "1544-2792", 0xFF0033A0, "자동차 사고접수 메뉴 확인", "긴급출동 메뉴 확인"),
        company("kakao", "카카오페이손해보험", "카카오", "1599-1114", 0xFFFAE100, "앱/콜센터 안내 확인", "긴급출동 메뉴 확인"),
        company("custom", "직접 입력", "직접", "", 0xFF607D8B, "실제 ARS에서 자동차 사고접수·긴급출동 선택", null)
    )

    fun find(id: String): InsuranceCompany? = companies.firstOrNull { it.id == id }

    private fun company(
        id: String,
        name: String,
        shortLabel: String,
        phone: String,
        color: Long,
        ars: String,
        roadside: String?
    ) = InsuranceCompany(
        id = id,
        name = name,
        shortLabel = shortLabel,
        claimPhone = phone,
        logoColorArgb = color,
        arsClaimMenu = ars,
        arsRoadsideMenu = roadside,
        sourceNote = "손해보험협회·각사 공식 안내 기준",
        verifiedOn = "2026-11-14"
    )
}
