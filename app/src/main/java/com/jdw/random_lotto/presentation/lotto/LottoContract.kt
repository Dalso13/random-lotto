package com.jdw.random_lotto.presentation.lotto

import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.domain.lotto.model.LottoModel

// 상태
data class LottoState(
    val selectItems: List<LottoModel> = emptyList(),
    val insertItems: List<LottoModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// intent (동작)
sealed interface LottoIntent {
    data class Observe(val type: LottoType) : LottoIntent
    data class Remove(val id: Long) : LottoIntent
    data class Clear(val type: LottoType) : LottoIntent
    data class Edit(val type: LottoType): LottoIntent
}

// ui effect (일회성 이벤트)
sealed interface LottoEffect {
    data class ShowSnackbar(val message: String) : LottoEffect      // 스낵바 표시
}