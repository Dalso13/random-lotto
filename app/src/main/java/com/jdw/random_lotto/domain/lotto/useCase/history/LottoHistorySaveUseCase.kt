package com.jdw.random_lotto.domain.lotto.useCase.history

import com.jdw.random_lotto.common.util.LottoResult
import com.jdw.random_lotto.data.lotto.db.LottoRepo
import com.jdw.random_lotto.data.lotto.db.toEntity
import com.jdw.random_lotto.domain.core.useCase.SuspendResultUseCase
import com.jdw.random_lotto.domain.core.useCase.SuspendUseCase
import com.jdw.random_lotto.domain.lotto.model.LottoHistoryModel

interface LottoHistorySaveUseCase : SuspendUseCase<List<LottoHistoryModel>, LottoResult<Int>>

class LottoHistorySaveUseCaseImpl(
    private val repo: LottoRepo
) : SuspendResultUseCase<List<LottoHistoryModel>, Int>(), LottoHistorySaveUseCase {
    override suspend fun execute(params: List<LottoHistoryModel>): LottoResult<Int> {
        return runCatching {
            repo.addAllHistory(params.map { it.toEntity() })
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