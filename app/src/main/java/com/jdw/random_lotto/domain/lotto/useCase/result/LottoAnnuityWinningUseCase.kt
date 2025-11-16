package com.jdw.random_lotto.domain.lotto.useCase.result

import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.data.lotto.winning.LottoWinningRepo
import com.jdw.random_lotto.domain.core.useCase.SuspendResultUseCase
import com.jdw.random_lotto.domain.core.useCase.SuspendUseCase
import com.jdw.random_lotto.domain.lotto.model.AnnuityWinningModel
import javax.inject.Inject

// 추상체
interface LottoAnnuityWinningUseCase :
    SuspendUseCase<Unit, LottoResult<AnnuityWinningModel>>

// 구현체
class LottoAnnuityWinningUseCaseImpl @Inject constructor(
    val repo: LottoWinningRepo,
) : SuspendResultUseCase<Unit, AnnuityWinningModel>(), LottoAnnuityWinningUseCase {

    // 연금 복권 당첨 정보 조회
    override suspend fun execute(params: Unit): LottoResult<AnnuityWinningModel>  {
        return repo.fetchAnnuity()
    }
}