package com.jdw.random_lotto.domain.lotto.model

import androidx.compose.runtime.Immutable
import com.jdw.random_lotto.common.util.LottoType

/**
 * 로또 당첨 여부 모델
 * @param id - DB 아이디
 * @param number - 번호 리스트
 * @param type - 복권 종류
 * @param createdAt - 생성 시각 (epoch milli)
 * @param isWinning - 당첨 여부
 * @param winningRate - 당첨 등수 (1~5), 미당첨 시 null
 * @param winningRateIndex - 당첨 번호 인덱스 리스트 (0~5), 미당첨 시 null
 */
@Immutable
data class LottoResultModel(
    val id: Long = 0,
    val number: List<Int>,
    val type: LottoType,
    val createdAt: Long = 0L,
    val isWinning: Boolean,
    val winningRate: Int?,
    val winningRateIndex: List<Int>?
)

