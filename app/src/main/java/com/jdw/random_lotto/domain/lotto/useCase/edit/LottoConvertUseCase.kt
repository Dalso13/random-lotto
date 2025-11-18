package com.jdw.random_lotto.domain.lotto.useCase.edit

import com.google.gson.Gson
import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.domain.core.useCase.BlockingResultUseCase
import com.jdw.random_lotto.domain.core.useCase.BlockingUseCase
import com.jdw.random_lotto.domain.lotto.model.LottoModel
import javax.inject.Inject
import javax.inject.Singleton

interface LottoConvertUseCase : BlockingUseCase<String, LottoResult<LottoModel>>


@Singleton
class LottoConvertUseCaseImpl @Inject constructor() : BlockingResultUseCase<String, LottoModel>(), LottoConvertUseCase {
    private val gson = Gson()


    override fun execute(params: String): LottoResult<LottoModel> {
        return LottoResult.Fail("Not implemented yet")
    }
}