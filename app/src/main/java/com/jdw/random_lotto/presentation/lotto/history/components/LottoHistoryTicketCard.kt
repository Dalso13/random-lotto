package com.jdw.random_lotto.presentation.lotto.history.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.domain.lotto.model.LottoHistoryModel
import com.jdw.random_lotto.presentation.common.components.CapsuleChip
import com.jdw.random_lotto.presentation.common.components.NumbersFlow
import com.jdw.random_lotto.presentation.common.components.rememberCapsuleStyle
import com.jdw.random_lotto.presentation.common.util.toMmDd
import com.jdw.random_lotto.presentation.lotto.result.model.EvalBadge
import com.jdw.random_lotto.presentation.lotto.result.model.toEvalUi

@Composable
fun LottoHistoryTicketCard(
    type: LottoType,
    item: LottoHistoryModel
) {
    val cs = MaterialTheme.colorScheme
    val tp = MaterialTheme.typography

    // 표시용 날짜
    val savedAt = remember(item.sourceCreatedAt) { item.sourceCreatedAt.toMmDd() }

    // 연금 복권 숫자/조 파싱 (numbers 첫값이 조일 수도/아닐 수도 있다고 가정)
    val (annuityGroup, digits) = remember(type, item.number) {
        if (type == LottoType.ANNUITY) {
            // 규칙:
            // - 7개면 [조, d1..d6]
            // - 6개면 조 없음
            if (item.number.size >= 7) {
                item.number.first() to item.number.drop(1)
            } else null to item.number
        } else null to item.number
    }

    // 평가(Evaluation) → UI 매핑
    val evalUi = remember(item) { item.toEvalUi() }

    val isWin = evalUi.rank != null

    Surface(
        shape = RoundedCornerShape(14.dp),
        tonalElevation = if (isWin) 1.dp else 0.dp,
        border = BorderStroke(1.dp, if (isWin) cs.primary.copy(.35f) else cs.outline.copy(.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(Modifier.padding(14.dp)) {

            // 상단 타이틀/메모
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    evalUi.rankText,
                    style = tp.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = when (evalUi.badge) {
                        EvalBadge.WIN -> cs.primary
                        EvalBadge.LOSE -> cs.onSurface
                        EvalBadge.PENDING -> cs.secondary
                        EvalBadge.UNCHECKED -> cs.onSurfaceVariant
                    }
                )
                Spacer(Modifier.weight(1f))
                Text(savedAt, style = tp.labelMedium, color = cs.onSurfaceVariant)
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (type == LottoType.ANNUITY && annuityGroup != null) {
                    CapsuleChip(
                        text = "조 $annuityGroup",
                        style = rememberCapsuleStyle(emphasis = false)
                    )
                    Spacer(Modifier.width(12.dp))
                }

                // 일치 숫자 강조(지난 회차에서만 의미가 있을 수 있지만, 평가가 Win/Lose면 항상 표시 안전)
                NumbersFlow(
                    numbers = digits,
                    matched = evalUi.matched,
                    emphasisAll = false,
                    isSpecial = { false },
                    maxItemsInEachRow = 6
                )
            }

            // 결과 요약
            Spacer(Modifier.height(8.dp))
            Text(
                evalUi.summary,
                style = tp.bodyMedium,
                color = when (evalUi.badge) {
                    EvalBadge.WIN -> cs.primary
                    EvalBadge.LOSE -> cs.onSurfaceVariant
                    EvalBadge.PENDING -> cs.onSurfaceVariant
                    EvalBadge.UNCHECKED -> cs.onSurfaceVariant
                }
            )
        }
    }
}