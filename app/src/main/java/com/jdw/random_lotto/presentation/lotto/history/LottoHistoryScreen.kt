package com.jdw.random_lotto.presentation.lotto.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdw.random_lotto.presentation.common.components.LottoSkeletonList
import com.jdw.random_lotto.presentation.common.effect.AppSnackbar
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        // 최초 진입 시 데이터 없을시 로드
        if (state.isFirstLoad) {
            vm.dispatch(LottoHistoryIntent.Load)
        }
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is LottoHistoryEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { AppSnackbar(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("당첨 이력") },
                navigationIcon = {
                    IconButton(
                        onClick = { onNavBack() }
                    ) {
                        Icon(
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
            Column(
                modifier = Modifier.fillMaxSize()
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

                if (state.isLoading && state.historyList.isEmpty()) {
                    LottoSkeletonList()
                } else {
                    LottoHistoryListScreen(
                        list = state.historyList,
                        isLoading = state.isLoading
                    ) {
                        vm.dispatch(LottoHistoryIntent.Load)
                    }
                }
            }
        }
    }

}