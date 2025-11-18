package com.jdw.random_lotto.presentation.lotto.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdw.random_lotto.presentation.common.components.LottoSkeletonList

/**
 * 로또 히스토리 화면
 */
@Composable
fun LottoHistoryScreen(
    vm: LottoHistoryViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {

        // 최초 진입 시 데이터 없을시 로드
        if (state.isFirstLoad) {
            vm.dispatch(LottoHistoryIntent.Load)
        }
    }

    Scaffold { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading && state.isFirstLoad) {
                LottoSkeletonList()
            } else {
                LottoHistoryListScreen(
                    list = state.historyList
                )
            }

            if (state.isLoading && !state.isFirstLoad) {
                // 추가 로딩 표시
            }
        }
    }

}