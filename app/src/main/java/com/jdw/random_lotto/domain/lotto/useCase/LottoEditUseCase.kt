package com.jdw.random_lotto.domain.lotto.useCase

import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.domain.lotto.model.LottoModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LottoEditUseCase @Inject constructor() {
    fun generate(type: LottoType): LottoModel {
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

    /**
     * 일반 복권 자동 생성기
     * @return 1~45 사이의 중복되지 않는 6개의 숫자 리스트 (오름차순 정렬)
     */
    private fun generateRandomStandardLottoNumber(): List<Int> {
        val numbers = (1..45).shuffled().take(6)
        return numbers
    }

    /**
     * 연금 복권 자동 생성기
     * @return 조(1~5) + 0~9 사이의 중복되지 않는 5개의 숫자 리스트 (오름차순 정렬)
     */
    private fun generateRandomAnnuityLottoNumber(): List<Int> {
        // 조 고르기 (1~5)
        val group = (1..5).random()

        // 번호 5개 고르기 (중복 가능) (0~9)
        val numbers = MutableList(5) { (0..9).random() }

        // 그룹을 첫번째에 추가
        numbers.add(0, group)
        return numbers
    }
}