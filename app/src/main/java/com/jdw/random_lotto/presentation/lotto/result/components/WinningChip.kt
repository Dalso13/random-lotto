package com.jdw.random_lotto.presentation.lotto.result.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.presentation.main.components.CapsuleChip
import com.jdw.random_lotto.presentation.main.components.NumberCapsule
import com.jdw.random_lotto.presentation.main.components.NumbersFlow
import com.jdw.random_lotto.presentation.main.components.rememberCapsuleStyle

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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (selectedTab == LottoType.ANNUITY) {
            // 조 배지 → 공통 CapsuleChip 사용
            CapsuleChip(
                text = "조 ${winning.annuityGroup ?: 1}",
                style = rememberCapsuleStyle(emphasis = true)
            )
        }

        NumbersFlow(
            numbers = winning.main,
            emphasisAll = true,               // 당첨번호 메인 강조
            isBonus = { false }               // 메인에서는 보너스 없음
        )

        winning.bonus?.let { b ->
            Text("＋", color = cs.onSurfaceVariant)
            NumberCapsule(number = b, isBonus = true)
        }
    }
}