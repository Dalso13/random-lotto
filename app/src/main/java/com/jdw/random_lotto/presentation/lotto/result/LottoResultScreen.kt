package com.jdw.random_lotto.presentation.lotto.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.presentation.lotto.result.components.MyTicket
import com.jdw.random_lotto.presentation.lotto.result.components.RoundHeader
import com.jdw.random_lotto.presentation.lotto.result.components.TicketCard
import com.jdw.random_lotto.presentation.lotto.result.components.WinningSet

enum class Segment { LAST, CURRENT }

// 결과 화면 ----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LottoResultScreen(selectedTab: LottoType) {
    val cs = MaterialTheme.colorScheme
    val tp = MaterialTheme.typography

    // 1) 상단 세그먼트 (지난 회차/이번 회차)
    var segment by remember { mutableStateOf(Segment.LAST) }

    // 2) 더미 데이터 (뷰모델 연결 시 교체)
    val dummy = remember(selectedTab) { buildDummyData(selectedTab) }

    // 3) 필터/정렬 (UI 전용)
    var winnersOnly by remember { mutableStateOf(false) }

    // 4) 매칭 계산 + 정렬/필터
    val rows = remember(segment, winnersOnly, dummy) {
        val isLast = segment == Segment.LAST
        val winning = if (isLast) dummy.winning else null
        val list = dummy.myTickets.map { t ->
            val result = winning?.let { evalMatch(selectedTab, t, it) }
            TicketRowUi(t, result)
        }.let { computed ->
            val filtered = if (winnersOnly) computed.filter { it.result?.rank != null } else computed
            filtered.sortedByDescending { it.result?.score ?: -1 }
        }
        list
    }

    // UI -----------------------------------------------------------------
    Column(Modifier.fillMaxSize()) {
        // 상단: 복권 요약/토글
        RoundHeader(
            segment = segment,
            onSelectSegment = { segment = it },
            selectedTab = selectedTab,
            dummy = dummy,
            winnersOnly = winnersOnly,
            onToggleWinners = { winnersOnly = !winnersOnly },
        )

        // 리스트
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp)
        ) {
            items(rows, key = { it.ticket.id }) { row ->
                TicketCard(
                    type = selectedTab,
                    ticket = row.ticket,
                    result = row.result
                )
            }

            if (rows.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("표시할 항목이 없어요", style = tp.bodyMedium, color = cs.onSurfaceVariant)
                    }
                }
            }
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}


// --- 컴포넌트들 -----------------------------------------------------------

// --- 더미/계산 로직 (뷰모델로 대체 예정) ----------------------------------

data class TicketRowUi(
    val ticket: MyTicket,
    val result: TicketResult?
)

data class TicketResult(
    val score: Int,           // 일치 개수 (보너스 포함 가중치 등 임의)
    val matched: Set<Int>,    // 일치한 숫자
    val bonusMatched: Boolean,
    val rank: Int?,           // 1~5 등 (null = 미당첨)
    val rankText: String,
    val summary: String
)

data class ResultDummy(
    val winning: WinningSet,
    val myTickets: List<MyTicket>,
    val nextDrawText: String
)

fun buildDummyData(type: LottoType): ResultDummy {
    return when (type) {
        LottoType.STANDARD -> {
            val winning = WinningSet(
                round = 1082,
                drawDate = "10/12",
                main = listOf(3, 12, 19, 25, 33, 41),
                bonus = 7
            )
            val tickets = listOf(
                MyTicket("a1", "티켓 A", "10/10", listOf(2, 12, 19, 25, 33, 41)),
                MyTicket("b2", "티켓 B", "10/09", listOf(1, 8, 14, 27, 35, 42)),
                MyTicket("c3", "티켓 C", "10/08", listOf(3, 7, 19, 22, 33, 41)),
                MyTicket("d4", "티켓 D", "10/07", listOf(3, 12, 19, 25, 33, 41)), // 6개 일치 (더미)
            )
            ResultDummy(
                winning = winning,
                myTickets = tickets,
                nextDrawText = "10/19 20:45"
            )
        }

        LottoType.ANNUITY -> {
            val winning = WinningSet(
                round = 582,
                drawDate = "10/12",
                main = listOf(1, 5, 9, 14, 22, 30), // 예시
                bonus = null,
                annuityGroup = 3
            )
            val tickets = listOf(
                MyTicket(
                    "x1",
                    "연금 A",
                    "10/11",
                    numbers = listOf(1, 5, 9, 14, 22, 31),
                    annuityGroup = 3
                ),
                MyTicket(
                    "y2",
                    "연금 B",
                    "10/10",
                    numbers = listOf(2, 6, 9, 15, 23, 30),
                    annuityGroup = 1
                ),
                MyTicket(
                    "z3",
                    "연금 C",
                    "10/09",
                    numbers = listOf(1, 5, 9, 14, 22, 30),
                    annuityGroup = 3
                ),
            )
            ResultDummy(
                winning = winning,
                myTickets = tickets,
                nextDrawText = "10/19 20:45"
            )
        }
    }
}

fun evalMatch(type: LottoType, ticket: MyTicket, winning: WinningSet): TicketResult {
    return when (type) {
        LottoType.STANDARD -> {
            val main = winning.main.toSet()
            val matched = ticket.numbers.filter { it in main }.toSet()
            val bonusMatched = winning.bonus != null && ticket.numbers.contains(winning.bonus)
            val matchCount = matched.size

            // 간단 랭크 규칙 (실제 규칙과 동일)
            val rank = when {
                matchCount == 6 -> 1
                matchCount == 5 && bonusMatched -> 2
                matchCount == 5 -> 3
                matchCount == 4 -> 4
                matchCount == 3 -> 5
                else -> null
            }
            val rankText = rank?.let { "${it}등" } ?: "미당첨"
            val summary = buildString {
                append("${matchCount}개 일치")
                if (bonusMatched) append(" + 보너스")
                if (rank != null) append(" → ${rank}등")
                else append(" · 미당첨")
            }
            // score: 정렬용 (보너스 가중치 살짝)
            val score = matchCount * 10 + if (bonusMatched) 1 else 0
            TicketResult(score, matched, bonusMatched, rank, rankText, summary)
        }

        LottoType.ANNUITY -> {
            val main = winning.main.toSet()
            val groupOk = ticket.annuityGroup != null && ticket.annuityGroup == winning.annuityGroup
            val matched = ticket.numbers.filter { it in main }.toSet()
            val matchCount = matched.size

            // (더미 규칙) 조가 같고 숫자 N개 이상 맞으면 등수
            val rank = when {
                groupOk && matchCount >= 6 -> 1
                groupOk && matchCount >= 5 -> 2
                groupOk && matchCount >= 4 -> 3
                matchCount >= 4 -> 4
                matchCount >= 3 -> 5
                else -> null
            }
            val rankText = rank?.let { "${it}등" } ?: "미당첨"
            val summary = buildString {
                append("${matchCount}개 일치")
                if (groupOk) append(" · 조 일치")
                if (rank != null) append(" → ${rank}등") else append(" · 미당첨")
            }
            val score = matchCount * 10 + if (groupOk) 2 else 0
            TicketResult(score, matched, bonusMatched = false, rank, rankText, summary)
        }
    }
}


