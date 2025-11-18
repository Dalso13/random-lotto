package com.jdw.random_lotto.presentation.lotto.history

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
import com.jdw.random_lotto.domain.lotto.model.LottoHistoryModel
import com.jdw.random_lotto.presentation.lotto.history.components.LottoHistoryTicketCard

/**
 * 로또 당첨 이력 리스트
 * @param list 표시할 로또 당첨 이력 목록
 */
@Composable
fun LottoHistoryListScreen(
    list: List<LottoHistoryModel>,
) {
    val cs = MaterialTheme.colorScheme
    val tp = MaterialTheme.typography

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp)
    ) {
        items(list, key = { it.id }) { item ->
            LottoHistoryTicketCard(
                type = item.type,
                item = item
            )
        }

        if (list.isEmpty()) {
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