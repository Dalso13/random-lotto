package com.jdw.random_lotto.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/**
 * 숫자 캡슐 플로우
 * @param numbers - 표시할 숫자 리스트
 * @param modifier - Modifier
 * @param matched - 일치하는 번호 집합 (강조용)
 * @param emphasisAll - 모든 번호 강조
 * @param isBonus - 보너스 번호 여부 판단 람다 (보너스는 강조 색상 다름)
 * @param selected - 선택 상태 (선택된 항목 강조)
 * @param maxItemsInEachRow - 한 행에 표시할 최대 아이템 수
 */
@Composable
fun NumbersFlow(
    numbers: List<Int>,
    modifier: Modifier = Modifier,
    matched: Set<Int> = emptySet(),
    emphasisAll: Boolean = false,
    isBonus: ((Int) -> Boolean)? = null,
    selected: Boolean = false,
    maxItemsInEachRow: Int = 6,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = maxItemsInEachRow
    ) {
        numbers.forEach { n ->
            NumberCapsule(
                number = n,
                emphasis = emphasisAll,
                matched = n in matched,
                isBonus = isBonus?.invoke(n) == true,
                selected = selected
            )
        }
    }
}

/**
 * 캡슐 칩
 * @param text - 표시할 텍스트
 * @param modifier - Modifier
 * @param style - 캡슐 스타일
 * @param padding - 내부 여백
 * @param shape - 모양 (기본은 완전 둥근 캡슐)
 * @param textStyle - 텍스트 스타일
 */
@Composable
fun CapsuleChip(
    text: String,
    modifier: Modifier = Modifier,
    style: CapsuleStyle = rememberCapsuleStyle(),
    padding: PaddingValues = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
    shape: RoundedCornerShape = RoundedCornerShape(999.dp),
    textStyle: TextStyle = MaterialTheme.typography.labelMedium
) {
    Box(
        modifier
            .clip(shape)
            .background(style.container)
            .border(1.dp, style.border, shape)
            .padding(padding)
    ) {
        Text(text, color = style.content, style = textStyle)
    }
}

/**
 * 숫자 캡슐
 * @param number - 표시할 숫자
 * @param emphasis - 당첨번호/헤더 강조
 * @param matched - 일치 번호 강조
 * @param isBonus - 보너스 번호 여부
 * @param selected - 선택 상태 (선택된 항목 강조)
 */
@Composable
fun NumberCapsule(
    number: Int,
    emphasis: Boolean = false,
    matched: Boolean = false,
    isBonus: Boolean = false,
    selected: Boolean = false
) {
    val style = rememberCapsuleStyle(
        emphasis = emphasis, matched = matched, isBonus = isBonus, selected = selected
    )
    CapsuleChip(
        text = "%02d".format(number),
        style = style,
        textStyle = MaterialTheme.typography.labelLarge
    )
}

/**
 * 캡슐색 정의 데이터
 * @param container - 배경
 * @param content - 글자색
 * @param border - 테두리
 */
@Immutable
data class CapsuleStyle(
    val container: Color,
    val content: Color,
    val border: Color
)

/**
 * 캡슐 스타일 기억
 * @param emphasis - 당첨번호/헤더 강조
 * @param matched - 일치 번호 강조
 * @param isBonus - 보너스 번호
 * @param selected - 리스트 선택 상태
 */
@Composable
fun rememberCapsuleStyle(
    emphasis: Boolean = false,
    matched: Boolean = false,
    isBonus: Boolean = false,
    selected: Boolean = false
): CapsuleStyle {
    val cs = MaterialTheme.colorScheme
    val container = when {
        matched -> cs.primary
        isBonus -> cs.tertiaryContainer
        emphasis -> cs.primary
        selected -> cs.background.copy(alpha = .7f)
        else -> cs.surfaceVariant.copy(alpha = .7f)
    }
    val content = when {
        matched || emphasis -> cs.onPrimary
        isBonus -> cs.onTertiaryContainer
        selected -> cs.onBackground
        else -> cs.onSurfaceVariant
    }
    val border = when {
        matched -> cs.primary
        isBonus -> cs.tertiary
        selected -> cs.primary.copy(.25f)
        emphasis -> cs.primary.copy(.35f)
        else -> cs.outline.copy(.4f)
    }
    return CapsuleStyle(container, content, border)
}

