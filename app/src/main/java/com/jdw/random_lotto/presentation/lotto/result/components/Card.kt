package com.jdw.random_lotto.presentation.lotto.result.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.presentation.lotto.result.TicketResult

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
                    Chip("조 ${ticket.annuityGroup ?: 1}", filled = false)
                    Spacer(Modifier.width(12.dp))
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ticket.numbers.forEachIndexed { idx, n ->
                        // 연금복권은 첫 숫자가 '조'라면 제외하고 칩 렌더
                        val isMatched = matchedNums.contains(n)
                        NumberChip(
                            num = n,
                            emphasis = false,
                            matched = isMatched
                        )
                    }
                }
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
