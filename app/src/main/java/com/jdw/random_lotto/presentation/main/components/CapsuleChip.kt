package com.jdw.random_lotto.presentation.main.components

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

@Immutable
data class CapsuleStyle(
    val container: Color,
    val content: Color,
    val border: Color
)

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

@Composable
fun rememberCapsuleStyle(
    emphasis: Boolean = false,   // 당첨번호/헤더 강조
    matched: Boolean = false,    // 일치 번호 강조
    isBonus: Boolean = false,    // 보너스 번호
    selected: Boolean = false    // 리스트 선택 상태
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

