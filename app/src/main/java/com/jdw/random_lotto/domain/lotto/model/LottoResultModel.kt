package com.jdw.random_lotto.domain.lotto.model

import androidx.compose.runtime.Immutable
import com.jdw.random_lotto.common.util.LottoType

/**
 * 로또 당첨 여부 모델
 * @param id - DB 아이디
 * @param number - 번호 리스트
 * @param type - 복권 종류
 * @param createdAt - 생성 시각 (epoch milli)
 * @param evaluation - 당첨 평가 상태
 */
@Immutable
data class LottoResultModel(
    val id: Long = 0,
    val number: List<Int>,
    val type: LottoType,
    val createdAt: Long = 0L,
    val evaluation: Evaluation = Evaluation.Unchecked // 기본값: 미확인
)

/**
 * 로또 당첨 평가 상태
 * @property Win 과거 회차 계산 완료
 * @property Lose 과거 회차 미당첨
 * @property Pending 이번 주 등 발표 전 등: 아직 결과가 ‘없음’이 확정적으로 표현됨
 * @property Unchecked 사용자가 아직 확인 액션을 안했거나, 과거 데이터인데 체크를 안 돌린 상태
 */
sealed interface Evaluation {

    // 당첨시
    data class Win(
        val rank: Int,                 // 1~5
        val matchIndices: List<Int>,     // 0..5
    ) : Evaluation

    // 미당첨시
    data object Lose: Evaluation

    // 당첨결과 발표 전
    data class Pending(
        val drawDate: Long? = null,    // 발표 예정 시각(있으면 표시)
    ) : Evaluation

    // 미확인
    data object Unchecked : Evaluation
}
