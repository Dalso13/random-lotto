package com.jdw.random_lotto.domain.lotto.model

import com.jdw.random_lotto.common.util.LottoType

/**
 * 로또 히스토리 모델
 * @param id - DB 아이디
 * @param round - 회차
 * @param number - 번호 리스트
 * @param type - 복권 종류
 * @param sourceCreatedAt - 원본 생성 시각
 * @param rowCreatedAt - 행 생성 시각
 * @param rank - 당첨 등수
 */
data class LottoHistoryModel(
    val id: Long = 0,
    val round: Int,
    val number: List<Int>,
    val type: LottoType,
    val sourceCreatedAt: Long,
    val rowCreatedAt: Long = 0L,
    val rank: Int,
)

/**
 * LottoResultModel을 LottoHistoryModel로 변환
 * 당첨된 경우에만 변환, 낙첨인 경우 null 반환
 */
fun LottoResultModel.toHistoryModel(round: Int): LottoHistoryModel? {
    if (this.evaluation !is Evaluation.Win) {
        return null
    }
    return LottoHistoryModel(
        round = round,
        number = this.number,
        type = this.type,
        sourceCreatedAt = this.createdAt,
        rank = this.evaluation.rank
    )
}