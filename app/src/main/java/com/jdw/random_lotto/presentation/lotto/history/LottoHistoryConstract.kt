package com.jdw.random_lotto.presentation.lotto.history

import com.jdw.random_lotto.domain.lotto.model.LottoHistoryModel

data class LottoHistoryState(
    val historyList: List<LottoHistoryModel> = emptyList(),     // 로또 히스토리 목록
    val isLoading: Boolean = false,                             // 로딩 상태
)

sealed interface LottoHistoryIntent {

}

sealed interface LottoHistoryEffect {
    data class ShowSnackbar(val message: String) : LottoHistoryEffect      // 스낵바 표시
}