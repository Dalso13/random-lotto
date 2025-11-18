package com.jdw.random_lotto.presentation.lotto.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 로또 히스토리 화면
 */
@Composable
fun LottoHistoryScreen(
    vm: LottoHistoryViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            LottoHistoryListScreen(
                list = state.historyList
            )
        }
    }

}