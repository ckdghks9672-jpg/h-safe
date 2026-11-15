package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

/** 현대차 톤 + 안전 컬러 (차계열 네이티브에 가깝게) */
object HSafeColors {
    val HyundaiBlue = Color(0xFF002C5F)
    val HyundaiNavy = Color(0xFF0B1F33)
    val HyundaiSky = Color(0xFF00AAD2)
    val SafetyYellow = Color(0xFFFFC107)
    val SafetyAmber = Color(0xFFFFB300)
    val EmergencyRed = Color(0xFFB71C1C)
    val FireOrange = Color(0xFFBF360C)
    val CabinGray = Color(0xFFE9EDF2)
    val Panel = Color(0xFFF7F9FB)
    val SoftCard = Color(0xFFFFFFFF)
    val Ink = Color(0xFF101820)
    val Muted = Color(0xFF4A5A6A)
    val Line = Color(0xFFCDD6E0)
    val Recommend = Color(0xFF1B5E20)
}

/**
 * 현대산스 KR은 공식 공개 배포가 없어,
 * 공개 라이선스(Pretendard)로 한국어 UI를 구성하고
 * 영문/숫자 헤드라인에 Hyundai Sans Head를 보조 적용합니다.
 */
val HSafeFontFamily = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.hyundai_sans_head_medium, FontWeight.Medium)
)

private val LightColors = lightColorScheme(
    primary = HSafeColors.HyundaiBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEAF4),
    onPrimaryContainer = HSafeColors.HyundaiBlue,
    secondary = HSafeColors.SafetyYellow,
    onSecondary = HSafeColors.Ink,
    secondaryContainer = Color(0xFFFFF4CC),
    onSecondaryContainer = Color(0xFF4A3800),
    tertiary = HSafeColors.HyundaiSky,
    error = HSafeColors.EmergencyRed,
    errorContainer = Color(0xFFFFEBEE),
    background = HSafeColors.CabinGray,
    surface = HSafeColors.SoftCard,
    surfaceVariant = HSafeColors.Panel,
    onBackground = HSafeColors.Ink,
    onSurface = HSafeColors.Ink,
    onSurfaceVariant = HSafeColors.Muted,
    outline = HSafeColors.Line
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = HSafeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
        lineHeight = 62.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = HSafeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 46.sp,
        lineHeight = 52.sp
    ),
    titleLarge = TextStyle(
        fontFamily = HSafeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 38.sp,
        lineHeight = 44.sp
    ),
    titleMedium = TextStyle(
        fontFamily = HSafeFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 38.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = HSafeFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = HSafeFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    labelLarge = TextStyle(
        fontFamily = HSafeFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp
    )
)

@Composable
fun SagohanjangTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
