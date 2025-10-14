package com.jdw.random_lotto.presentation.lotto.result.components

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.presentation.lotto.result.TicketResult
import com.jdw.random_lotto.presentation.main.components.CapsuleChip
import com.jdw.random_lotto.presentation.main.components.NumbersFlow
import com.jdw.random_lotto.presentation.main.components.rememberCapsuleStyle

data class MyTicket(
    val id: String,
    val title: String,
    val savedAt: String,
    val numbers: List<Int>,        // 6개 (연금복권은 조+번호 형태면 넘겨줄 때 가공)
    val annuityGroup: Int? = null,
)

@Composable
fun TicketCard(
    type: LottoType,
    ticket: MyTicket,
    result: TicketResult?
) {
    val cs = MaterialTheme.colorScheme
    val tp = MaterialTheme.typography

    val matchedNums = result?.matched ?: emptySet()
    val isWin = result?.rank != null

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
                    result?.rankText ?: "",
                    style = tp.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.weight(1f))
                Text(ticket.savedAt, style = tp.labelMedium, color = cs.onSurfaceVariant)
            }

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (type == LottoType.ANNUITY) {
                    val group = ticket.annuityGroup ?: 0
                    CapsuleChip(
                        text = "조 $group",
                        style = rememberCapsuleStyle(emphasis = false) // 필요 시 강조 true
                    )
                    Spacer(Modifier.width(12.dp))
                }

// digits: 연금이면 첫 자리(조) 제외, 아니면 그대로
                val digits by remember(ticket.numbers, type) {
                    mutableStateOf(
                        if (type == LottoType.ANNUITY) ticket.numbers.drop(1) else ticket.numbers
                    )
                }

// 공통 숫자 나열 유틸 사용
                NumbersFlow(
                    numbers = digits,
                    matched = matchedNums,          // 지난 회차 매칭 강조용
                    emphasisAll = false,            // 당첨번호 강조 아님(개별 매칭만 강조)
                    isBonus = { false },            // 필요 시 보너스 기준 넣기
                    maxItemsInEachRow = 6
                )

            }

            // 결과 요약
            result?.let { safe ->
                Spacer(Modifier.height(8.dp))
                Text(
                    safe.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (safe.rank != null) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }
    }
}
