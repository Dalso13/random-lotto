package com.jdw.random_lotto.domain.lotto.useCase

import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.Segment
import com.jdw.random_lotto.domain.core.useCase.BlockingResultUseCase
import com.jdw.random_lotto.domain.core.useCase.BlockingUseCase
import com.jdw.random_lotto.domain.lotto.model.AnnuityWinningModel
import com.jdw.random_lotto.domain.lotto.model.Evaluation
import com.jdw.random_lotto.domain.lotto.model.LottoModel
import com.jdw.random_lotto.domain.lotto.model.LottoResultModel
import com.jdw.random_lotto.domain.lotto.model.StandardWinningModel
import com.jdw.random_lotto.domain.lotto.model.toResult
import com.jdw.random_lotto.domain.lotto.model.winningNumbers
import javax.inject.Inject

data class LottoCheckParams(
    val type: LottoType,
    val lottoModels: List<LottoModel>,
    val segment: Segment,
    val standardWinningModel: StandardWinningModel,
    val annuityWinningModel: AnnuityWinningModel
)


interface LottoCheckUseCase: BlockingUseCase<LottoCheckParams, LottoResult<List<LottoResultModel>>>

class LottoCheckUseCaseImpl @Inject constructor() : BlockingResultUseCase<LottoCheckParams, List<LottoResultModel>>(), LottoCheckUseCase {

    override fun execute(params: LottoCheckParams): LottoResult<List<LottoResultModel>> {
        val (type, lottoModels, segment ,standardWinningModel, annuityWinningModel) = params

        when (type) {
            LottoType.STANDARD -> {
                val results = checkStandardLottoResult(lottoModels, standardWinningModel)
                return LottoResult.Success(results)
            }
            LottoType.ANNUITY -> {
                val results = checkAnnuityLottoResult(lottoModels, annuityWinningModel)
                return LottoResult.Success(results)
            }
        }

    }

    /**
     * 6/45 복권 당첨 결과 확인
     * @param list 사용자가 가진 로또 모델들
     * @param winningModel 당첨 번호 모델
     * @return 당첨 결과 모델 리스트
     */
    private fun checkStandardLottoResult(
        list: List<LottoModel>,
        winningModel: StandardWinningModel
    ): List<LottoResultModel> {

        // 당첨 메인번호/보너스 준비
        val winning = winningModel.winningNumbers()
        val winningSet = winning.toSet()
        val bonus = winningModel.bonus

        return list.map { ticket ->
            val nums = ticket.number

            // 메인 6개 기준 일치 인덱스 수집(티켓 내 인덱스 0~5)
            val matchIndices = nums.mapIndexedNotNull { idx, n ->
                if (n in winningSet) idx else null
            }
            val matchCount = matchIndices.size

            // 보너스 포함 여부 (등수 판정에만 사용, 인덱스에는 포함 X)
            val hasBonus = bonus in nums

            // 등수 결정
            val rank = determineRank(matchCount, hasBonus)

            if (rank != null) {
                // 당첨
                ticket.toResult(
                    evaluation = Evaluation.Win(
                        rank = rank,
                        matchIndices = matchIndices
                    )
                )
            } else {
                // 낙첨
                ticket.toResult(
                    evaluation = Evaluation.Lose
                )
            }
        }
    }

    // --- 등수 판정 (보너스 없음) ---
    private fun determineAnnuityRank(
        ticketGroup: Int,
        ticketDigits: List<Int>,
        winGroup: Int,
        winDigits: List<Int>
    ): Int? {
        // 1등: 조+6자리 모두 일치
        if (ticketGroup == winGroup && ticketDigits == winDigits) return 1

        // 2~7등: 오른쪽부터 n자리 연속 일치 (조 무관)
        return when {
            ticketDigits.endsWithN(6, winDigits) -> 2
            ticketDigits.endsWithN(5, winDigits) -> 3
            ticketDigits.endsWithN(4, winDigits) -> 4
            ticketDigits.endsWithN(3, winDigits) -> 5
            ticketDigits.endsWithN(2, winDigits) -> 6
            ticketDigits.endsWithN(1, winDigits) -> 7
            else -> null
        }
    }

    /**
     * 연금 복권 720+ 당첨 결과 확인 (보너스 없음)
     * LottoModel.number = [group, d1, d2, d3, d4, d5, d6] 가정
     * winningRate: 1~7, 낙첨은 null
     * winningRateIndex: 일치 포지션 인덱스(조=0, 자릿수=1..6)
     */
    private fun checkAnnuityLottoResult(
        list: List<LottoModel>,
        winningModel: AnnuityWinningModel
    ): List<LottoResultModel> {

        requireValidWinning(winningModel)

        val winGroup = winningModel.group
        val winDigits = winningModel.winningNumbers()

        return list.map { ticket ->
            val nums = ticket.number

            // 유효성: [조, d1..d6]
            val valid = nums.size == 7 &&
                    nums[0] in 1..5 &&
                    nums.drop(1).size == 6 &&
                    nums.drop(1).all { it in 0..9 }

            if (!valid) {
                return@map ticket.toResult(
                    evaluation = Evaluation.Unchecked
                )
            }

            val tGroup = nums[0]
            val tDigits = nums.drop(1) // 6자리

            val rank = determineAnnuityRank(tGroup, tDigits, winGroup, winDigits)

            if (rank == null) {
                // 낙첨
                ticket.toResult(
                    evaluation = Evaluation.Lose
                )
            } else {
                ticket.toResult(
                    evaluation = Evaluation.Win(
                        rank = rank,
                        matchIndices = matchedIndicesForAnnuity(rank) ?: emptyList()
                    )
                )
            }
        }
    }


}

// --- 헬퍼---
/**
 * 등수 판정 (일반 6/45)
 * @param matchCount 메인 6개 당첨번호와 일치한 개수
 * @param hasBonus   사용자의 번호 중 보너스 포함 여부
 * @return 1~5등 또는 null(낙첨)
 */
private fun determineRank(matchCount: Int, hasBonus: Boolean): Int? = when {
    matchCount == 6 -> 1
    matchCount == 5 && hasBonus -> 2
    matchCount == 5 -> 3
    matchCount == 4 -> 4
    matchCount == 3 -> 5
    else -> null
}

/**
 * 연금 복권 등수별 일치 인덱스 반환
 * @param rank 1~7등 또는 null
 * @return 일치 인덱스 리스트 또는 null
 */
private fun matchedIndicesForAnnuity(rank: Int?): List<Int>? = when (rank) {
    1 -> (0..6).toList()  // 조+6자리
    2 -> (1..6).toList()  // 6자리
    3 -> (2..6).toList()  // 5자리
    4 -> (3..6).toList()
    5 -> (4..6).toList()
    6 -> (5..6).toList()
    7 -> listOf(6)        // 마지막 1자리
    else -> null
}

/**
 * 연금 복권 당첨번호 유효성 검사
 * @param model 연금 복권 당첨 모델
 */
private fun requireValidWinning(model: AnnuityWinningModel) {
    require(model.group in 1..5) { "group must be 1..5" }
    val main = model.winningNumbers()
    require(main.size == 6 && main.all { it in 0..9 }) { "digits must be 6 numbers in 0..9" }
}

// --- List<Int> 확장 함수 ---
/**
 * 리스트가 오른쪽부터 N개가 대상 리스트와 동일한지 확인
 * @param n 대상 개수
 * @param target 비교 대상 리스트
 * @return 동일하면 true
 */
private fun List<Int>.endsWithN(n: Int, target: List<Int>): Boolean {
    if (n <= 0) return true
    val from = this.size - n
    return this.subList(from, this.size) == target.subList(target.size - n, target.size)
}