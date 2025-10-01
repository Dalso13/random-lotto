package com.jdw.random_lotto.domain.lotto.useCase

import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.domain.lotto.model.LottoModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LottoEditUseCase @Inject constructor() {
    fun execute(type: LottoType): LottoModel {
        // 로또 타입에 따라 무작위 번호 생성 및
        val num = when (type) {
            LottoType.STANDARD -> {
                generateRandomStandardLottoNumber()
            }
            LottoType.ANNUITY -> {
                generateRandomAnnuityLottoNumber()
            }
        }

        return LottoModel(
            number = num,
            type = type
        )
    }

    private fun generateRandomStandardLottoNumber(): String {
        val numbers = (1..45).shuffled().take(6).sorted()
        return numbers.joinToString(", ")
    }

    private fun generateRandomAnnuityLottoNumber(): String {
        val numbers = (1..30).shuffled().take(5).sorted()
        return numbers.joinToString(", ")
    }
}