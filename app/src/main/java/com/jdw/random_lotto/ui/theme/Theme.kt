package com.jdw.random_lotto.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jdw.random_lotto.R

val Inter = FontFamily(
    Font(R.font.inter_regular, weight = FontWeight.Normal),
    Font(R.font.inter_medium, weight = FontWeight.Medium),
    Font(R.font.inter_semibold, weight = FontWeight.SemiBold),
    Font(R.font.inter_bold, weight = FontWeight.Bold)
)

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 45.sp),
    headlineLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 32.sp),
    titleLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 22.sp),
    bodyLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelLarge = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 14.sp)
)

// Brand tokens
val BrandSelected = Color(0xFF006FFD)
val BrandUnselectedLight = Color(0xFFEAF2FF)
val BrandUnselectedDark = Color(0xFF153454) // 어두운 배경에서 비선택 칩용

private val LightColorScheme = lightColorScheme(
    primary = BrandSelected,           // 버튼/강조(선택) 배경
    onPrimary = Color.White,           // 선택 위 텍스트
    primaryContainer = BrandUnselectedLight, // 비선택 배경 칩/탭 등에 활용
    onPrimaryContainer = Color(0xFF083266),  // 비선택 위 텍스트(딥 블루)

    background = Color.White,
    surface = Color.White,
    onBackground = Color(0xFF0D141B),
    onSurface = Color(0xFF0D141B),

    secondary = Color(0xFF295EA6),
    onSecondary = Color.White,
    tertiary = Color(0xFF0061A8),
    onTertiary = Color.White,

    surfaceVariant = Color(0xFFF2F5FA),
    outline = Color(0xFFCBD6E6)
)

// Dark scheme: primary는 동일(또는 살짝 톤다운), container는 어두운 블루
private val DarkColorScheme = darkColorScheme(
    primary = BrandSelected,           // 다크에서도 선택은 선명하게
    onPrimary = Color.White,
    primaryContainer = BrandUnselectedDark, // 다크 비선택 칩/탭 배경
    onPrimaryContainer = Color(0xFFBBD6FF),

    background = Color(0xFF121212),
    surface = Color(0xFF1A1A1A),
    onBackground = Color(0xFFEFEFEF),
    onSurface = Color(0xFFEFEFEF),

    secondary = Color(0xFF7BA7FF),
    onSecondary = Color(0xFF0C1A2A),
    tertiary = Color(0xFF62A4FF),
    onTertiary = Color(0xFF0C1A2A),

    surfaceVariant = Color(0xFF202631),
    outline = Color(0xFF3B4A61)
)
@Composable
fun Random_lottoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
