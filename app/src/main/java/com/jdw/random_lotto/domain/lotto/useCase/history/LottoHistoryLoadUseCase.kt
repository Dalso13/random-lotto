package com.jdw.random_lotto.domain.lotto.useCase.history

import com.jdw.random_lotto.domain.core.useCase.SuspendUseCase
import com.jdw.random_lotto.domain.lotto.model.LottoHistoryModel

data class LottoHistoryLoadParams(
    val type: String,
    val orderBy: String,
    val page: Int,
    val pageSize: Int
)

interface LottoHistoryLoadUseCase : SuspendUseCase<LottoHistoryLoadParams, List<LottoHistoryModel>>
