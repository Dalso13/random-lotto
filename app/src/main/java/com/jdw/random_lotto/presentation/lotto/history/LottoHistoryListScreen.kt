package com.jdw.random_lotto.presentation.lotto.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jdw.random_lotto.domain.lotto.model.LottoHistoryModel
import com.jdw.random_lotto.presentation.common.components.LoadingLottie
import com.jdw.random_lotto.presentation.lotto.history.components.LottoHistoryTicketCard
import kotlinx.coroutines.flow.distinctUntilChanged
/**
 * 로또 당첨 이력 리스트
 * @param list 표시할 로또 당첨 이력 목록
 * @param isLoading 로딩 중 여부
 */
@Composable
fun LottoHistoryListScreen(
    list: List<LottoHistoryModel>,
    isLoading: Boolean,
    load: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val tp = MaterialTheme.typography

    // 리스트 상태 기억
    val listState = rememberLazyListState()

    // 더 로드해야 하는지 여부 계산
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()

            if (totalItemsCount == 0 || lastVisibleItem == null) {
                false
            } else {
                lastVisibleItem.index >= totalItemsCount - 1
            }
        }
    }

    // 리스트 스크롤에 따른 무한 로드 처리
    val latestIsLoading by rememberUpdatedState(isLoading)
    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .collect { needLoad ->
                if (needLoad && !latestIsLoading) {
                    load()
                }
            }
    }

    LazyColumn(
        state = listState,
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
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingLottie(
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
