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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.Segment
import com.jdw.random_lotto.presentation.lotto.result.components.RoundHeader
import com.jdw.random_lotto.presentation.lotto.result.components.TicketCard

// 결과 화면 ----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LottoResultScreen(selectedTab: LottoType, vm: LottoResultViewModel) {
    val cs = MaterialTheme.colorScheme
    val tp = MaterialTheme.typography

    val state by vm.state.collectAsStateWithLifecycle()

    // 상단 세그먼트 (지난 회차/이번 회차)
    var segment by remember { mutableStateOf(Segment.LAST) }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is LottoResultEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is LottoResultEffect.ShowDialog -> {
                    dialogEffect = effect    // 다이얼로그는 상태로 들고 있다가 밑에서 그림
                }
            }
        }
    }

    LaunchedEffect(selectedTab) {
        // 탭 변경 시 데이터 로드
        if (vm.state.value.standardWinningModel != null &&
            vm.state.value.annuityWinningModel != null) {
            vm.dispatch(LottoResultIntent.Load(selectedTab, segment))
        }
    }

    LaunchedEffect(segment) {
        // 세그먼트 변경 시 데이터 로드
        if (vm.state.value.standardWinningModel != null &&
            vm.state.value.annuityWinningModel != null) {
            vm.dispatch(LottoResultIntent.Load(selectedTab, segment))
        }
    }

    // UI -----------------------------------------------------------------
    Column(Modifier.fillMaxSize()) {
        // 상단: 복권 요약/토글
        RoundHeader(
            segment = segment,
            onSelectSegment = { segment = it },
            selectedTab = selectedTab,
            state = state,
        )

        // 리스트
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


