package com.jdw.random_lotto.presentation.lotto.history

import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.OrderBy
import com.jdw.random_lotto.domain.lotto.model.LottoHistoryModel

data class LottoHistoryState(
    val page: Int = 0,                                          // 다음에 조회할 페이지
    val type: LottoType? = null,                                // 로또 타입
    val orderBy: OrderBy = OrderBy.CREATED_AT_DESC,             // 정렬 방식
    val historyList: List<LottoHistoryModel> = emptyList(),     // 로또 히스토리 목록
    val isLoading: Boolean = false,                             // 로딩 상태
)

sealed interface LottoHistoryIntent {
    data object Load: LottoHistoryIntent                                // 특정 타입의 히스토리 불러오기
    data class ChangeType(val type: LottoType?) : LottoHistoryIntent    // 로또 타입 변경
    data class ChangeOrderBy(val orderBy: OrderBy) : LottoHistoryIntent // 정렬 방식 변경
}

sealed interface LottoHistoryEffect {
    data class ShowSnackbar(val message: String) : LottoHistoryEffect      // 스낵바 표시
}