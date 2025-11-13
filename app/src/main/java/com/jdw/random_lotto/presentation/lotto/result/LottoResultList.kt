package com.jdw.random_lotto.presentation.lotto.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.presentation.lotto.result.components.TicketCard
import com.jdw.random_lotto.presentation.lotto.result.components.TicketCardSkeleton

/**
 * 로또 결과 리스트
 * @param selectedTab 현재 선택된 로또 타입 탭
 * @param state 로또 결과 상태
 */
@Composable
fun LottoResultList(
    selectedTab: LottoType,
    state: LottoResultState,
) {
    val cs = MaterialTheme.colorScheme
    val tp = MaterialTheme.typography

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(state.resultItems, key = { it.id }) { item ->
            TicketCard(
                type = selectedTab,
                item = item
            )
        }

        if (state.resultItems.isEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("표시할 항목이 없어요", style = tp.bodyMedium, color = cs.onSurfaceVariant)
                }
            }
        }
        item {
            Spacer(Modifier.height(80.dp))
        }
    }
}

/**
 * 로딩 스켈레톤 리스트
 */

@Composable
fun LottoResultSkeletonList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(6) {
            TicketCardSkeleton()
            Spacer(Modifier.height(8.dp))
        }
    }
}
