package com.jdw.random_lotto.presentation.lotto.result.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.presentation.lotto.result.model.WinningUi
import com.jdw.random_lotto.presentation.common.components.CapsuleChip
import com.jdw.random_lotto.presentation.common.components.NumberCapsule
import com.jdw.random_lotto.presentation.common.components.NumbersFlow
import com.jdw.random_lotto.presentation.common.components.rememberCapsuleStyle

@Composable
fun WinningChips(winning: WinningUi) {
    val cs = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 연금복권이면 조 배지 노출 (값 있을 때만)
        winning.annuityGroup?.let { g ->
            CapsuleChip(
                text = "조 $g",
                style = rememberCapsuleStyle(emphasis = true)
            )
        }

        // 메인 당첨번호
        NumbersFlow(
            numbers = winning.main,
            emphasisAll = true,
            isBonus = { false }
        )

        // 6/45 보너스 번호
        winning.bonus?.let { b ->
            Text("＋", color = cs.onSurfaceVariant)
            NumberCapsule(number = b, isBonus = true)
        }
    }
}
