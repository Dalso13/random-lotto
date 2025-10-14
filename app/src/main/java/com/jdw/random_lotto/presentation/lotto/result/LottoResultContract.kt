package com.jdw.random_lotto.presentation.lotto.result

import com.jdw.random_lotto.domain.lotto.model.LottoModel

// 상태
data class LottoResultState(
    val selectItems: List<LottoModel> = emptyList(),                // 조회된 항목들
    val isLoading: Boolean = false,                                 // 로딩 상태
    val error: String? = null                                       // 에러 메시지
)

// intent (동작)
sealed interface LottoResultIntent {
    data class Select(val type: String) : LottoResultIntent              // 로드 (DB에서 불러오기)
}

// ui effect (일회성 이벤트)
sealed interface LottoResultEffect {
    data class ShowSnackbar(val message: String) : LottoResultEffect      // 스낵바 표시
}