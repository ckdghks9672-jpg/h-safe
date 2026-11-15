# 사고한장 (Sagohanjang)

차량 사고 직후 보험사 연결과 통화용 요약을 돕는 Android Automotive / Pleos 앱입니다.

## 모듈

- `:automotive` — 차량 앱 MVP (본 제품)
- `:shared` — 도메인 모델·DataStore
- `:mobile` — 템플릿 유지 (모바일 접수는 준비 중)

## Pleos 키 설정

1. `pleos.local.properties.example`을 복사해 `pleos.local.properties`를 만듭니다.
2. Playground에서 받은 Project ID / Client ID / Client Secret / CRN을 입력합니다.
3. 이 파일은 `.gitignore`에 포함되어 커밋되지 않습니다.

빌드 시 `:automotive`의 `BuildConfig`로 주입됩니다.

## DevBox CRN 주입 (에뮬레이터)

```bash
adb root
adb shell su 0 "echo 'propId: 554696961 areaId: 0 values: <CRN>' > /data/vendor/vsomeip/vhal_fifo"
adb reboot
```

`<CRN>`은 `pleos.local.properties`의 `pleos.crn` 값을 사용합니다.

## 실행

1. Pleos Connect Emulator v2.1.0 (API 34) AVD 실행
2. `:automotive` Run
3. 시연 흐름: 보험 등록 → **사고 대응** → 다이얼/ARS → 시연용 고속도로 요약 → 현장 기록 → 접수번호

## SDK

- Vehicle `2.3.2`
- NaviHelper `3.0.5`
- Maven: `https://nexus-playground.pleos.ai/repository/maven-releases/`
