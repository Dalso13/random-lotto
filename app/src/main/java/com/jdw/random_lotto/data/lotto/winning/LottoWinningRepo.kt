package com.jdw.random_lotto.data.lotto.winning

import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.domain.lotto.model.AnnuityWinningModel
import com.jdw.random_lotto.domain.lotto.model.StandardWinningModel
import javax.inject.Inject
import javax.inject.Singleton


interface LottoWinningRepo {
    suspend fun fetchStandard(round: Int): LottoResult<StandardWinningModel>
    suspend fun fetchAnnuity(): LottoResult<AnnuityWinningModel>
}

@Singleton
class LottoWinningRepoImpl @Inject constructor(
    val source: LottoWinningSource
) : LottoWinningRepo {

    override suspend fun fetchStandard(round: Int): LottoResult<StandardWinningModel> {
        return when (val r = source.fetchStandard(round)) {
            is LottoResult.Success -> {
                val model = r.value.toModel()
                if (model != null) LottoResult.Success(model)
                else LottoResult.Fail("Invalid data from source: ${r.value}")
            }
            is LottoResult.Fail -> r
        }
    }

    override suspend fun fetchAnnuity(): LottoResult<AnnuityWinningModel>{
        return when (val r = source.fetchAnnuity()) {
            is LottoResult.Success -> {
                val model = r.value.toModel()
                if (model != null) LottoResult.Success(model)
                else LottoResult.Fail("Invalid data from source: ${r.value}")
            }
            is LottoResult.Fail -> r
        }
    }
}