package com.jdw.random_lotto.presentation.lotto

import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.domain.lotto.model.LottoModel

// 상태
data class LottoEditState(
    val insertItems: List<LottoModel> = emptyList(),                // 추가된 항목들
    val deselectedKeysByType: Map<LottoType, Set<String>> =         // 선택 해제된 항목들의 키 맵
        LottoType.entries.associateWith { emptySet<String>() },
    val isLoading: Boolean = false,                                 // 로딩 상태
    val error: String? = null                                       // 에러 메시지
)

// intent (동작)
sealed interface LottoEditIntent {
    data class ToggleSelect(val type: LottoType, val keys: String) : LottoEditIntent        // 선택/해제 토글
    data class SelectAll(val type: LottoType, val keys: Set<String>) : LottoEditIntent      // 전체 선택/해제
    data class Edit(val type: LottoType): LottoEditIntent                                   // 추가 (무작위 번호 생성)
    data class Save(val type: LottoType) : LottoEditIntent                                  // 저장 (DB에 삽입)
}

// ui effect (일회성 이벤트)
sealed interface LottoEditEffect {
    data class ShowSnackbar(val message: String) : LottoEditEffect      // 스낵바 표시
}