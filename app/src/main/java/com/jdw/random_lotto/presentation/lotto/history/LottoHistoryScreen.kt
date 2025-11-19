package com.jdw.random_lotto.presentation.lotto.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdw.random_lotto.presentation.common.components.LoadingLottie
import com.jdw.random_lotto.presentation.common.components.LottoSkeletonList
import com.jdw.random_lotto.presentation.lotto.history.components.LottoHistorySortBar
import com.jdw.random_lotto.presentation.lotto.history.components.LottoTypeFilterBar

/**
 * 로또 히스토리 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LottoHistoryScreen(
    vm: LottoHistoryViewModel = hiltViewModel(),
    onNavBack: () -> Unit,
) {
    val state by vm.state.collectAsStateWithLifecycle()

    // 리스트 맨 아래 도달 여부 계산
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index

            if (totalItemsCount == 0 || lastVisibleItemIndex == null) {
                false
            } else {
                // 마지막 아이템이 보이기 시작하면 로딩
                lastVisibleItemIndex >= totalItemsCount - 1
            }
        }
    }


    LaunchedEffect(Unit) {

        // 최초 진입 시 데이터 없을시 로드
        if (state.isFirstLoad) {
            vm.dispatch(LottoHistoryIntent.Load)
        }
    }

    // 맨 아래까지 스크롤 됐을 때 DB 조회 트리거
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !state.isLoading) {
            vm.dispatch(LottoHistoryIntent.Load)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("당첨 이력") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(
                        onClick = { onNavBack() }
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            LottoTypeFilterBar(
                selectedType = state.type,
                onTypeChange = { newType ->
                    vm.dispatch(LottoHistoryIntent.ChangeType(newType))
                }
            )

            LottoHistorySortBar(
                orderBy = state.orderBy,
                onOrderChange = { newOrder ->
                    vm.dispatch(LottoHistoryIntent.ChangeOrderBy(newOrder))
                }
            )

            if (state.isLoading && state.isFirstLoad) {
                LottoSkeletonList()
            } else {
                LottoHistoryListScreen(
                    list = state.historyList
                )
            }

            if (state.isLoading && !state.isFirstLoad) {
                // 추가 로딩 표시
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingLottie(
                        modifier = Modifier.size(120.dp)
                    )
                }
            }
        }
    }

}