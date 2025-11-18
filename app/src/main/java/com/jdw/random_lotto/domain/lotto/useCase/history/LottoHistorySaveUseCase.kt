package com.jdw.random_lotto.domain.lotto.useCase.history

import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.data.lotto.db.LottoRepo
import com.jdw.random_lotto.data.lotto.db.toEntity
import com.jdw.random_lotto.domain.core.useCase.SuspendResultUseCase
import com.jdw.random_lotto.domain.core.useCase.SuspendUseCase
import com.jdw.random_lotto.domain.lotto.model.LottoResultModel
import com.jdw.random_lotto.domain.lotto.model.toHistoryModel

interface LottoHistorySaveUseCase : SuspendUseCase<Pair<List<LottoResultModel>, Int>, LottoResult<Int>>

class LottoHistorySaveUseCaseImpl(
    private val repo: LottoRepo
) : SuspendResultUseCase<Pair<List<LottoResultModel>, Int>, Int>(), LottoHistorySaveUseCase {
    override suspend fun execute(params: Pair<List<LottoResultModel>, Int>): LottoResult<Int> {
        val (list, round) = params
        val historyList = list.mapNotNull { it.toHistoryModel(round) }

        if (historyList.isEmpty()) return LottoResult.Success(0)

        return runCatching {
            repo.addAllHistory(historyList.map { it.toEntity() })
        }.fold(
            onSuccess = { data ->
                LottoResult.Success(data.size)
            },
            onFailure = { e ->
                LottoResult.Fail("히스토리 저장 실패: ${e.message}")
            }
        )
    }
}