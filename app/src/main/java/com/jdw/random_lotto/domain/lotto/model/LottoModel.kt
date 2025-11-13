package com.jdw.random_lotto.domain.lotto.model

import androidx.compose.runtime.Immutable
import com.jdw.random_lotto.common.util.LottoType

/**
 * 로또 모델
 * @param id - DB 아이디
 * @param number - 번호 리스트
 * @param type - 복권 종류
 * @param createdAt - 생성 시각 (epoch milli)
 * @param signature - 문자열로 파싱
 */
@Immutable
data class LottoModel(
    val id: Long = 0,
    val number: List<Int>,
    val type: LottoType,
    val createdAt: Long = 0L,
    val signature: String = number.joinToString("")
)

// LottoModel을 LottoResultModel로 변환하는 확장 함수
fun LottoModel.toResult(
    evaluation: Evaluation = Evaluation.Unchecked
) = LottoResultModel(
    id = id,
    number = number,
    type = type,
    createdAt = createdAt,
    evaluation = evaluation
)
