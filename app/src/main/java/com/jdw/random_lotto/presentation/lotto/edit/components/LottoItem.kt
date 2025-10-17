package com.jdw.random_lotto.presentation.lotto.edit.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.domain.lotto.model.LottoModel
import com.jdw.random_lotto.presentation.main.components.CapsuleChip
import com.jdw.random_lotto.presentation.main.components.NumbersFlow
import com.jdw.random_lotto.presentation.main.components.rememberCapsuleStyle

/**
 * 리스트 아이템
 * @param item - 복권 모델
 * @param selected - 선택됨 여부
 * @param onToggle - 선택/해제 토글 콜백
 */
@Composable
fun LottoItem(
    item: LottoModel,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val cs = colorScheme

    val bg by animateColorAsState(if (selected) cs.primaryContainer else cs.surface, label = "bg")
    val border by animateColorAsState(
        if (selected) cs.primary.copy(alpha = .35f) else cs.outline.copy(
            alpha = .6f
        ), label = "bd"
    )
    val elev by animateDpAsState(if (selected) 2.dp else 0.dp, label = "elev")

    Surface(
        onClick = onToggle,
        color = bg,
        tonalElevation = elev,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, border),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // 연금: 조 배지
            if (item.type == LottoType.ANNUITY) {
                val group = item.number.firstOrNull() ?: 0
                CapsuleChip(
                    text = "조 $group",
                    style = rememberCapsuleStyle( /* 필요시 강조/선택 반영 */
                        selected = selected
                    )
                )
                Spacer(Modifier.width(12.dp))
            }

            // 숫자 칩들 (연금이면 첫 자리 제외)
            val digits by remember(item) {
                derivedStateOf {
                    if (item.type == LottoType.ANNUITY) item.number.drop(1) else item.number
                }
            }

            // 공통 숫자 나열 유틸로 일관되게 렌더
            NumbersFlow(
                numbers = digits,
                modifier = Modifier.weight(1f),
                matched = emptySet(),        // 지난 회차 비교 시에는 매칭 세트 전달
                emphasisAll = false,
                selected = selected,
                maxItemsInEachRow = 6
            )


            AnimatedVisibility(visible = selected) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = cs.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}