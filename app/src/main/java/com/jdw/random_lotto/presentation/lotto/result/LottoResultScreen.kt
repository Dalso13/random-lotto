package com.jdw.random_lotto.presentation.lotto.result

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
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
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.Segment
import com.jdw.random_lotto.presentation.common.effect.AppDialog
import com.jdw.random_lotto.presentation.common.effect.AppSnackbar
import com.jdw.random_lotto.presentation.lotto.result.components.RoundHeader

// 결과 화면 ----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LottoResultScreen(selectedTab: LottoType, vm: LottoResultViewModel) {

    // 상태
    val state by vm.state.collectAsStateWithLifecycle()

    // 상단 세그먼트 (지난 회차/이번 회차)
    var segment by remember { mutableStateOf(Segment.LAST) }

    // Effect 처리: 스낵바/다이얼로그 등 일회성
    val snackbarHostState = remember { SnackbarHostState() }
    var dialogEffect by remember { mutableStateOf<LottoResultEffect.ShowDialog?>(null) }


    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is LottoResultEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is LottoResultEffect.ShowDialog -> {
                    dialogEffect = effect
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        // 최초 진입 시 데이터 로드
        if (state.isTryInit && (vm.state.value.standardWinningModel == null || vm.state.value.annuityWinningModel == null)) {
            vm.dispatch(LottoResultIntent.reInit)
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
    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {

            // 당첨번호 헤더
            RoundHeader(
                segment = segment,
                onSelectSegment = { segment = it },
                selectedTab = selectedTab,
                state = state,
            )

            // 리스트
            if (state.isLoading) {
                LottoResultSkeletonList()
            } else {
                LottoResultList(
                    state = state,
                    selectedTab = selectedTab,
                )
            }
        }

        // 스낵바
        AppSnackbar(
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

    // 다이얼로그
    AppDialog(
        visible = dialogEffect != null,
        message = dialogEffect?.message.orEmpty(),
        confirmText = dialogEffect?.confirmText,
        cancelText = dialogEffect?.cancelText,
        onConfirm = {
            dialogEffect?.confirmIntent?.let(vm::dispatch)
        },
        onCancel = {
            dialogEffect?.cancelIntent?.let(vm::dispatch)
        },
        onDismissRequest = { dialogEffect = null }
    )
}


