package com.jdw.random_lotto.presentation.lotto.result.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.LottoType

data class WinningSet(
    val round: Int,
    val drawDate: String,          // "10/12" 등
    val main: List<Int>,           // 6개(6/42), 연금복권은 필요 수만
    val bonus: Int? = null,        // 6/42 보너스
    val annuityGroup: Int? = null  // 연금복권 '조'
)


@Composable
fun WinningChips(winning: WinningSet, selectedTab: LottoType) {
    val cs = MaterialTheme.colorScheme
    val tp = MaterialTheme.typography

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (selectedTab == LottoType.ANNUITY) {
            Chip(text = "조 ${winning.annuityGroup ?: 1}", filled = true)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            winning.main.forEach { n ->
                NumberChip(n, emphasis = true)
            }
        }
        winning.bonus?.let { b ->
            Text("＋", color = cs.onSurfaceVariant)
            NumberChip(b, emphasis = false, isBonus = true)
        }
    }
}

@Composable
fun NumberChip(
    num: Int,
    emphasis: Boolean,
    isBonus: Boolean = false,
    matched: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    val tx = when {
        matched -> cs.onPrimary
        emphasis -> cs.onPrimary
        else -> cs.onSurface
    }
    val bg = when {
        matched -> cs.primary
        isBonus -> cs.tertiaryContainer
        emphasis -> cs.primary
        else -> cs.surfaceVariant
    }
    val bd = if (matched) cs.primary else cs.outline.copy(.3f)

    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, bd, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text("%02d".format(num), color = tx, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun Chip(text: String, filled: Boolean = false) {
    val cs = MaterialTheme.colorScheme
    val bg = if (filled) cs.primary.copy(.12f) else cs.surfaceVariant
    val bd = if (filled) cs.primary.copy(.35f) else cs.outline.copy(.4f)
    val tx = if (filled) cs.primary else cs.onSurfaceVariant
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, bd, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, color = tx, style = MaterialTheme.typography.labelMedium)
    }
}