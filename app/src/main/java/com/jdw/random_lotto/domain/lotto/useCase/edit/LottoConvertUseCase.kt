package com.jdw.random_lotto.domain.lotto.useCase.edit

import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.common.util.LottoType
import com.jdw.random_lotto.common.util.qr.QRConverter.convertQrResult
import com.jdw.random_lotto.domain.core.useCase.BlockingResultUseCase
import com.jdw.random_lotto.domain.core.useCase.BlockingUseCase
import com.jdw.random_lotto.domain.lotto.model.LottoModel
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface LottoConvertUseCase : BlockingUseCase<String, LottoResult<List<LottoModel>>>


@Singleton
class LottoConvertUseCaseImpl @Inject constructor() : BlockingResultUseCase<String, List<LottoModel>>(), LottoConvertUseCase {
    override fun execute(params: String): LottoResult<List<LottoModel>> {
        val (type, list) = convertQrResult(params) ?: return LottoResult.Fail("QR 코드 변환 실패")
        if (list == null || list.isEmpty()) {
            return LottoResult.Fail("QR 코드에서 로또 번호를 찾을 수 없습니다.")
        }

        // 로또 타입에 따른 번호 개수 설정
        val typeSize = if (type == LottoType.STANDARD) 6 else 7

        Timber.d("LottoEditViewModel QR Result - Type: $type, Lists: $list")

        // 유효한 로또 번호로 변환
        val numbers = list.mapNotNull { numberList ->
            val number = numberList.toLottoNumbers()
            if (numberList.size != typeSize) {
                null
            } else {
                LottoModel(
                    type = type,
                    number = number
                )
            }
        }

        return if (numbers.isNotEmpty()) {
            LottoResult.Success(numbers)
        } else {
            LottoResult.Fail("유효한 로또 번호가 없습니다.")
        }
    }
}

private fun List<String>.toLottoNumbers(): List<Int> {
    return this.mapNotNull { it.toIntOrNull() }
}