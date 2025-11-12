package com.jdw.random_lotto.presentation.lotto.result

import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.Segment
import com.jdw.random_lotto.domain.lotto.model.AnnuityWinningModel
import com.jdw.random_lotto.domain.lotto.model.LottoResultModel
import com.jdw.random_lotto.domain.lotto.model.StandardWinningModel

// 상태
data class LottoResultState(
    val standardWinningModel: StandardWinningModel? = null,         // 6/45 당첨 모델
    val annuityWinningModel: AnnuityWinningModel? = null,           // 연금 복권 당첨 모델
    val resultItems: List<LottoResultModel> = emptyList(),          // 조회된 로또모델 리스트
    val isLoading: Boolean = false,                                 // 로딩 상태
    val error: String? = null                                       // 에러 메시지
)

// intent (동작)
sealed interface LottoResultIntent {
    data class Load(val type: LottoType, val segment: Segment) : LottoResultIntent              // 로드 (DB에서 불러오기)
    data object reInit : LottoResultIntent                                                      // 재초기화
}

// ui effect (일회성 이벤트)
sealed interface LottoResultEffect {
    data class ShowSnackbar(val message: String) : LottoResultEffect      // 스낵바 표시
    data class ShowDialog(
        val message: String,
        val confirmText: String?,
        val cancelText: String?,
        val confirmIntent: LottoResultIntent?,
        val cancelIntent: LottoResultIntent?
    ) : LottoResultEffect                                                 // 다이얼로그 표시
}